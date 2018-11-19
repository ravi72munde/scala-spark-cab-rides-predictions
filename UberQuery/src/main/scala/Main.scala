import collection.JavaConversions._
import com.uber.sdk.rides.client.model.PriceEstimate



case class UberPrice(priceEstimate: PriceEstimate) {

  val name = priceEstimate.getDisplayName()
  val price:Option[BigDecimal] = (priceEstimate.getHighEstimate,priceEstimate.getLowEstimate) match {
    case(_,null) | (null,_) => None
    case (eh:java.math.BigDecimal,el:java.math.BigDecimal) => Some(BigDecimal(eh.add(el))/2)
  }

  val distance:Option[Float] = priceEstimate.getDistance match {
    case f:java.lang.Float=> Some(f.floatValue())
    case _=> None
  }
  val surge_multiplier:Float = priceEstimate.getSurgeMultiplier() match{
    case b:java.lang.Float => b.floatValue()
    case _ =>1f
  }
  override def toString: String = "UberType : " + name + ", Price: " + price + ", Distance: " + distance + ", Surge: " + surge_multiplier
}

object Main extends App {

  val ep = UberRideEstimator.getPriceEstimates(42.349867f,-71.077356f, 42.340064f,-71.089784f)
//  val ep = UberRideEstimator.getPriceEstimates(37.7752315f, -122.418075f, 37.7752415f, -122.518075f)
  val prices: Stream[UberPrice] = ep match {
    case null => Stream()
    case _ => ep.getPrices.toStream.map(p => UberPrice(p))
  }

  println(prices.size)
  println(prices foreach println)
}
