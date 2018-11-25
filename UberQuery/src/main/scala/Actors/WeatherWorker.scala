package Actors

import Models.{Location, Weather}
import _root_.Weather.WeatherAPI
import akka.actor.{Actor, ActorLogging}


class WeatherWorker extends Actor with ActorLogging {
  def getWeatherInfo(location: Location): Option[Weather] = {
    val getWeather: Location => Option[Weather] = (x: Location) => WeatherAPI.getCurrentWeather(x)
    getWeather(location)
  }

  override def receive: Receive = {
    case location: Location => sender ! getWeatherInfo(location)
    case q => log.warning(s"received unknown message type: ${q.getClass}")
  }
}
