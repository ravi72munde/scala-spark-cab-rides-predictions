import models.LocationRepository
import org.mockito.MockitoSugar
import org.mockito.MockitoSugar._
import org.scalatest.{FlatSpec, Matchers}
import rides.RidesAPI

class RidesSpec extends FlatSpec with Matchers {

  behavior of "UberAPI"

  "An empty Set" should "have size 0" in {
    val uberAPI = mock[RidesAPI]
    MockitoSugar.doReturn(Set()).when(uberAPI).getPrices(LocationRepository.backBay,LocationRepository.fenway)
    val result = uberAPI.getPrices(LocationRepository.backBay,LocationRepository.fenway)
    println(result)
  }

}
