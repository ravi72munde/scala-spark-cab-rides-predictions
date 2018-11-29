package rides.connector

import scala.concurrent.Future

/**
  *
  * @T Type of Java Pricing wrapper from the API
  */
trait RidesConnector[T] {
  /**
    *
    * @param startLatitude  starting latitude of the trip
    * @param startLongitude starting longitude of the trip
    * @param endLatitude    end latitude of the trip
    * @param endLongitude   end longitude of the trip
    * @return
    */
  def getPriceEstimates(startLatitude: Float, startLongitude: Float, endLatitude: Float, endLongitude: Float): Future[T]
}
