package Actors

import Models.{CabPrice, Location}
import Rides.UberAPI
import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe

import scala.concurrent.Future

/**
  * Actor responsible for fetching price estimates for uber using Uber API
  */
class UberActor extends Actor with ActorLogging {
  import context.dispatcher

  /**
    * Get price estimates(wrapped in CabPrice) from Uber API
    * @param locations: Tuple with source and destination of a trip
    */
  def getPrices(locations: (Location, Location)): Future[Set[CabPrice]] = {
    val prices: Future[Set[CabPrice]] = {
      Future(UberAPI.getPrices(locations._1, locations._2))
    }
    prices
  }


  override def receive = {

    //directly pipe the prices data to sender(Master)

    case locations: (Location, Location) => pipe(getPrices(locations)) to sender

    case q => log.warning(s"received unknown message type: ${q.getClass}")
  }
}
