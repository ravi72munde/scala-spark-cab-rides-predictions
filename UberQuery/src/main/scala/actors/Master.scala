package actors

import models._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.routing.RoundRobinPool
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Master Actor to supervise other actors
  *
  * @param numWeatherWorkers: Number of weather workers requested
  * @param numUberWorkers   : Number of Uber workers requested
  * @param numDynamoWorkers : Number of DynamoDB workers requested
  */
//noinspection ScalaDocParserErrorInspection,RedundantBlock,RedundantBlock
class Master(numWeatherWorkers: Int, numUberWorkers: Int, numDynamoWorkers: Int) extends Actor with ActorLogging {

  log.info("Master Actor started")

  val weatherWorkerRouter: ActorRef = context.actorOf(
    Props[WeatherActor].withRouter(RoundRobinPool(numWeatherWorkers)), name = "weatherWorkerPool")
  log.info("WeatherActor started with no. of workers = " + numWeatherWorkers)

  val uberWorkerRouter: ActorRef = context.actorOf(
    Props[CabPriceActor].withRouter(RoundRobinPool(numUberWorkers)), name = "uberWorkerPool")
  log.info("UberWorker started with no. of workers = " + numUberWorkers)

  val dynamoRouter: ActorRef = context.actorOf(
    Props[DynamoActor].withRouter(RoundRobinPool(numDynamoWorkers)),
    name = "dynamoWorkerPool")
  log.info("DynamoWorker started with no. of workers = " + numDynamoWorkers)

  implicit val timeout: Timeout = 2.minutes //timeout for response from workers
  import context.dispatcher

  /**
    * function to transform 'Seq[Set[CabPrice]]' to CabPriceBatch
    */
  def processCabPrices: Seq[Set[CabPrice]] => CabPriceBatch = {
    sc: Seq[Set[CabPrice]] => CabPriceBatch(sc.flatten.toSet)
  }

  /**
    * function to transform 'Seq[Some[Weather]]' to WeatherBatch
    */
  def processWeather: Seq[Some[Weather]] => WeatherBatch = {
    sw: Seq[Some[Weather]] => WeatherBatch(sw.flatten)
  }

  override def receive: Receive = {

    // Process tuple of locations to retrieve price estimates. Once all uber workers are done processing, pipe the data to Dynamo Worker
    case rides: LocationsTuples => {
      // wait for all the workers to send data and then process
      val rideBatchResult: Future[CabPriceBatch] = Future.sequence(rides.lts.map(t => uberWorkerRouter ? (t._1, t._2)).map(_.mapTo[Set[CabPrice]])).map(processCabPrices)

      //forward data to dynamo actor for storing
      pipe(rideBatchResult) to dynamoRouter
      log.info("Cab ride data piped to Dynamo Actor")
    }

    // Process tuple of locations to retrieve weather info. Once all weather workers are done processing, pipe the data to Dynamo Worker
    case locationsBatch: Seq[Location] => //noinspection RedundantBlock
    {
      // wait for all the workers to send data and then process
      val weatherBatchResult = Future.sequence(locationsBatch.map(weatherWorkerRouter ? _).map(_.mapTo[Some[Weather]])).map(processWeather)

      //forward data to dynamo actor for storing
      pipe(weatherBatchResult) to dynamoRouter
      log.info("Weather data piped to Dynamo Actor")
    }

    case q => log.warning(s"received unknown message type: ${q.getClass}")

  }


}


