# weather-sdk

## Introduction

The Weather SDK provides an easy-to-use interface for retrieving real-time weather data using the OpenWeather API. It
supports two modes of operation: on-demand and polling, ensuring flexibility based on your application's needs.

## Installation

### Using Maven

Add the following dependency to your pom.xml file:

```
<dependency>
    <groupId>io.github.akhmatovnurbek</groupId>
    <artifactId>weather-sdk</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Using Gradle

```
dependencies {
    implementation group: 'io.github.akhmatovnurbek', name: 'weather-sdk', version: '1.0.2'
}
```

## Usage

```
public class Main {
    public static void main(String[] args) {
        io.github.weather.WeatherSDK sdk = io.github.weather.WeatherSDK.getInstance("your_api_key", io.github.weather.WeatherSDK.Mode.ON_DEMAND);
    }
}
```

```
try {
    JsonNode weatherData = sdk.getWeather("London");
    System.out.println(weatherData.toPrettyString());
} catch (IOException e) {
    System.err.println("Failed to fetch weather data: " + e.getMessage());
}
```

## Unit Testing

Unit tests are included using JUnit and Mockito.
To run tests, execute:
``` mvn test ```

## Author

Developed by **Akhmatov Nurbek**