package actors

import _root_.weather.WeatherAPI
import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import models.{Location, Weather}

import scala.concurrent.Future

/**
  * Weather Actor responsible for retrieving weather info using Scala weather API
  */
class WeatherActor extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {

    //directly pipe the weather data to sender(Master)
    case location: Location => pipe(getWeatherInfo(location)) to sender

    case q => log.warning(s"received unknown message type: ${q.getClass}")
  }

  /**
    * Internal method to wrap the request in Future
    *
    * @param location : Location to get weather of
    * @return
    */
  def getWeatherInfo(location: Location): Future[Option[Weather]] = {
    val getWeather: Location => Option[Weather] = (x: Location) => WeatherAPI.getCurrentWeather(x)
    Future(getWeather(location))
  }
}
