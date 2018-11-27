package java_connector;

import com.lyft.networking.ApiConfig;
import com.lyft.networking.LyftApiFactory;
import com.lyft.networking.apiObjects.CostEstimateResponse;
import com.lyft.networking.apis.LyftPublicApi;


/**
 * Java class for getting data from Lyft API
 */
public class LyftRideEstimator {
    private static final LyftRideEstimator serviceInstance = new LyftRideEstimator();
    private static LyftPublicApi rideService;

    /**
     * Create a session config for Lyft and service for getting estimates
     * Singleton Implementation
     */
    private LyftRideEstimator() {
        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId(System.getenv("lyft_clientID"))
                .setClientToken(System.getenv("lyft_client_token"))
                .build();
        rideService = new LyftApiFactory(apiConfig).getLyftPublicApi();
    }

    /**
     * @param startLatitude  of the source
     * @param startLongitude of the source
     * @param endLatitude    of the destination
     * @param endLongitude   of the destination
     * @return estimate price response from Lyft API
     */
    public static CostEstimateResponse getPriceEstimates(float startLatitude, float startLongitude, float endLatitude, float endLongitude) {
        CostEstimateResponse priceEstimate = null;
        try {

            priceEstimate = rideService
                    .getCosts((double) startLatitude, (double) startLongitude, null, (double) endLatitude, (double) endLongitude)
                    .execute()
                    .body();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceEstimate;
    }


}
