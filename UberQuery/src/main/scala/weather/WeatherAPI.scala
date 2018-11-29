package weather

import cats.effect.IO
import com.snowplowanalytics.weather.Errors
import com.snowplowanalytics.weather.providers.darksky.{BlockType, DarkSky, DarkSkyClient, Responses}
import models.{Location, Weather, WeatherModel}
import org.slf4j.LoggerFactory
import weather.WeatherAPIConfig.weatherClient

import scala.util.Properties.envOrElse

/**
  * Weather API wrapper
  */
class WeatherAPI {


  /**
    *
    * @param location to get the weather of
    * @return
    */
  def getCurrentWeather(location: Location): Option[Weather] = {


    // Dark Sky by default sends extra data which is not required. Blocking unnecessary information to reduce latency
    val excludeBlocks = List(BlockType.alerts, BlockType.daily, BlockType.flags, BlockType.minutely, BlockType.hourly)

    //get weather info in sync.
    val weatherInfo: Either[Errors.WeatherError, Responses.DarkSkyResponse] = weatherClient.forecast(location.latitude, location.longitude, exclude = excludeBlocks).unsafeRunSync()
    weatherInfo match {
      case Right(value) => Some(WeatherModel(value, location))

      case Left(value) => println(value.getMessage); None
    }

  }

}

/**
  * Configuration object for Weather API
  */
private object WeatherAPIConfig {
  val weatherClient: DarkSkyClient[IO] = DarkSky.basicClient[IO](envOrElse("WEATHER_API_KEY", "NOT_DEFINED"))
  private val log = LoggerFactory.getLogger(WeatherAPIConfig.getClass)
  log.info("Starting Weather Service")
}