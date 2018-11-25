package Actors

import java.util.concurrent.TimeUnit

import Models._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future

class Master(nrofWeatherWorkers: Int, nrofUberWorkers: Int, nrofDynamoWorkers: Int) extends Actor with ActorLogging {
  val weatherWorkerRouter: ActorRef = context.actorOf(
    Props[WeatherWorker].withRouter(RoundRobinPool(nrofWeatherWorkers)), name = "weatherWorkerPool")
  val uberWorkerRouter: ActorRef = context.actorOf(
    Props[UberWorker].withRouter(RoundRobinPool(nrofUberWorkers)), name = "uberWorkerPool")
  val dynamoRouter: ActorRef = context.actorOf(
    Props[DynamoActor].withRouter(RoundRobinPool(nrofDynamoWorkers)), name = "dynamoWorkerPool")

  implicit val timeout: Timeout = Timeout(10, TimeUnit.MINUTES) //timeout for response from worker
  import context.dispatcher

  def processWeather(sw: Seq[Some[Weather]]) = {
    log.info("Storing weather information")

    dynamoRouter ! WeatherBatch(sw.flatten)
    log.info("Shutting Down")

  }

  def processCabPrices(sc: Seq[Set[CabPrice]]) = {
    log.info("Storing cab prices")
    print(sc)
    dynamoRouter ! CabPriceBatch(sc.flatten.toSet)

  }

  override def receive: Receive = {

    case rides: LocationsTuples => {
      Future.sequence(rides.lts.map(tp => uberWorkerRouter ? (tp._1, tp._2)).map(_.mapTo[Set[CabPrice]])).map(processCabPrices)
//      rides.lts.foreach(uberWorkerRouter ! _)
    }
    case locationsBatch: Seq[Location] => Future.sequence(locationsBatch.map(weatherWorkerRouter ? _).map(_.mapTo[Some[Weather]])).map(processWeather)

    case q => log.warning(s"received unknown message type: ${q}")

  }


}


