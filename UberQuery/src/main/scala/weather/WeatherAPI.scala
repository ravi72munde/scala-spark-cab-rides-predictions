package weather

import cats.effect.IO
import com.snowplowanalytics.weather.Errors
import com.snowplowanalytics.weather.providers.darksky.{BlockType, DarkSky, Responses}
import models.{Location, Weather, WeatherModel}

/**
  * Weather API wrapper
  */
object WeatherAPI {
  /**
    *
    * @param location to get the weather of
    * @return
    */
  def getCurrentWeather(location: Location): Option[Weather] = {

    val client = DarkSky.basicClient[IO](System.getenv("WEATHER_API_KEY"))

    // Dark Sky by default sends extra data which is not required. Blocking unnecessary information to reduce latency
    val excludeBlocks = List(BlockType.alerts, BlockType.daily, BlockType.flags, BlockType.minutely, BlockType.hourly)

    //get weather info in sync.
    val weatherInfo: Either[Errors.WeatherError, Responses.DarkSkyResponse] = client.forecast(location.latitude, location.longitude, exclude = excludeBlocks).unsafeRunSync()
    weatherInfo match {
      case Right(value) => Some(WeatherModel(value, location))

      case Left(value) => println(value.getMessage); None
    }

  }
}