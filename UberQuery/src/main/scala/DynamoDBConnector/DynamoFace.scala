package DynamoDBConnector

//import UberScalaConnector.UberPrice
import UberJavaConnector.UberRideEstimator
import UberScalaConnector.{CabPrice, UberPriceModel}
import com.gu.scanamo.{DynamoFormat, Scanamo, Table}

import scala.collection.JavaConverters._

object DynamoFace extends App {
  val ep = UberRideEstimator.getPriceEstimates(42.349867f,-71.077356f, 42.340064f,-71.089784f)
  val prices: Set[CabPrice] = ep match {
    case null => Set()
    case _ => ep.getPrices.asScala.toSet.map(p => UberPriceModel(p))
  }

  put(prices)
  def put(vs: Set[CabPrice]): Unit ={
    val client = LocalDynamoDB.client()
    implicit val floatAttribute = DynamoFormat.coercedXmap[Float,String,IllegalArgumentException](
      _.toFloat
    )(
      _.toString
    )
    val table = Table[CabPrice]("cab_rides")
    //Table[UberPrice]("cab_rides")
    print(vs)
    val operations = table.putAll(vs)
    Scanamo.exec(client)(operations)
  }



}
