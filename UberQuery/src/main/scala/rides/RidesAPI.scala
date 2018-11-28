package rides

import com.lyft.networking.apiObjects.CostEstimateResponse
import com.uber.sdk.rides.client.model.PriceEstimatesResponse
import java_connector.LyftConnector
import models.{CabPrice, Location, LyftPriceModel, UberPriceModel}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

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

    // future wrapped price estimate from uber api
    val epf: Future[PriceEstimatesResponse] = UberConnector.getPriceEstimates(source.latitude, source.longitude, destination.latitude, destination.longitude)

    //process data in sync
    val result = Await.result(epf, 30 seconds)
    result match {
      case pe: PriceEstimatesResponse => {
        pe.getPrices
          .asScala.map(UberPriceModel(_, source, destination))
          .toSet
      }
      // in case of failure just send blank set to avoid failures
      case q => println("Failed to fetch uber records" + q); Set()
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
      // future wrapped price estimate from lyft api
      val cef: Future[CostEstimateResponse] = LyftConnector.getPriceEstimates(source.latitude, source.longitude, destination.latitude, destination.longitude)

      //process data in sync
      val result = Await.result(cef, 30 seconds)
      result match {
        case cer: CostEstimateResponse => {
          cer.cost_estimates
            .asScala.map(LyftPriceModel(_, source, destination))
            .toSet
        }
        // in case of failure just send blank set to avoid failures
        case q => println("Failed to fetch uber records" + q); Set()
      }

    }

  }

}
