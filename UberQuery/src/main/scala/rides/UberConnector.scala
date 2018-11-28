package rides

import com.uber.sdk.core.client.{ServerTokenSession, SessionConfiguration}
import com.uber.sdk.rides.client.UberRidesApi
import com.uber.sdk.rides.client.model.PriceEstimatesResponse
import com.uber.sdk.rides.client.services.RidesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UberConnector {

  lazy val config: SessionConfiguration = new SessionConfiguration.Builder()
    .setClientId(System.getenv("uber_clientId"))
    .setServerToken(System.getenv("uber_token"))
    .build

  lazy val session: ServerTokenSession = new ServerTokenSession(config)


  lazy val rideService: RidesService = UberRidesApi.`with`(session).build.createService

  /**
    *
    * @param startLatitude  of the source
    * @param startLongitude of the source
    * @param endLatitude    of the destination
    * @param endLongitude   of the destination
    * @return estimated price response from Uber API
    */
  def getPriceEstimates(startLatitude: Float, startLongitude: Float, endLatitude: Float, endLongitude: Float): Future[PriceEstimatesResponse] = {

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
