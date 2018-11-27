import com.github.tomakehurst.wiremock.WireMockServer;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import java_connector.UberRideEstimator;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
public class UberRideEstimatorTest extends WireMockTest{

//    @Rule
//    public WireMockRule wm = new WireMockRule(options().port(2345));
    @Before
    public  void setup(){
        WireMockServer wm = new WireMockServer(options().port(2345));
        stubFor(get(urlMatching("/estimates/price"))
                .willReturn(aResponse().withBodyFile("estimate.json")));
    }


    @Test
    public void testUberResponse(){






        float startLatitude = 37.7752315f;
        float startLongitude = -122.418075f;
        float endLatitude = 37.7752415f;
        float endLongitude = -122.518075f;
        PriceEstimatesResponse result = UberRideEstimator.getPriceEstimates(startLatitude,startLongitude,endLatitude,endLongitude);
        System.out.println(result.getPrices());
        assertNotEquals(null,result);
    }

    @Test
    public void testUberFailedResponse(){
        float startLatitude = 0f;
        float startLongitude = 0f;
        float endLatitude = 0f;
        float endLongitude = 0f;
        PriceEstimatesResponse result = UberRideEstimator.getPriceEstimates(startLatitude,startLongitude,endLatitude,endLongitude);
        assertEquals(null,result);
    }


}


