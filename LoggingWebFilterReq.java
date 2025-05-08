package com.example.swagger_demo;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.*;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name()
                : "UNKNOWN";
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        MDC.put("requestId", requestId);
        MDC.put("method", method);
        MDC.put("path", path);

        System.out.printf("➡️ Incoming Request: %s %s [requestId=%s]%n", method, path, requestId);

        return DataBufferUtils.join(exchange.getRequest().getBody())
            .flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);

                String requestBody = new String(bytes, StandardCharsets.UTF_8);
                System.out.printf("➡️ Request Body: %s%n", requestBody);

                // Wrap the bytes into new buffer so it can be re-read downstream
                DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                Flux<DataBuffer> cachedBodyFlux = Flux.defer(() -> {
                    DataBuffer buffer = bufferFactory.wrap(bytes);
                    return Mono.just(buffer);
                });

                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedBodyFlux;
                    }
                };

                ServerHttpResponse decoratedResponse = decorateResponse(exchange, method, path, requestId);

                return chain.filter(exchange.mutate()
                        .request(mutatedRequest)
                        .response(decoratedResponse)
                        .build())
                    .doOnError(error -> System.err.printf("❌ Error in Response: %s %s [requestId=%s]: %s%n",
                            method, path, requestId, error.getMessage()))
                    .doFinally(signalType -> MDC.clear());
            });
    }

    private ServerHttpResponse decorateResponse(ServerWebExchange exchange, String method, String path, String requestId) {
        ServerHttpResponse originalResponse = exchange.getResponse();

        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
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
