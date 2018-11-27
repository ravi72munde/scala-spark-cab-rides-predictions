import com.github.tomakehurst.wiremock.WireMockServer;
import com.lyft.networking.apiObjects.CostEstimate;
import com.lyft.networking.apiObjects.CostEstimateResponse;
import java_connector.LyftRideEstimator;
import java_connector.UberRideEstimator;
import org.junit.Before;
import org.junit.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class LyftRideEstimatorTest extends WireMockTest{

    @Before
    public  void setup(){
        WireMockServer wm = new WireMockServer(options().port(2345));
        stubFor(get(urlMatching("/v1/cost"))
                .willReturn(aResponse().withBodyFile("lyft_estimate.json")));
    }
    @Test
    public void testLyftResponse(){

        float startLatitude = 37.7752315f;
        float startLongitude = -122.418075f;
        float endLatitude = 37.7752415f;
        float endLongitude = -122.518075f;
        CostEstimateResponse result = LyftRideEstimator.getPriceEstimates(startLatitude,startLongitude,endLatitude,endLongitude);
        assertNotEquals(null,result);
    }
    @Test
    public void testLyftFailedResponse(){

        float startLatitude = 0f;
        float startLongitude = 0f;
        float endLatitude = 0f;
        float endLongitude = 0f;
        CostEstimateResponse result = LyftRideEstimator.getPriceEstimates(startLatitude,startLongitude,endLatitude,endLongitude);
        assertNull(result);
    }

}
