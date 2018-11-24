
import DynamoDB.{DynamoUberImpl, DynamoWeatherImp}
import Models.{CabPrice, Location, LocationRepository, Weather}
import Rides.UberAPI
import _root_.Weather.WeatherAPI
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Work in progress
  */
object Main extends App {


  /*
  Uber Price testing
   */
  val prices: Set[CabPrice] = {
    LocationRepository.getPairedLocations.flatMap(l => UberAPI.getPrices(l._1, l._2)).toSet
  }
  val cabPriceResult: Future[Seq[BatchWriteItemResult]] = DynamoUberImpl.put(prices)

  /** **********************************/

  /*
  Weather Info testing
   */
  val getWeather: Location => Option[Weather] = (x: Location) => WeatherAPI.getCurrentWeather(x)
  val locations = LocationRepository.sourceSeq ++ LocationRepository.destinationSeq
  val result: Seq[Weather] = locations.flatMap(l=>getWeather(l))
  val futureResult: Future[Seq[BatchWriteItemResult]] = DynamoWeatherImp.put(result.toSet)

  Await.result(futureResult, Duration.Inf)
  Await.ready(cabPriceResult, Duration.Inf)

}
