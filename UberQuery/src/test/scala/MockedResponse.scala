import com.snowplowanalytics.weather.providers.darksky.Responses.{DarkSkyResponse, DataPoint}
import models.CabPrice

object MockedResponse {

  val cabPriceResponse = Set(
    CabPrice("Uber", "55c66225-fbe7-4fd5-9072-eab1ece5e23e", "UberX", Some(7.5), Some(1.57f), 1.0f, 1543437805043L, "Theatre District", "North End", "b3f7d5d1-14bd-454f-b9b1-6d3f1dec8666"),
    CabPrice("Lyft", "lyft_lux", "Lux Black", Some(32.5), Some(3.43f), 1.0f, 1543437802879L, "Fenway", "Financial District", "577877e2-da12-47b9-a3c7-866eb7da9a19"),
    CabPrice("Uber", "8cf7e821-f0d3-49c6-8eba-e679c0ebcf6a", "Taxi", None, Some(3.64f), 1.0f, 1543437801544L, "Fenway", "Financial District", "65701cba-3000-43ad-8183-161e71963217"),
    CabPrice("Uber", "8cf7e821-f0d3-49c6-8eba-e679c0ebcf6a", "Taxi", None, Some(2.79f), 1.0f, 1543437804427L, "Boston University", "Beacon Hill", "74c46efb-4737-4cba-9dba-66a95ebf764a")
  )
  val costEstimateJson = "{\n      \"ride_type\": \"lyft_plus\",\n      \"estimated_duration_seconds\": 913,\n      \"estimated_distance_miles\": 3.29,\n      \"estimated_cost_cents_max\": 2355,\n      \"primetime_percentage\": \"25%\",\n      \"currency\": \"USD\",\n      \"estimated_cost_cents_min\": 1561,\n      \"display_name\": \"Lyft Plus\",\n      \"primetime_confirmation_token\": null,\n      \"cost_token\": null,\n      \"is_valid_estimate\": true\n    }"
  val priceEstimateJson = "{\n      \"localized_display_name\": \"uberX\",\n      \"distance\": 6.17,\n      \"display_name\": \"uberX\",\n      \"product_id\": \"a1111c8c-c720-46c3-8534-2fcdd730040d\",\n      \"high_estimate\": 17,\n      \"low_estimate\": 13,\n      \"duration\": 1080,\n      \"estimate\": \"$13-17\",\n      \"currency_code\": \"USD\"\n    }"
  val weatherResponse = DarkSkyResponse(42.356f, -71.055f, "America/New_York", Some(DataPoint(Some(33.68f), None, None, None, None, Some(0.83f), Some(29.04f), Some(0.64f), Some("partly-cloudy-night"), None, Some(58), Some(3), Some(355.21f), None, Some(0.0f), None, None, Some(0.0f), None, Some(995.66f), Some("Mostly Cloudy"), None, None, Some(40.38f), None, None, None, None, None, None, None, None, 1543448090, Some(0), None, Some(10.0f), Some(296), Some(17.82f), Some(11.05f))), None, None, None, None, None)

}
