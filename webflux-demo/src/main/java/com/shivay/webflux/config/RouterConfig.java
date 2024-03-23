package com.shivay.webflux.config;

import com.shivay.webflux.dto.InputFailedValidationResponse;
import com.shivay.webflux.exception.InputFailedValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Configuration
public class RouterConfig {


    @Autowired
    private RequestHandler requestHandler;

    @Bean
    public RouterFunction<ServerResponse> serverResponseRouterFunction() {

        return RouterFunctions.route()
                .GET("router/square/{input}", requestHandler::squareHandler)
                .GET("router/table/{input}", requestHandler::tableHandler)
                .GET("router/table-stream/{input}", requestHandler::tableStreamHandler)
                .POST("router/multiply", requestHandler::multiplyHandler)
                .GET("router/square/{input}/validation", requestHandler::squareHandlerWithValidation)

                .onError(InputFailedValidationException.class,exceptionHandler())
                .build();
    }

    private BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> exceptionHandler(){

        return (err,req) -> {
            InputFailedValidationException exception = (InputFailedValidationException) err;
            InputFailedValidationResponse response = new InputFailedValidationResponse();

            response.setInput(exception.getInput());
            response.setMessage(exception.getMessage());

            return ServerResponse.badRequest().bodyValue(response);
         };
    }
 }
