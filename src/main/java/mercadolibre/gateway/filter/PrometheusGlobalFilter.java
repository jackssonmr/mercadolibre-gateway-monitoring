package mercadolibre.gateway.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PrometheusGlobalFilter implements GlobalFilter {
    private final MeterRegistry meterRegistry;

    @Autowired
    public PrometheusGlobalFilter(MeterRegistry meterRegistry){
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Counter.builder("gateway.requests.total")
                .description("Cantidad solicitudes recibidas")
                .tag("method", exchange.getRequest().getMethodValue())
                .tag("path", exchange.getRequest().getPath().toString())
                .register(this.meterRegistry)
                .increment();


        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {

                    if(exchange.getResponse().getRawStatusCode() != null){
                        int statusCode = exchange.getResponse().getRawStatusCode();

                        Counter.builder("gateway.responses.total")
                                .description("Cantidad respuesta gestionadas")
                                .tag("status", Integer.toString(statusCode))
                                .register(this.meterRegistry)
                                .increment();
                    }
                }));
    }
}
