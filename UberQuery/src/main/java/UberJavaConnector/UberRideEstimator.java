package UberJavaConnector;

import com.uber.sdk.core.client.ServerTokenSession;
import com.uber.sdk.core.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

public class UberRideEstimator {
    private static RidesService rideService;
    private static final UberRideEstimator serviceInstance = new UberRideEstimator();

    private UberRideEstimator(){
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId(System.getenv("uber_clientId"))
                .setServerToken(System.getenv("uber_token"))
                .build();
        ServerTokenSession session = new ServerTokenSession(config);

        rideService = UberRidesApi.with(session).build().createService();
    }

    public static PriceEstimatesResponse getPriceEstimates(float startLattitude,float startLongitude, float endLattitude,float endLongitude ){

        PriceEstimatesResponse priceEstimate =null;
        try {
            priceEstimate= rideService.
                    getPriceEstimates(startLattitude,startLongitude,endLattitude,endLongitude).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return priceEstimate;
    }




    public static void main(String[] args) {
//        UberConnector.UberRideEstimator estimatorService = UberConnector.UberRideEstimator.generateService();
//        estimatorService.getPriceEstimates(37.7752315f,-122.418075f,37.7752415f,-122.518075f);
    }



}
