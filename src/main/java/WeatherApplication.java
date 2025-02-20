import java.io.IOException;

public class WeatherApplication {
    public static void main(String[] args) throws IOException {
        WeatherSDK weatherSDK = new WeatherSDK("your apiKey", WeatherSDK.Mode.ON_DEMAND);
        System.out.println(weatherSDK.getWeather("Tashkent"));
    }
}
