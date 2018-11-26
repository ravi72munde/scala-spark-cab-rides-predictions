package Actors

import DynamoDB.{DynamoWeatherImp, UberCabImpl}
import Models.{CabPriceBatch, WeatherBatch}
import akka.actor.{Actor, ActorLogging}
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Actor Responsible for pushing data to DynamoDB
  */
class DynamoActor extends Actor with ActorLogging {

  import context.dispatcher

  def putWeatherInfo(weatherInfo: WeatherBatch): Unit = {


    val result: Future[Seq[BatchWriteItemResult]] = DynamoWeatherImp.put(weatherInfo.weathers)

    result onComplete {
      case Success(_) => log.info("Weather Batch processed on DynamoDB")
      case Failure(exception) => log.error("error process weather batch on dynamoDB :" + exception.getMessage)
    }

  }

  def putCabPrices(cabPriceBatch: CabPriceBatch) = {

    val result: Future[Seq[BatchWriteItemResult]] = UberCabImpl.put(cabPriceBatch.cabPrices.toSeq)
    result onComplete {
      case Success(_) => log.info("Cab Prices Batch processed on DynamoDB")
      case Failure(exception) => log.error("error process Cab Prices batch on dynamoDB :" + exception.getMessage)
    }
  }

  override def receive: Receive = {


    case weatherInfo: WeatherBatch => {
      // put all weather information(batch) to DynamoDB
      putWeatherInfo(weatherInfo)
    }
    case cabPrices: CabPriceBatch => {
      //put all cab prices to DynamoDB
      putCabPrices(cabPrices)
    }

    case q => log.warning(s"received unknown message type: ${q}")

  }
}
