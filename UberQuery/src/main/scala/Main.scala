
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

  /*Actor System for the project*/
  val system = ActorSystem("CabRideSystem")

  /*All locations from repository*/
  val locations = LocationRepository.locations

  /*Set up master actor for supervising all workers*/
  val master = system.actorOf(Props(new Master(nrofWeatherWorkers = locations.size, nrofUberWorkers = locations.size / 2, 2)),
    "master")

  /*Schedule Weather Job with specified interval*/
  system.scheduler.schedule(0 seconds, 30 minutes)(
    master ! locations
  )

  /*Schedule Cab Price Estimator Job with specified interval*/
  system.scheduler.schedule(0 seconds, 3 minute)(
    master ! LocationsTuples(getPairedLocations)
  )

}
