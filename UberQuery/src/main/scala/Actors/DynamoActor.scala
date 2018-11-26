package Actors

import DynamoDB.{DynamoWeatherImp, UberCabImpl}
import Models.{CabPriceBatch, WeatherBatch}
import akka.actor.{Actor, ActorLogging, Status}
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

  /**
    * Put the records to dynamodb
    *
    * @param cabPriceBatch batch of cab prices
    */
  def putCabPrices(cabPriceBatch: CabPriceBatch): Unit = {

    val result: Future[Seq[BatchWriteItemResult]] = UberCabImpl.put(cabPriceBatch.cabPrices.toSeq)
    result onComplete {
      case Success(_) => log.info("Cab Prices Batch processed on DynamoDB")
      case Failure(exception) => log.error("error process Cab Prices batch on dynamoDB :" + exception.getMessage)
    }
  }

  override def receive: Receive = {

    // put all weather information(batch) to DynamoDB
    case weatherInfo: WeatherBatch => putWeatherInfo(weatherInfo)

    //put all cab prices to DynamoDB
    case cabPrices: CabPriceBatch => putCabPrices(cabPrices)

    //log errors
    case Status.Failure(v) => log.error(v.getMessage)

    case q => log.warning(s"received unknown message type: $q")

  }
}
