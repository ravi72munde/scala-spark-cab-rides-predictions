package actors

import akka.actor.ActorSystem

/**
  * Actor System object to be used by the project
  */
object CabRideSystem {
  /*Actor System for the project*/
  val system: ActorSystem = ActorSystem("CabRideSystem")

}
