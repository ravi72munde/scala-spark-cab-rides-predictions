package models

import java.util.UUID

import com.lyft.networking.apiObjects.CostEstimate
import com.uber.sdk.rides.client.model.PriceEstimate


/**
  * Generic Case class representing cab rides
  *
  * @param cab_type         : Type of the cab(Uber/Lyft)
  * @param product_id       : Unique ID from API
  * @param name             : Name of the cab type: UberXL,Pool,Share etc
  * @param price            : Average price estimate in USD
  * @param distance         : Distance in miles
  * @param surge_multiplier : Price Surge Multiplier, 1 if none
  * @param time_stamp       : Time in epoch when query was made
  * @param source           : Source of the trip
  * @param destination      : Destination of the trip
  */
case class CabPrice(cab_type: String, product_id: String, name: String, price: Option[BigDecimal], distance: Option[Float], surge_multiplier: Float, time_stamp: Long, source: String, destination: String, id: String)

case class CabPriceBatch(cabPrices: Set[CabPrice]) {}

/**
  * Cab price trait to convert specific(Uber & Lyft) to a generic CabPrice Model
  */
trait CabPriceModel[T] {

  /**
    *
    * @param x : Source Objects from 'Cab' APIs
    * @param s : Source of the trip(Location)
    * @param d : Destination of the trip(Location)
    * @return : CabPrice wrapped estimate
    */
  def apply(x: T, s: Location, d: Location): CabPrice
}


/**
  * Cab price implementation for Uber prices.
  * [PriceEstimate] is a java model for Uber prices
  */
object UberPriceModel extends CabPriceModel[PriceEstimate] {

  /**
    * Convert PriceEstimate to CabPrice
    *
    * @param priceEstimate : Uber JAVA API response type
    * @param source        : Source of the trip(Location)
    * @param destination   : Destination of the trip(Location)
    * @return CabPrice     : CabPrice wrapped estimate
    */
  override def apply(priceEstimate: PriceEstimate, source: Location, destination: Location): CabPrice = {

    //name: Uber,UberXL,Pool etc
    val name = priceEstimate.getDisplayName

    // Unique product id from each type of uber(Hexadecimal values)
    val product_id = priceEstimate.getProductId

    //Average of max and min estimated price, can be null in some cases
    val price: Option[BigDecimal] = (priceEstimate.getHighEstimate, priceEstimate.getLowEstimate) match {
      case (_, null) | (null, _) => None
      case (eh: java.math.BigDecimal, el: java.math.BigDecimal) => Some(BigDecimal(eh.add(el)) / 2)
    }

    //Distance between given source and destination
    val distance: Option[Float] = priceEstimate.getDistance match {
      case f: java.lang.Float => Some(f.floatValue())
      case _ => None
    }

    //Price surge multiplier if present, else 1
    val surge_multiplier: Float = priceEstimate.getSurgeMultiplier match {
      case b: java.lang.Float => b.floatValue()
      case _ => 1f
    }
    val time_stamp = System.currentTimeMillis()
    val id: String = UUID.randomUUID().toString

    // Create the generic CabPrice value
    CabPrice("Uber", product_id, name, price, distance, surge_multiplier, time_stamp, source.name, destination.name, id)

  }

}

/////////////////////////////////////////TO DO LYFT API BELOW/////////////////////////////////////////////////////

/**
  *
  * Cab price implementation for Lyft prices.
  * [CostEstimate] is a java model for Lyft prices
  */
object LyftPriceModel extends CabPriceModel[CostEstimate] {
  /**
    *
    * @param priceEstimate : Source Objects from Lyft API
    * @param source        : Source of the trip(Location)
    * @param destination   : Destination of the trip(Location)
    * @return : CabPrice wrapped estimate
    */
  override def apply(priceEstimate: CostEstimate, source: Location, destination: Location): CabPrice = {
    //name: Lyft,Shared, etc
    val name = priceEstimate.display_name

    // Unique product id from each type of lyft
    val product_id = priceEstimate.ride_type

    //Average of max and min estimated price, can be null in some cases
    val price: Option[BigDecimal] = (priceEstimate.estimated_cost_cents_max, priceEstimate.estimated_cost_cents_min) match {
      case (_, null) | (null, _) => None
      case (eh: Integer, el: Integer) => Some(BigDecimal(eh + el) / 200) // cost estimate is in cents so divide by 100 to get in dollars.
    }

    //Distance between given source and destination
    val distance: Option[Float] = priceEstimate.estimated_distance_miles match {
      case f: java.lang.Double => Some(f.floatValue())
      case _ => None
    }

    //Price surge multiplier if present, else 1
    //Lyft gives the value in % of additional amount. Convert it to a multiplier
    val surge_multiplier: Float = {
      """\d+""".r findFirstIn priceEstimate.primetime_percentage match {
        case b: Option[String] => try {
          b.get.toFloat / 100
        } catch {
          case _: Exception => 1f
        } //convert % to float
        case _ => 1f
      }
    } + 1 //add 1 to convert % to a multiplier


    val time_stamp = System.currentTimeMillis()
    val id: String = UUID.randomUUID().toString
    // Create the generic CabPrice value

    CabPrice("Lyft", product_id, name, price, distance, surge_multiplier, time_stamp, source.name, destination.name, id)

  }
}
