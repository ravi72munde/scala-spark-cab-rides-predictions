package rides.connector

import actors.CabRideSystem
import com.uber.sdk.core.client.{ServerTokenSession, SessionConfiguration}
import com.uber.sdk.rides.client.UberRidesApi
import com.uber.sdk.rides.client.model.PriceEstimatesResponse
import com.uber.sdk.rides.client.services.RidesService
import org.slf4j.LoggerFactory
import rides.connector.UberConnectorConfig.rideService

import scala.concurrent.Future
import scala.util.Properties.envOrElse

class UberConnector extends RidesConnector[PriceEstimatesResponse] {
  /**
    *
    * @param startLatitude  of the source
    * @param startLongitude of the source
    * @param endLatitude    of the destination
    * @param endLongitude   of the destination
    * @return estimated price response from Uber API
    */
  def getPriceEstimates(startLatitude: Float, startLongitude: Float, endLatitude: Float, endLongitude: Float): Future[PriceEstimatesResponse] = {

    implicit val executionContext = CabRideSystem.system.getDispatcher
    val priceEstimate: Future[PriceEstimatesResponse] = Future {
      rideService match {
        case service: RidesService => service.getPriceEstimates(startLatitude, startLongitude, endLatitude, endLongitude)
          .execute()
          .body
        case q => throw new Exception("failed to initialize uber ride service " + q)
      }
    }
    priceEstimate
  }
}

/**
  * Configuration object for Uber
  */
private object UberConnectorConfig {
  private val log = LoggerFactory.getLogger(UberConnectorConfig.getClass)
  private val config: SessionConfiguration = new SessionConfiguration.Builder()
    .setClientId(envOrElse("uber_clientId", "NOT_DEFINED"))
    .setServerToken(envOrElse("uber_token", "NOT_DEFINED"))
    .build
  private val session: ServerTokenSession = new ServerTokenSession(config)
  val rideService: RidesService = UberRidesApi.`with`(session).build.createService

  log.info("Starting Uber Ride Service")


}
