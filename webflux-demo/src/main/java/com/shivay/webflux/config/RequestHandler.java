package com.shivay.webflux.config;

import com.shivay.webflux.dto.Response;
import com.shivay.webflux.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class RequestHandler {
    @Autowired
    private ReactiveMathService mathService;

    public Mono<ServerResponse> squareHandler(ServerRequest serverRequest){
       int input = Integer.parseInt(serverRequest.pathVariable("input"));

       return ServerResponse.ok().body(this.mathService.findSquare(input), Response.class);
    }

    public Mono<ServerResponse> tableHandler(ServerRequest serverRequest){
        int input = Integer.parseInt(serverRequest.pathVariable("input"));

        return ServerResponse.ok().body(this.mathService.multiplicationTable(input), Response.class);
    }


}
