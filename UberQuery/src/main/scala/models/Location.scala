package models

/**
  * Represents physical location
  *
  * @param name      : String format name
  * @param latitude  : geo latitude
  * @param longitude : geo longitude
  *
  */
case class Location(name: String, latitude: Float, longitude: Float)

case class LocationBatch(locations: Seq[Location])

case class LocationsTuples(lts: Seq[(Location, Location)])

/**
  * Location Repository for busy locations in Boston City
  *
  */
object LocationRepository {

  val fenway = Location("Fenway", 42.342907f, -71.100292f)
  val southStation = Location("South Station", 42.352141f, -71.055135f)
  val bu = Location("Boston University", 42.350666f, -71.105410f)
  val northEnd = Location("North End", 42.365008f, -71.054222f)
  val haymarketSquare = Location("Haymarket Square", 42.364007f, -71.058433f)
  val neu = Location("Northeastern University", 42.340422f, -71.089269f)

  val finDistrict = Location("Financial District", 42.355976f, -71.054972f)
  val westEnd = Location("West End", 42.363428f, -71.066568f)
  val beaconHill = Location("Beacon Hill", 42.358865f, -71.070747f)
  val theatreDist = Location("Theatre District", 42.351884f, -71.064262f)
  val backBay = Location("Back Bay", 42.350282f, -71.080968f)
  val northStation = Location("North Station", 42.366265f, -71.063098f)

  //Sequence of 1st set of locations.
  val sourceSeq: Seq[Location] = Seq(fenway, southStation, bu, northEnd, haymarketSquare, neu)

  //Sequence of 2nd set of locations.
  val destinationSeq: Seq[Location] = Seq(finDistrict, westEnd, beaconHill, theatreDist, backBay, northStation)

  // Sequence of all the locations
  val locations: Seq[Location] = sourceSeq ++ destinationSeq

  /**
    * creates a random pair of source and destination with a reverse ride
    *
    * @return : Seq of paired locations
    */
  def getPairedLocations: Seq[(Location, Location)] = {
    import java.util.Random
    //randomize paring every time it's called so set new seed every time
    val rand = new Random(System.currentTimeMillis())
    // create a tuple of (source,destination); lazy to re-evaluate every time
    val randomPair: Seq[(Location, Location)] = sourceSeq.map(s => (s, destinationSeq(rand.nextInt(sourceSeq.length))))

    //append swapped tuple to the sequence of locations
    randomPair ++ randomPair.map(x => x.swap)
  }


}