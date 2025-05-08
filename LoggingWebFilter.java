import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Slf4j
@Component
@Order(-1)
public class LoggingWebFilter implements WebFilter {

    private static final String MDC_REQUEST_ID = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        String path = exchange.getRequest().getURI().toString();
        String method = exchange.getRequest().getMethodValue();

        return chain.filter(exchange)
                .doOnEach(signal -> {
                    if (signal.isOnComplete() || signal.isOnError() || signal.isOnNext()) {
                        MDC.put(MDC_REQUEST_ID, requestId);
                        log.info("Request: {} {} | Response status: {}", method, path,
                                exchange.getResponse().getStatusCode() != null ?
                                        exchange.getResponse().getStatusCode().value() : "unknown");
                        MDC.clear();
                    }
                })
                .contextWrite(Context.of(MDC_REQUEST_ID, requestId));
    }
}
