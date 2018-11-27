
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Rule;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockTest {

    protected static WireMockConfiguration WIRE_MOCK_CONFIG = wireMockConfig()
            .notifier(new Slf4jNotifier(false))
            .dynamicPort();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_CONFIG);
}