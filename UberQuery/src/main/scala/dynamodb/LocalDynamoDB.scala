package dynamodb

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2._

import scala.util.Properties.envOrElse

/**
  * Required object to provide AWS client in scala. Boilerplate provided as a part of Scanamo library
  * Only Keys & DynamoDB instance configurations modified.
  */
object LocalDynamoDB {

  def client(): AmazonDynamoDBAsync =
    AmazonDynamoDBAsyncClient.asyncBuilder()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(envOrElse("AWS_ACCESS_KEY_ID", "NOT_DEFINED"), envOrElse("AWS_SECRET_ACCESS_KEY", "NOT_DEFINED"))))
      .withEndpointConfiguration(new EndpointConfiguration("dynamodb.us-east-1.amazonaws.com", "us-east-1"))
      .build()
}

