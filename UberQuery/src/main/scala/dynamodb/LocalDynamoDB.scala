package dynamodb

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.model._

import scala.collection.JavaConverters._

/**
  * Required object to provide AWS client in scala. Boilerplate provided as a part of Scanamo library
  * Only Keys & DynamoDB instance configurations modified.
  */
object LocalDynamoDB {
  private val arbitraryThroughputThatIsIgnoredByDynamoDBLocal = new ProvisionedThroughput(1L, 1L)

  def client(): AmazonDynamoDBAsync =
    AmazonDynamoDBAsyncClient.asyncBuilder()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"))))
      .withEndpointConfiguration(new EndpointConfiguration("dynamodb.us-east-1.amazonaws.com", "us-east-1"))
      .build()

  def usingRandomTable[T](client: AmazonDynamoDB)(attributeDefinitions: (Symbol, ScalarAttributeType)*)(
    thunk: String => T
  ): Unit = {
    withRandomTable(client)(attributeDefinitions: _*)(thunk)
    ()
  }

  def withRandomTable[T](client: AmazonDynamoDB)(attributeDefinitions: (Symbol, ScalarAttributeType)*)(
    thunk: String => T
  ): T = {
    var created: Boolean = false
    var tableName: String = null
    while (!created) {
      try {
        tableName = java.util.UUID.randomUUID.toString
        createTable(client)(tableName)(attributeDefinitions: _*)
        created = true
      } catch {
        case e: ResourceInUseException =>
      }
    }

    val res = try {
      thunk(tableName)
    } finally {
      client.deleteTable(tableName)
      ()
    }
    res
  }

  def usingTable[T](client: AmazonDynamoDB)(tableName: String)(attributeDefinitions: (Symbol, ScalarAttributeType)*)(
    thunk: => T
  ): Unit = {
    withTable(client)(tableName)(attributeDefinitions: _*)(thunk)
    ()
  }

  def withTable[T](client: AmazonDynamoDB)(tableName: String)(attributeDefinitions: (Symbol, ScalarAttributeType)*)(
    thunk: => T
  ): T = {
    createTable(client)(tableName)(attributeDefinitions: _*)
    val res = try {
      thunk
    } finally {
      client.deleteTable(tableName)
      ()
    }
    res
  }

  def createTable(client: AmazonDynamoDB)(tableName: String)(attributes: (Symbol, ScalarAttributeType)*) = {
    client.createTable(
      attributeDefinitions(attributes),
      tableName,
      keySchema(attributes),
      arbitraryThroughputThatIsIgnoredByDynamoDBLocal
    )
  }

  def withTableWithSecondaryIndex[T](client: AmazonDynamoDB)(tableName: String, secondaryIndexName: String)
                                    (primaryIndexAttributes: (Symbol, ScalarAttributeType)*)(secondaryIndexAttributes: (Symbol, ScalarAttributeType)*)(
                                      thunk: => T
                                    ): T = {
    client.createTable(
      new CreateTableRequest().withTableName(tableName)
        .withAttributeDefinitions(attributeDefinitions(
          primaryIndexAttributes.toList ++ (secondaryIndexAttributes.toList diff primaryIndexAttributes.toList)))
        .withKeySchema(keySchema(primaryIndexAttributes))
        .withProvisionedThroughput(arbitraryThroughputThatIsIgnoredByDynamoDBLocal)
        .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
          .withIndexName(secondaryIndexName)
          .withKeySchema(keySchema(secondaryIndexAttributes))
          .withProvisionedThroughput(arbitraryThroughputThatIsIgnoredByDynamoDBLocal)
          .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
        )
    )
    val res = try {
      thunk
    } finally {
      client.deleteTable(tableName)
      ()
    }
    res
  }

  private def keySchema(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
    val hashKeyWithType :: rangeKeyWithType = attributes.toList
    val keySchemas = hashKeyWithType._1 -> KeyType.HASH :: rangeKeyWithType.map(_._1 -> KeyType.RANGE)
    keySchemas.map { case (symbol, keyType) => new KeySchemaElement(symbol.name, keyType) }.asJava
  }

  private def attributeDefinitions(attributes: Seq[(Symbol, ScalarAttributeType)]) = {
    attributes.map { case (symbol, attributeType) => new AttributeDefinition(symbol.name, attributeType) }.asJava
  }
}

