package mercadolibre.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MercadoLibreGatewayApplicationTests {

    @LocalServerPort
    int port;
    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }


    @Test
    void contextLoads() {
    }

    @Test
    public void rateLimiterWorks() {
        WebTestClient authClient = client.mutate()
                .filter(basicAuthentication("user", "password"))
                .build();

        boolean wasLimited = false;

        for (int i = 0; i < 80; i++) {
            FluxExchangeResult<Map> result = authClient.get()
                    .uri("/200")
                    .header("Host", "http://httpstat.us")
                    .exchange()
                    .returnResult(Map.class);
            if (result.getStatus().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                System.out.println("Received result: "+result);
                wasLimited = true;
                break;
            }
        }

        assertThat(wasLimited)
                .as("A HTTP 429 TOO_MANY_REQUESTS was not received")
                .isTrue();

    }

}
