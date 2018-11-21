package DynamoDB

import Models.CabPrice
import com.gu.scanamo.{DynamoFormat, Scanamo, Table}



/**
  *Trait for scala to dynamoDB connection
  */
trait DynamoTrait[T]{
  //interface to put values to DynamoDB
  def put(vs:Set[T])
}

/**
  * DynamoDB implementation for pushing cab rides to cloud
  */
object DynamoCabImpl extends DynamoTrait[CabPrice] {
  def put(vs: Set[CabPrice]): Unit ={
    val client = LocalDynamoDB.client()
    implicit val floatAttribute = DynamoFormat.coercedXmap[Float,String,IllegalArgumentException](
      _.toFloat
    )(
      _.toString
    )
    val table = Table[CabPrice]("cab_rides")
    val operations = table.putAll(vs)
    Scanamo.exec(client)(operations)
  }
}

//object WeatherImpl extends DynamoTrait[T]{
//  override def put(vs: Set[T]): Unit = ???
//}
