import MockedResponse._
import com.lyft.networking.apiObjects.CostEstimate
import com.uber.sdk.rides.client.model.PriceEstimate
import models._
import org.scalatest.{FlatSpec, Matchers}

class ModelSpec extends FlatSpec with Matchers {
  behavior of "Models"
  "UberPriceModel" should "return CabPrice" in {
    import com.google.gson.Gson
    val g = new Gson

    val priceEstimate = g.fromJson(priceEstimateJson, classOf[PriceEstimate])
    val source = Location("source_test", 45f, 34f)
    val destination = Location("destination_test", 55f, 34f)

    UberPriceModel(priceEstimate, source, destination) should matchPattern {
      case CabPrice("Uber", "a1111c8c-c720-46c3-8534-2fcdd730040d", "uberX", _, Some(6.17f), 1.0, _, "source_test", "destination_test", _) =>
    }
  }

  "LyftPriceModel" should "return CabPrice" in {
    import com.google.gson.Gson

    val g = new Gson

    val costEstimate = g.fromJson(costEstimateJson, classOf[CostEstimate])
    val source = Location("source_test", 45f, 34f)
    val destination = Location("destination_test", 55f, 34f)

    LyftPriceModel(costEstimate, source, destination) should matchPattern {
      case CabPrice("Lyft", "lyft_plus", "Lyft Plus", _, Some(3.29f), 1.25, _, "source_test", "destination_test", _) =>
    }
  }

  "WeatherModel" should "return Weather" in {
    val location = Location("source_test", 45f, 34f)
    WeatherModel(weatherResponse, location) should matchPattern {
      case Weather("source_test", Some(40.38f), Some(0.64f), Some(995.66f), Some(0.83f), None, None, Some(11.05f), 1543448090) =>
    }
  }
}
