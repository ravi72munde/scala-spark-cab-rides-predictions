
import DynamoDB.{DynamoUberImpl, DynamoWeatherImp}
import Models.{Location, LocationRepository, Weather}
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
  //  val source: Location =
  //  val destination: Location = Location("cab-test2", 42.340064f, -71.089784f)

  //  /*
  //  Uber Price testing
  //   */
  //  val prices = UberAPI.getPrices(source, destination)
  //  prices foreach println
  //  DynamoUberImpl.put(prices)
  //  /** **********************************/
  //
  //  /*
  //  Weather Info testing
  //   */
  //  val getWeather: Location=>Option[Weather] = (x:Location) => WeatherAPI.getCurrentWeather(x)
  //
  //  val result: Seq[Weather] = ls.map(getWeather andThen(w=>w.get))
  //  val futureResult: Future[Seq[BatchWriteItemResult]] = DynamoWeatherImp.put(result.toSet)
  //  Await.result(futureResult,Duration.apply(10,"sec"))
  //  futureResult.onComplete(println)

  val repository = LocationRepository.getPairedLocations
  repository foreach println
}
