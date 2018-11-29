import MockedResponse.cabPriceResponse
import models.CabPrice
import org.mockito.MockitoSugar
import org.mockito.MockitoSugar._
import org.scalatest.{FlatSpec, Matchers}
import rides.UberAPI
import rides.connector.{LyftConnector, UberConnector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class RidesSpec extends FlatSpec with Matchers {

  behavior of "RidesAPI"

  "Uber api" should "return set" in {

    val uberApi = mock[UberConnector]

    MockitoSugar.doReturn(Future(cabPriceResponse)).when(uberApi).getPriceEstimates(0f, 0f, 0f, 0f)

    val result = uberApi.getPriceEstimates(0f, 0f, 0f, 0f)
    Await.result(result, Duration.Inf) should matchPattern { case e: Set[CabPrice] => }

  }
  "Lyft api" should "return set" in {

    val uberApi = mock[LyftConnector]

    MockitoSugar.doReturn(Future(cabPriceResponse)).when(uberApi).getPriceEstimates(0f, 0f, 0f, 0f)

    UberAPI.ridesConnector
    val result = uberApi.getPriceEstimates(0f, 0f, 0f, 0f)
    Await.result(result, Duration.Inf) should matchPattern { case e: Set[CabPrice] => }

  }


}
