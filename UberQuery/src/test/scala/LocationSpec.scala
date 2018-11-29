import models.{Location, LocationRepository}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.Seq

class LocationSpec extends FlatSpec with Matchers {
  behavior of "Location"

  "repository locations" should " return Seq[Location]" in {
    val locations = LocationRepository.getLocations
    locations should matchPattern {
      case _: Seq[Location] =>
    }
  }

  "random pairing of locations" should "return Seq oftype of locations" in {
    val locationsTuples = LocationRepository.getPairedLocations
    locationsTuples should matchPattern {
      case _: Seq[(Location, Location)] =>
    }
  }


}
