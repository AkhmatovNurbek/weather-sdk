import io.github.weather.WeatherSDK;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherSDKTest {

    @Test
    void testInvalidApiKey() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new WeatherSDK("", WeatherSDK.Mode.ON_DEMAND);
        });
        assertEquals("API Key cannot be null or empty", exception.getMessage());
    }
}
