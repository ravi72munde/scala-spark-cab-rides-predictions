
import DynamoDB.{DynamoUberImpl, DynamoWeatherImp}
import Models.Location
import RidesAPI.UberAPI
import Weather.WeatherAPI


/**
  * Work in progress
  */
object Main extends App {
  val source: Location = Location("cab-test", 42.349867f, -71.077356f)
  val destination: Location = Location("cab-test2", 42.340064f, -71.089784f)

  /*
  Uber Price testing
   */
//  val prices = UberAPI.getPrices(source, destination)
//  prices foreach println
//  DynamoUberImpl.put(prices)
  /************************************/

//  val weatherInfo = WeatherAPI.getCurrentWeather(source)
//  DynamoWeatherImp.put(Set(weatherInfo.get))
//  print (weatherInfo)

}
