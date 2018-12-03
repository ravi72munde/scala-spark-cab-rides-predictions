package actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import models.{CabPrice, Location}
import rides.UberAPI
import rides.UberAPI.LyftAPI

import scala.concurrent.Future

/**
  * Actor responsible for fetching price estimates for cabs using Uber & Lyft API
  */
class CabPriceActor extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: PartialFunction[Any, Unit] = {

    //directly pipe the prices data to sender(Master)

    case locations: (Location, Location) => pipe(getPrices(locations)) to sender

    case q => log.warning(s"received unknown message type: ${q.getClass}")
  }

  /**
    * Get price estimates(wrapped in CabPrice) from Uber & Lyft API
    *
    * @param locations : Tuple with source and destination of a trip
    */
  def getPrices(locations: (Location, Location)): Future[Set[CabPrice]] = {

    val prices: Future[Set[CabPrice]] = for {
      uberPrices: Set[CabPrice] <- Future(UberAPI.getPrices(locations._1, locations._2))
      lyftPrices: Set[CabPrice] <- Future(LyftAPI.getPrices(locations._1, locations._2))
    } yield uberPrices ++ lyftPrices

    prices
  }
}
