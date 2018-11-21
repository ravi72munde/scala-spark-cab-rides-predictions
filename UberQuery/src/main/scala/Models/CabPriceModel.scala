package Models

import com.uber.sdk.rides.client.model.PriceEstimate


/**
  * Generic Case class for cab prices
  */
case class CabPrice(cab_type: String, product_id: String, name: String, price: Option[BigDecimal], distance: Option[Float], surge_multiplier: Float, time_stamp: Long) {
  // useful to see values in debug
  override def toString: String = "UberType : " + name + ", Price: " + price + ", Distance: " + distance + ", Surge: " + surge_multiplier
}


/**
  * Cab price trait to convert specific(uber & lyft) to a generic CabPrice Model
  */
trait CabPriceModel[T] {
  //apply to get generic cab price model
  def apply(x:T): CabPrice
}


/**
  * Cab price implementation for Uber prices.
  * [PriceEstimate] is a java model for Uber prices
  */
object UberPriceModel extends CabPriceModel[PriceEstimate] {

  // convert PriceEstimate to CabPrice
  override def apply(priceEstimate: PriceEstimate): CabPrice = {

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

    // Create the generic CabPrice value
    CabPrice("Uber", product_id, name, price, distance, surge_multiplier, System.currentTimeMillis())

  }

}
