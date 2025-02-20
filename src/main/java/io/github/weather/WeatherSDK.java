package io.github.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherSDK {
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";
    private static final int CACHE_EXPIRY_MINUTES = 10;
    private static final int MAX_CACHED_CITIES = 10;

    private final String apiKey;
    private final Mode mode;
    private final Map<String, CachedWeather> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();

    public enum Mode {
        ON_DEMAND, POLLING
    }

    public WeatherSDK(String apiKey, Mode mode) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key cannot be null or empty");
        }
        this.apiKey = apiKey;
        this.mode = mode;

        if (mode == Mode.POLLING) {
            scheduler.scheduleAtFixedRate(this::refreshCache, 0, CACHE_EXPIRY_MINUTES, TimeUnit.MINUTES);
        }
    }

    public static synchronized WeatherSDK getInstance(String apiKey, Mode mode) {
        return instances.computeIfAbsent(apiKey, k -> new WeatherSDK(apiKey, mode));
    }

    public static synchronized void removeInstance(String apiKey) {
        WeatherSDK instance = instances.remove(apiKey);
        if (instance != null) {
            instance.shutdown();
        }
    }

    public synchronized JsonNode getWeather(String city) throws IOException {
        if (cache.containsKey(city) && !cache.get(city).isExpired()) {
            return cache.get(city).getData();
        }
        JsonNode data = fetchWeatherData(city);
        cacheWeather(city, data);
        return data;
    }

    private JsonNode fetchWeatherData(String city) throws IOException {
        String requestUrl = String.format(API_URL, city, apiKey);
        HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new IOException("Error fetching weather data: HTTP " + connection.getResponseCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(connection.getInputStream());
    }

    private synchronized void cacheWeather(String city, JsonNode data) {
        if (cache.size() >= MAX_CACHED_CITIES) {
            removeOldestEntry();
        }
        cache.put(city, new CachedWeather(data));
    }

    private synchronized void refreshCache() {
        for (String city : cache.keySet()) {
            try {
                JsonNode newData = fetchWeatherData(city);
                cache.put(city, new CachedWeather(newData));
            } catch (IOException e) {
                System.err.println("Failed to refresh weather data for " + city + ": " + e.getMessage());
            }
        }
    }

    private void removeOldestEntry() {
        String oldestCity = cache.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().timestamp))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (oldestCity != null) {
            cache.remove(oldestCity);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private static class CachedWeather {
        private final JsonNode data;
        private final long timestamp;

        public CachedWeather(JsonNode data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MINUTES * 60 * 1000;
        }

        public JsonNode getData() {
            return data;
        }
    }
}
