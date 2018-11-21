
import DynamoDB.DynamoUberImpl
import Models.{CabPrice, Location, UberPriceModel}
import UberJavaConnector.UberRideEstimator
import com.uber.sdk.rides.client.model.{PriceEstimate, PriceEstimatesResponse}

import scala.collection.JavaConverters._

/**
  * Work in progress
  */
object Main extends App{
  val source:Location = Location("cab-test",42.349867f,-71.077356f)
  val destination:Location = Location("cab-test2",42.340064f,-71.089784f)
  val ep: PriceEstimatesResponse = UberRideEstimator.getPriceEstimates(source, destination)
  val priceSet: Set[PriceEstimate] = ep.getPrices.asScala.toSet
  val prices: Set[CabPrice] = priceSet match {
    case ps:Set[PriceEstimate] => ps.map(p => UberPriceModel(p,source,destination))
    case _ => Set()
  }
  prices foreach println

  DynamoUberImpl.put(prices)


}
