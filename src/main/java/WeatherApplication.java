import java.io.IOException;

public class WeatherApplication {
    public static void main(String[] args) throws IOException {

        WeatherSDK weatherSDK = WeatherSDK.getInstance("bd5e378503939ddaee76f12ad7a97608", WeatherSDK.Mode.ON_DEMAND);
        WeatherSDK weatherSDK1 = WeatherSDK.getInstance("bd5e378503939ddaee76f12ad7a97608", WeatherSDK.Mode.ON_DEMAND);
        System.out.println(weatherSDK.getWeather("Tashkent"));
        System.out.println(weatherSDK1.getWeather("Tashkent"));
    }
}
