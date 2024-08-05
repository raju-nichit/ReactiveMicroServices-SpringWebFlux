package com.shivay.webflux.controller;

import com.shivay.webflux.BaseTest;
import com.shivay.webflux.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MathServiceTest extends BaseTest {

    @Autowired
    private WebClient webClient;


    @Test
    public void squareEndpointTest(){


        Mono<Response> response = this.webClient
                .get()
                .uri("reactive-math/square/{number}",5)
                .retrieve()
                .bodyToMono(Response.class);

        StepVerifier.create(response)
                .expectNextMatches(r-> r.getOutput() ==25)
                .verifyComplete();
    }

    @Test
    public void multiplyEndpointTest(){


        Flux<Response> response = this.webClient
                .get()
                .uri("reactive-math/table/{input}",5)
                .retrieve()
                .bodyToFlux(Response.class);

        StepVerifier.create(response)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    public void streamEndpointTesting(){


        Flux<Response> response = this.webClient
                .get()
                .uri("reactive-math/table/{input}/stream",5)
                .retrieve()
                .bodyToFlux(Response.class)
                .doOnNext(System.out::println);


        StepVerifier.create(response)
                .expectNextCount(10)
                .verifyComplete();
    }
}
