package Weather


import Models.{Location, Weather, WeatherModel}
import cats.effect.IO
import com.snowplowanalytics.weather.Errors
import com.snowplowanalytics.weather.providers.darksky.{DarkSky, Responses}

/**
  * Weather API wrapper
  */
object WeatherAPI  {
  /**
    *
    * @param location to get the weather of
    * @return
    */
  def getCurrentWeather(location: Location): Option[Weather] = {

    val client = DarkSky.basicClient[IO](System.getenv("WEATHER_API_KEY"))
    val weatherInfo: Either[Errors.WeatherError, Responses.DarkSkyResponse] = client.forecast(location.latitude, location.longitude).unsafeRunSync()
    println(weatherInfo)
    weatherInfo match {
      case Right(value) => Some(WeatherModel(value, location))

      case _ => None
    }

  }
}