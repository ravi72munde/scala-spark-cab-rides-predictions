
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockTest {

    protected static WireMockConfiguration WIRE_MOCK_CONFIG = wireMockConfig()
            .notifier(new Slf4jNotifier(false))
            .dynamicPort();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_CONFIG);

    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Before
    public void setupData() {
        environmentVariables.set("AWS_ACCESS_KEY_ID", "dummy");
        environmentVariables.set("AWS_SECRET_ACCESS_KEY", "dummy");
        environmentVariables.set("lyft_client_token", "dummy");
        environmentVariables.set("lyft_clientID", "dummy");
        environmentVariables.set("uber_clientId", "dummy");
        environmentVariables.set("uber_token", "dummy");
        environmentVariables.set("WEATHER_API_KEY", "dummy");

    }
}