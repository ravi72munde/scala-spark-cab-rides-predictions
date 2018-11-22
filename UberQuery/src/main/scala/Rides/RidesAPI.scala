package Rides

import Models.{CabPrice, Location, UberPriceModel}
import UberJavaConnector.UberRideEstimator
import com.uber.sdk.rides.client.model.{PriceEstimate, PriceEstimatesResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * Trait to model cab prices
  */
trait RidesAPI {
  /**
    * @param source
    * @param destination
    * @return set[SabPrices]
    */
  def getPrices(source: Location, destination: Location): Set[CabPrice]
}

/**
  * Uber specific implementation
  */
object UberAPI extends RidesAPI {

  /**
    * @param source
    * @param destination
    * @return set[SabPrices]
    */
  override def getPrices(source: Location, destination: Location): Set[CabPrice] = {
    val ep: PriceEstimatesResponse = UberRideEstimator.getPriceEstimates(source, destination)
    val priceSet: Set[PriceEstimate] = ep.getPrices.asScala.toSet
    val prices: Set[CabPrice] = priceSet match {
      case ps: Set[PriceEstimate] => ps.map(p => UberPriceModel(p, source, destination))
      case _ => Set()
    }
    prices
  }


  /**
    * Lyft Specific implementation
    */

  object LyftAPI extends RidesAPI {
    /**
      * @param source
      * @param destination
      * @return set[SabPrices]
      */
    override def getPrices(source: Location, destination: Location): Set[CabPrice] = ???
  }

}
