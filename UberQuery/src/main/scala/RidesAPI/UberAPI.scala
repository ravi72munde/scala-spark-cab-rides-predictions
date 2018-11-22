package RidesAPI

import Models.{CabPrice, Location, UberPriceModel}
import UberJavaConnector.UberRideEstimator
import com.uber.sdk.rides.client.model.{PriceEstimate, PriceEstimatesResponse}

import scala.collection.JavaConverters._
object UberAPI {


    def getPrices(source:Location,destination:Location): Set[CabPrice] ={
      val ep: PriceEstimatesResponse = UberRideEstimator.getPriceEstimates(source, destination)
      val priceSet: Set[PriceEstimate] = ep.getPrices.asScala.toSet
      val prices: Set[CabPrice] = priceSet match {
        case ps: Set[PriceEstimate] => ps.map(p => UberPriceModel(p, source, destination))
        case _ => Set()
      }
      prices
    }

}
