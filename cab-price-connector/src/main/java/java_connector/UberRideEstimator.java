package java_connector;

import com.uber.sdk.core.client.ServerTokenSession;
import com.uber.sdk.core.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

/**
 * Java class for getting data from UBER API
 */
@Deprecated
public class UberRideEstimator {

    private static final UberRideEstimator serviceInstance = new UberRideEstimator(); //final singleton variable
    private static RidesService rideService;

    /**
     * Create a session config for Uber and service for getting estimates
     * Singleton Implementation
     */
    private UberRideEstimator() {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId(System.getenv("uber_clientId"))
                .setServerToken(System.getenv("uber_token"))
                .build();
        ServerTokenSession session = new ServerTokenSession(config);

        rideService = UberRidesApi.with(session).build().createService();
    }

    /**
     * @param startLatitude  of the source
     * @param startLongitude of the source
     * @param endLatitude    of the destination
     * @param endLongitude   of the destination
     * @return estimated price response from Uber API
     */
    public static PriceEstimatesResponse getPriceEstimates(float startLatitude, float startLongitude, float endLatitude, float endLongitude) {

        PriceEstimatesResponse priceEstimate = null;
        try {
            priceEstimate = rideService
                    .getPriceEstimates(startLatitude, startLongitude, endLatitude, endLongitude)
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return priceEstimate;
    }


}
