
import actors.{CabRideSystem, Master}
import akka.actor.Props
import models.LocationRepository.getPairedLocations
import models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Work in progress
  */
object Main extends App {

  val system = CabRideSystem.system
  /*All locations from repository*/
  val locations = LocationRepository.getLocations

  /*Set up master actor for supervising all workers*/
  val master = system.actorOf(Props(new Master(numWeatherWorkers = locations.size, numUberWorkers = locations.size, 2)),
    "master")

  /*Schedule Weather Job with specified interval*/
  system.scheduler.schedule(0 seconds, 1 hour)(
    master ! locations
  )

  /*Schedule Cab Price Estimator Job with specified interval*/
  system.scheduler.schedule(0 seconds, 5 minute)(
    master ! LocationsTuples(getPairedLocations)
  )


}
