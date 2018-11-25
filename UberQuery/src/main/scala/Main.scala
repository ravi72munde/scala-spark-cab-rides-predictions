
import Actors.Master
import DynamoDB.{DynamoWeatherImp, UberCabImpl}
import Models._
import Rides.UberAPI
import _root_.Weather.WeatherAPI
import akka.actor.{ActorSystem, Props}
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import LocationRepository.getPairedLocations
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

  val master = system.actorOf(Props(new Master(
    2, 2, 2)),
    "master")
  val locations = LocationRepository.sourceSeq ++ LocationRepository.destinationSeq

  master ! locations
  master ! LocationsTuples(getPairedLocations)



}
