package com.shivay.webflux.config;

import com.shivay.webflux.dto.InputFailedValidationResponse;
import com.shivay.webflux.exception.InputFailedValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Configuration
public class RouterConfig {


    @Autowired
    private RequestHandler requestHandler;
    @Autowired LoggingFilter loggingFilter;

    @Bean
    public RouterFunction<ServerResponse> highLevelRouter(){

        return RouterFunctions
                .route()
                .path("router",this::serverResponseRouterFunction)
                .build();

    }

    @Bean
    public RouterFunction<ServerResponse> serverResponseRouterFunction() {

        return RouterFunctions.route()
                .GET("{input}", RequestPredicates.path("/1?"), requestHandler::squareHandler)
                .GET("{input}", req-> ServerResponse.badRequest().bodyValue("Only 10-19 allowed"))
                .GET("/table/{input}", requestHandler::tableHandler)
                .GET("/table-stream/{input}", requestHandler::tableStreamHandler)
                .POST("/multiply", requestHandler::multiplyHandler)
                .GET("/square/{input}/validation", requestHandler::squareHandlerWithValidation)
                .onError(InputFailedValidationException.class,exceptionHandler())
                .filter(loggingFilter)
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
