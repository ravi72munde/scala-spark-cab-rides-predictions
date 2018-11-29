package actors

import akka.actor.{Actor, ActorLogging, Status}
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult
import dynamodb.{CabImpl, WeatherImp}
import models.{CabPriceBatch, WeatherBatch}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Actor Responsible for pushing data to DynamoDB
  */
class DynamoActor extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {

    // put all weather information(batch) to DynamoDB
    case weatherInfo: WeatherBatch => putWeatherInfo(weatherInfo)

    //put all cab prices to DynamoDB
    case cabPrices: CabPriceBatch => putCabPrices(cabPrices)

    //log errors
    case Status.Failure(v) => v.getStackTrace.foreach(error => log.error(error.toString))

    case q => log.warning(s"received unknown message type: ${q.getClass}")

  }

  def putWeatherInfo(weatherInfo: WeatherBatch): Unit = {

    val weathers = weatherInfo.weathers
    log.info("received " + weathers.size + " number of weather records")
    val result: Future[Seq[BatchWriteItemResult]] = WeatherImp.put(weathers)

    result onComplete {
      case Success(_) => log.info("Weather Batch processed on DynamoDB")
      case Failure(exception) => log.error("error process weather batch on dynamoDB :" + exception.getStackTrace.map(s => s.toString))
    }

  }

  /**
    * Put the records to dynamodb
    *
    * @param cabPriceBatch batch of cab prices
    */
  def putCabPrices(cabPriceBatch: CabPriceBatch): Unit = {
    val cabPrices = cabPriceBatch.cabPrices.toSeq
    log.info("received " + cabPrices.size + " number of cab price records")
    val result: Future[Seq[BatchWriteItemResult]] = CabImpl.put(cabPrices)
    result onComplete {
      case Success(_) => log.info("Cab Prices Batch processed on DynamoDB")
      case Failure(exception) => log.error("error process Cab Prices batch on dynamoDB :" + exception.getStackTrace)
    }
  }
}
