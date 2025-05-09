package com.example.swagger_demo;

import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class LoggingWebFilter implements WebFilter {

    private static final String[] EXCLUDED_PATHS = {
        "/swagger", "/swagger-ui", "/v3/api-docs", "/webjars"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip Swagger and static resources
        for (String exclude : EXCLUDED_PATHS) {
            if (path.startsWith(exclude)) {
                return chain.filter(exchange);
            }
        }

        String requestId = UUID.randomUUID().toString();
        String method = exchange.getRequest().getMethodValue();

        MDC.put("requestId", requestId);
        MDC.put("method", method);
        MDC.put("path", path);

        System.out.printf("➡️ Incoming Request: %s %s [requestId=%s]%n", method, path, requestId);

        ServerHttpRequest decoratedRequest = decorateRequest(exchange);
        ServerHttpResponse decoratedResponse = decorateResponse(exchange, method, path, requestId);

        return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                .doOnError(error -> System.err.printf("❌ Error in Response: %s %s [requestId=%s]: %s%n",
                        method, path, requestId, error.getMessage()))
                .doFinally(signalType -> MDC.clear());
    }

    private ServerHttpRequest decorateRequest(ServerWebExchange exchange) {
        ServerHttpRequest originalRequest = exchange.getRequest();

        return new ServerHttpRequestDecorator(originalRequest) {
            @Override
            public Flux<DataBuffer> getBody() {
                return super.getBody()
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);

                            String body = new String(bytes, StandardCharsets.UTF_8);
                            System.out.printf("➡️ Request Body: %s%n", body);

                            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                            return Mono.just(buffer);
                        });
            }
        };
    }

    private ServerHttpResponse decorateResponse(ServerWebExchange exchange, String method, String path, String requestId) {
        ServerHttpResponse originalResponse = exchange.getResponse();

        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(
                            fluxBody.map(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                String responseBody = new String(content, StandardCharsets.UTF_8);
                                System.out.printf("⬅️ Response Body: %s%n", responseBody);
                                return originalResponse.bufferFactory().wrap(content);
                            })
                    );
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> setComplete() {
                System.out.printf("⬅️ Outgoing Response: %s %s [requestId=%s]%n", method, path, requestId);
                return super.setComplete();
            }
        };
    }
}
