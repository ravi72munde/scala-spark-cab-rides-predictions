package java_connector

import com.lyft.networking.apiObjects.CostEstimateResponse
import com.lyft.networking.apis.LyftPublicApi
import com.lyft.networking.{ApiConfig, LyftApiFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object LyftConnector {
  lazy val apiConfig: ApiConfig = new ApiConfig.Builder()
    .setClientId(System.getenv("lyft_clientID"))
    .setClientToken(System.getenv("lyft_client_token"))
    .build
  lazy val rideService: LyftPublicApi = new LyftApiFactory(apiConfig).getLyftPublicApi

  /**
    * @param startLatitude  of the source
    * @param startLongitude of the source
    * @param endLatitude    of the destination
    * @param endLongitude   of the destination
    * @return estimate price response from Lyft API
    */

  def getPriceEstimates(startLatitude: Float, startLongitude: Float, endLatitude: Float, endLongitude: Float): Future[CostEstimateResponse] = {

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
