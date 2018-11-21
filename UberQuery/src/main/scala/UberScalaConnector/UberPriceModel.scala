package UberScalaConnector

import com.uber.sdk.rides.client.model.PriceEstimate

case class CabPrice(cab_type:String,product_id:String, name:String, price:Option[BigDecimal], distance:Option[Float], surge_multiplier:Float, time_stamp: Long){
  override def toString: String = "UberType : " + name + ", Price: " + price + ", Distance: " + distance + ", Surge: " + surge_multiplier

}
object UberPriceModel {


  def apply(priceEstimate: PriceEstimate): CabPrice = {

    val name = priceEstimate.getDisplayName
    val product_id = priceEstimate.getProductId
    val price:Option[BigDecimal] = (priceEstimate.getHighEstimate,priceEstimate.getLowEstimate) match {
      case(_,null) | (null,_) => None
      case (eh:java.math.BigDecimal,el:java.math.BigDecimal) => Some(BigDecimal(eh.add(el))/2)
    }

    val distance:Option[Float] = priceEstimate.getDistance match {
      case f:java.lang.Float=> Some(f.floatValue())
      case _=> None
    }

    val surge_multiplier:Float = priceEstimate.getSurgeMultiplier match{
      case b:java.lang.Float => b.floatValue()
      case _ =>1f
    }

    CabPrice("Uber",product_id,name,price,distance,surge_multiplier,System.currentTimeMillis())

  }
}
