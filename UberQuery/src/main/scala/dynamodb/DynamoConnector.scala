package dynamodb

import actors.CabRideSystem
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult
import com.gu.scanamo.{DynamoFormat, ScanamoAsync, Table}
import models.{CabPrice, Weather}

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
object CabImpl extends DynamoTrait[CabPrice] {

  private val client = LocalDynamoDB.client()

  /**
    * Put set of values into DynamoDB
    *
    * @param vs : Set of values
    * @return : Future wrapped results
    */
  def put(vs: Seq[CabPrice]): Future[Seq[BatchWriteItemResult]] = {
    // float implicit conversion required for DynamoDB object conversions
    implicit val floatAttribute = DynamoFormat.coercedXmap[Float, String, IllegalArgumentException](_.toFloat)(_.toString)
    implicit val executionContext = CabRideSystem.system.getDispatcher
    val table = Table[CabPrice]("cab_rides")
    val operations = table.putAll(vs.toSet)
    ScanamoAsync.exec(client)(operations)
  }
}

/**
  * Weather specific implementation
  */
object WeatherImp extends DynamoTrait[Weather] {

  private val client = LocalDynamoDB.client()

  /**
    * Put set of values into DynamoDB
    *
    * @param vs : Set of values
    * @return : Future wrapped results
    */
  def put(vs: Seq[Weather]): Future[Seq[BatchWriteItemResult]] = {
    implicit val floatAttribute = DynamoFormat.coercedXmap[Float, String, IllegalArgumentException](_.toFloat)(_.toString)
    implicit val executionContext = CabRideSystem.system.getDispatcher

    val table = Table[Weather]("weather")
    val operations = table.putAll(vs.toSet)
    ScanamoAsync.exec(client)(operations)
  }
}


