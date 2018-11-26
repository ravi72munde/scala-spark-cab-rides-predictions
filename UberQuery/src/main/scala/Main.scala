
import Actors.Master
import Models.LocationRepository.getPairedLocations
import Models._
import akka.actor.{ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Work in progress
  */
object Main extends App {


  //  /*
  //  Uber Price testing
  //   */
  //  val prices: Set[CabPrice] = {
  //    LocationRepository.getPairedLocations.flatMap(l => UberAPI.getPrices(l._1, l._2)).toSet
  //  }
  //  val cabPriceResult: Future[Seq[BatchWriteItemResult]] = UberCabImpl.put(prices)
  //
  //  /** **********************************/
  //
  //  /*
  //  Weather Info testing
  //   */
  //  val getWeather: Location => Option[Weather] = (x: Location) => WeatherAPI.getCurrentWeather(x)
  //  val locations = LocationRepository.sourceSeq ++ LocationRepository.destinationSeq
  //  val result: Seq[Weather] = locations.flatMap(l=>getWeather(l))
  //  val futureResult: Future[Seq[BatchWriteItemResult]] = DynamoWeatherImp.put(result.toSet)
  //
  //  Await.result(futureResult, Duration.Inf)
  //  Await.ready(cabPriceResult, Duration.Inf)


  val system = ActorSystem("CabRideSystem")
  val locations = LocationRepository.sourceSeq ++ LocationRepository.destinationSeq

  val master = system.actorOf(Props(new Master(nrofWeatherWorkers = locations.size, nrofUberWorkers = locations.size / 2, 2)),
    "master")
  system.scheduler.schedule(0 seconds, 5 minutes)(
    master ! locations
  )

  system.scheduler.schedule(0 seconds, 1 minute)(
    master ! LocationsTuples(getPairedLocations)
  )

}
