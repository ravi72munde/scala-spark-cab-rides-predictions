package Actors

import DynamoDB.{DynamoWeatherImp, UberCabImpl}
import Models.{CabPriceBatch, WeatherBatch}
import akka.actor.{Actor, ActorLogging}
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DynamoActor extends Actor with ActorLogging {
  def putWeatherInfo(weatherInfo: WeatherBatch): Seq[BatchWriteItemResult] = {
    val result = DynamoWeatherImp.put(weatherInfo.weathers)
    Await.result(result, Duration.Inf)
  }

  def putCabPrices(cabPriceBatch: CabPriceBatch): Seq[BatchWriteItemResult] = {

    val result = UberCabImpl.put(cabPriceBatch.cabPrices.toSeq)
    Await.result(result, Duration.Inf)
  }

  override def receive: Receive = {
    case weatherInfo: WeatherBatch => {
      putWeatherInfo(weatherInfo);
      context.stop(self)
    }
    case cabPrices: CabPriceBatch => {
      println("received "+cabPrices)
      putCabPrices(cabPrices)
    }
    case q => log.warning(s"received unknown message type: ${q.getClass}")

  }
}
