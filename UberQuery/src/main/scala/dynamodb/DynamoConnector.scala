package dynamodb

import models.{CabPrice, Weather}
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult
import com.gu.scanamo.{DynamoFormat, ScanamoAsync, Table}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Trait for scala to dynamoDB connection
  */
trait DynamoTrait[T] {

  /**
    * Put set of values into DynamoDB
    *
    * @param vs : Set of values
    * @return : Future wrapped results
    */
  def put(vs: Seq[T]): Future[Seq[BatchWriteItemResult]]
}

/**
  * DynamoDB implementation for pushing cab rides to cloud
  */
object UberCabImpl extends DynamoTrait[CabPrice] {

  /**
    * Put set of values into DynamoDB
    *
    * @param vs : Set of values
    * @return : Future wrapped results
    */
  def put(vs: Seq[CabPrice]): Future[Seq[BatchWriteItemResult]] = {
    val client = LocalDynamoDB.client()

    // float implicit conversion required for DynamoDB object conversions
    implicit val floatAttribute = DynamoFormat.coercedXmap[Float, String, IllegalArgumentException](_.toFloat)(_.toString)

    val tbl = Table[CabPrice]("cab_rides")
    val operations = tbl.putAll(vs.toSet)
    ScanamoAsync.exec(client)(operations)
  }
}

/**
  * Weather specific implementation
  */
object DynamoWeatherImp extends DynamoTrait[Weather] {
  /**
    * Put set of values into DynamoDB
    *
    * @param vs : Set of values
    * @return : Future wrapped results
    */
  def put(vs: Seq[Weather]): Future[Seq[BatchWriteItemResult]] = {
    val client = LocalDynamoDB.client()

    implicit val floatAttribute = DynamoFormat.coercedXmap[Float, String, IllegalArgumentException](_.toFloat)(_.toString)

    val table = Table[Weather]("weather")
    val operations = table.putAll(vs.toSet)
    ScanamoAsync.exec(client)(operations)
  }
}


