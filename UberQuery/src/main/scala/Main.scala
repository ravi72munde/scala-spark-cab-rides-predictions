
import UberJavaConnector.UberRideEstimator
import UberScalaConnector.{CabPrice, UberPriceModel}

import scala.collection.JavaConverters._

object Main extends App{

  val ep = UberRideEstimator.getPriceEstimates(42.349867f,-71.077356f, 42.340064f,-71.089784f)
  val prices: Set[CabPrice] = ep match {
    case null => Set()
    case _ => ep.getPrices.asScala.toSet.map(p => UberPriceModel(p))
  }

//  DynamoFace.put(prices)
  println(prices foreach println)
}