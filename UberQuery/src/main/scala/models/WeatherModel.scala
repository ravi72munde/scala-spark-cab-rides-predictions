package models

import com.snowplowanalytics.weather.providers.darksky.Responses

/**
  * Weather class that represents weather at a given location
  *
  * @param location of the ride
  * @param temp temperature
  * @param humidity at location
  * @param pressure at location
  * @param clouds at location
  * @param rain at location
  * @param snow at location
  * @param wind at location
  * @param time_stamp request time_stamp
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
    * @param weatherResponse wrapped response from DarkSky
    * @param location the weather corresponds to
    * @return case class Weather
    */
  def apply(weatherResponse: Responses.DarkSkyResponse, location: Location): Weather = {

    val temp: Option[Float] = try {
      weatherResponse.currently.get.temperature
    }
    catch {
      case e: Exception => e.printStackTrace(); None
    }
    val humidity: Option[Float] = try {
      weatherResponse.currently.get.humidity
    } catch {
      case e: Exception => e.printStackTrace();None
    }
    val pressure: Option[Float] = try {
      weatherResponse.currently.get.pressure
    }
    catch {
      case e: Exception => e.printStackTrace();None
    }

    val clouds: Option[Float] = try {
      weatherResponse.currently.get.cloudCover
    }
    catch {
      case e: Exception => e.printStackTrace();None
    }

    val rain: Option[Float] = try {

      weatherResponse.currently.get.precipType match {
        case Some("rain") => weatherResponse.currently.get.precipIntensity
        case _ => None
      }
    }
    catch {
      case e: Exception => e.printStackTrace();None
    }
    val snow: Option[Float] = try {
      weatherResponse.currently.get.precipAccumulation
    }
    catch {
      case e: Exception => e.printStackTrace();None
    }

    val wind: Option[Float] = try {
      weatherResponse.currently.get.windSpeed
    } catch {
      case e: Exception => e.printStackTrace();None
    }
    val timestamp = try {
      weatherResponse.currently.get.time
    }
    catch {
      case _: Exception => System.currentTimeMillis()
    }

    Weather(location.name, temp, humidity, pressure, clouds, rain, snow, wind, timestamp)
  }

}
