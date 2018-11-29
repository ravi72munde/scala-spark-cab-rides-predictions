package rides

import models.{CabPrice, Location, LyftPriceModel, UberPriceModel}
import com.lyft.networking.apiObjects.CostEstimate
import com.uber.sdk.rides.client.model.PriceEstimate
import java_connector.{LyftRideEstimator, UberRideEstimator}

import scala.collection.JavaConverters._

/**
  * Trait to model cab prices
  */
trait RidesAPI {
  /**
    * @param source      location of the trip
    * @param destination location of the trip
    * @return set[SabPrices]
    */
  def getPrices(source: Location, destination: Location): Set[CabPrice]
}

/**
  * Uber specific implementation
  */
object UberAPI extends RidesAPI {

  /**
    * @param source      location of the trip
    * @param destination location of the trip
    * @return set[SabPrices]
    */
  override def getPrices(source: Location, destination: Location): Set[CabPrice] = {


    val priceSet: Option[Set[PriceEstimate]] = try {
      val ep = UberRideEstimator.getPriceEstimates(source.latitude, source.longitude, destination.latitude, destination.longitude)
      Some(ep.getPrices.asScala.toSet)
    } catch {
      case e: Exception => println(e.getMessage); None
    }

    // in case of failure just send blank set to avoid failures
    priceSet match {
      case ps: Some[Set[PriceEstimate]] => ps.get.map(p => UberPriceModel(p, source, destination))
      case _ => Set()
    }
  }


  /**
    * Lyft Specific implementation
    */

  object LyftAPI extends RidesAPI {
    /**
      * @param source      location of the trip
      * @param destination location of the trip
      * @return set[SabPrices]
      */
    override def getPrices(source: Location, destination: Location): Set[CabPrice] = {
      val priceSet: Option[Set[CostEstimate]] = try {
        val ec = LyftRideEstimator.getPriceEstimates(source.latitude, source.longitude, destination.latitude, destination.longitude)
        Some(ec.cost_estimates.asScala.toSet)
      }
      catch {
        case e: Exception => println(e.getMessage); None
      }
      // in case of failure just send blank set to avoid failures
      priceSet match {
        case ps: Some[Set[CostEstimate]] => ps.get.map(p => LyftPriceModel(p, source, destination))
        case _ => Set()
      }
    }
  }

}
