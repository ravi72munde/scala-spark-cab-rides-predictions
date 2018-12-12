import com.amazonaws.services.dynamodbv2.document.internal.InternalUtils
import com.amazonaws.services.dynamodbv2.streamsadapter.model.RecordAdapter
import com.amazonaws.services.kinesis.model.Record
import com.google.gson.Gson
import org.apache.spark.sql._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kinesis.dynamostream.KinesisInitialPositions.Latest
import org.apache.spark.streaming.kinesis.dynamostream.KinesisInputDStream
import org.apache.spark.streaming.{Milliseconds, Seconds, StreamingContext}

object Trials extends App {

  import org.apache.log4j.{Level, Logger}

  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  //session setup
  System.setProperty("hadoop.home.dir", "C:\\winutils")
  val sparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("test")
    .getOrCreate()
  val sc = sparkSession.sparkContext
  val ssc = new StreamingContext(sc, Seconds(10))
  val sqlContext = sparkSession.sqlContext

  //creates an array of strings from raw byte array
  def kinesisRecordHandler: Record => Array[String] = (record: Record) => new String(record.getData.array()).split(",")

  //converts records to map of key value pair and then json
  def recordHandler = (record: Record) => {
    val gson = new Gson
    val sRecord = record.asInstanceOf[RecordAdapter].getInternalObject
    val map = InternalUtils.toSimpleMapValue(sRecord.getDynamodb.getNewImage)
    gson.toJson(map)
  }

  case class CabPrice(cab_type: String, product_id: String, name: String, price: String, distance: String, surge_multiplier: String, time_stamp: String, source: String, destination: String, id: String)

  val stream_cab = KinesisInputDStream.builder
    .streamingContext(ssc)
    .streamName("cab_rides")
    .regionName("us-east-1")
    .initialPosition(new Latest())
    .checkpointAppName("cab_rides-app")
    .checkpointInterval(Milliseconds(1000))
    .storageLevel(StorageLevel.MEMORY_AND_DISK_2)
    .buildWithMessageHandler(recordHandler)


  val stream_weather = KinesisInputDStream.builder
    .streamingContext(ssc)
    .streamName("weather")
    .regionName("us-east-1")
    .initialPosition(new Latest())
    .checkpointAppName("cab_rides-app")
    .checkpointInterval(Milliseconds(1000))
    .storageLevel(StorageLevel.MEMORY_AND_DISK_2)
    .buildWithMessageHandler(recordHandler)


  //creating dataframe, can be stored as temp view
  val cabSchema = Encoders.product[CabPrice].schema
  stream_cab.foreachRDD(rdd => {
    import sqlContext.implicits._
    //val xx: Dataset[String] = rdd.toDS()

    val df: DataFrame = sqlContext.read.schema(cabSchema).json(rdd.toDS())
    df.show()

  })
  ssc.start()
  ssc.awaitTermination()

}