package UberJavaConnector;

import Models.Location;
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

    public static PriceEstimatesResponse getPriceEstimates(Location source,Location destination ){

        PriceEstimatesResponse priceEstimate =null;
        try {
            priceEstimate= rideService
                    .getPriceEstimates(source.latitude(),source.longitude(),destination.latitude(),source.longitude())
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return priceEstimate;
    }




}
