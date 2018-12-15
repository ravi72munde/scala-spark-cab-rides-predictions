package rides

import com.lyft.networking.apiObjects.CostEstimateResponse
import com.uber.sdk.rides.client.model.PriceEstimatesResponse
import models.{CabPrice, Location, LyftPriceModel, UberPriceModel}
import org.slf4j.LoggerFactory
import rides.connector.{LyftConnector, RidesConnector, UberConnector}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Trait to model cab prices
  */
trait RidesAPI {

  // Connector for rides
  val ridesConnector: RidesConnector[_]

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

  // uberConnector
  override val ridesConnector = new UberConnector
  private val log = LoggerFactory.getLogger(UberAPI.getClass)


  /**
    * @param source      location of the trip
    * @param destination location of the trip
    * @return set[SabPrices]
    */
  override def getPrices(source: Location, destination: Location): Set[CabPrice] = {


    // future wrapped price estimate from uber api
    val epf: Future[PriceEstimatesResponse] = ridesConnector.getPriceEstimates(source.latitude, source.longitude, destination.latitude, destination.longitude)

    //process data in sync
    val result = Await.result(epf, 30 seconds)
    result match {
      case pe: PriceEstimatesResponse => {
        pe.getPrices
          .asScala.map(UberPriceModel(_, source, destination))
          .toSet
      }
      // in case of failure just send blank set to avoid failures
      case q => log.error("Failed to fetch uber records. Got " + q + " instead of PriceEstimateResponse"); Set()
    }

  }


  /**
    * Lyft Specific implementation
    */
  object LyftAPI extends RidesAPI {


    override val ridesConnector = new LyftConnector
    private val log = LoggerFactory.getLogger(LyftAPI.getClass)

    /**
      * @param source      location of the trip
      * @param destination location of the trip
      * @return set[SabPrices]
      */
    override def getPrices(source: Location, destination: Location): Set[CabPrice] = {
      // future wrapped price estimate from lyft api

      val cef: Future[CostEstimateResponse] = ridesConnector.getPriceEstimates(source.latitude, source.longitude, destination.latitude, destination.longitude)

      //process data in sync
      val result: CostEstimateResponse = Await.result(cef, 30 seconds)
      result match {
        case cer: CostEstimateResponse => {
          cer.cost_estimates
            .asScala.map(LyftPriceModel(_, source, destination))
            .toSet
        }
        // in case of failure just send blank set to avoid failures
        case q => log.error("Failed to fetch uber records. Got " + q + " instead of CostEstimateResponse"); Set()
      }

    }

  }

}
