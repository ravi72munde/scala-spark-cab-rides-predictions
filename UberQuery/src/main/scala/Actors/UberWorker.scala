package Actors

import Models.{CabPrice, CabPriceBatch, Location}
import Rides.UberAPI
import akka.actor.{Actor, ActorLogging}

class UberWorker extends Actor with ActorLogging{
  /*
    Uber Price
     */
  def getPrices(locations: (Location, Location)) = {
    val prices: Set[CabPrice] = {
      UberAPI.getPrices(locations._1, locations._2)
    }
    CabPriceBatch(prices)

  }

  override def receive = {
    case locations: (Location, Location) => sender !  getPrices(locations)
    case q => log.warning(s"received unknown message type: ${q.getClass}")
  }
}
