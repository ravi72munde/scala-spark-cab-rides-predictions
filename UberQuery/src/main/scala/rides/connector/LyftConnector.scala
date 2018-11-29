package rides.connector

import actors.CabRideSystem
import com.lyft.networking.apiObjects.CostEstimateResponse
import com.lyft.networking.apis.LyftPublicApi
import com.lyft.networking.{ApiConfig, LyftApiFactory}
import org.slf4j.LoggerFactory
import rides.connector.LyftConnectorConfig.rideService

import scala.concurrent.Future
import scala.util.Properties.envOrElse

class LyftConnector extends RidesConnector[CostEstimateResponse] {

  /**
    * @param startLatitude  of the source
    * @param startLongitude of the source
    * @param endLatitude    of the destination
    * @param endLongitude   of the destination
    * @return estimate price response from Lyft API
    */

  def getPriceEstimates(startLatitude: Float, startLongitude: Float, endLatitude: Float, endLongitude: Float): Future[CostEstimateResponse] = {
    implicit val executionContext = CabRideSystem.system.getDispatcher
    val priceEstimate: Future[CostEstimateResponse] = Future {
      rideService match {
        case service: LyftPublicApi => service.getCosts(startLatitude.toDouble, startLongitude.toDouble, null, endLatitude.toDouble, endLongitude.toDouble)
          .execute()
          .body
        case q => throw new Exception("failed to initialize lyft ride service " + q)
      }
    }
    priceEstimate
  }
}

/**
  * Configuration object for Uber
  */
private object LyftConnectorConfig {
  val rideService: LyftPublicApi = new LyftApiFactory(apiConfig).getLyftPublicApi
  private val log = LoggerFactory.getLogger(LyftConnectorConfig.getClass)
  private val apiConfig: ApiConfig = new ApiConfig.Builder()
    .setClientId(envOrElse("lyft_clientID", "NOT_DEFINED"))
    .setClientToken(envOrElse("lyft_client_token", "NOT_DEFINED"))
    .build

  log.info("Starting Lyft Ride Service")
}
