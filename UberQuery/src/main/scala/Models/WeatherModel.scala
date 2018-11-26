package Models

import com.snowplowanalytics.weather.providers.darksky.Responses

/**
  * Weather class that represents weather at a given location
  *
  * @param location
  * @param temp
  * @param humidity
  * @param pressure
  * @param clouds
  * @param rain
  * @param snow
  * @param wind
  * @param timeStamp
  */
case class Weather(location: String, temp: Option[Float], humidity: Option[Float], pressure: Option[Float],
                   clouds: Option[Float], rain: Option[Float], snow: Option[Float], wind: Option[Float], time_stamp: Long)
case class WeatherBatch(weathers: Seq[Weather])

/**
  * Weather model trait
  */
trait WeatherModel[T] {
  def apply(x: T, l: Location): Weather
}

/**
  * Weather model implementation to convert OpenWeatherMap API response to case class
  */
object WeatherModel extends WeatherModel[Responses.DarkSkyResponse] {
  /**
    *
    * @param weatherResponse
    * @param location
    * @return
    */
  def apply(weatherResponse: Responses.DarkSkyResponse, location: Location) = {

    val temp: Option[Float] = try {
      weatherResponse.currently.get.temperature
    }
    catch {
      case e: Exception => None
    }
    val humidiy: Option[Float] = try {
      weatherResponse.currently.get.humidity
    } catch {
      case e: Exception => None
    }
    val pressure: Option[Float] = try {
      weatherResponse.currently.get.pressure
    }
    catch {
      case e: Exception => None
    }

    val clouds: Option[Float] = try {
      weatherResponse.currently.get.cloudCover
    }
    catch {
      case e: Exception => None
    }

    val rain: Option[Float] = try {

      weatherResponse.currently.get.precipType match {
        case Some("rain") => weatherResponse.currently.get.precipIntensity
        case _ => None
      }
    }
    catch {
      case e: Exception => None
    }
    val snow: Option[Float] = try {
      weatherResponse.currently.get.precipAccumulation
    }
    catch {
      case e: Exception => None
    }

    val wind: Option[Float] = try {
      weatherResponse.currently.get.windSpeed
    } catch {
      case e: Exception => None
    }
    val timestamp = try {
      weatherResponse.currently.get.time
    }
    catch {
      case e: Exception => System.currentTimeMillis()
    }

    Weather(location.name, temp, humidiy, pressure, clouds, rain, snow, wind, timestamp)
  }

}
