package com.shivay.webflux.config;

import com.shivay.webflux.dto.MultiplyRequestDTO;
import com.shivay.webflux.dto.Response;
import com.shivay.webflux.exception.InputFailedValidationException;
import com.shivay.webflux.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class RequestHandler {
    @Autowired
    private ReactiveMathService mathService;

    public Mono<ServerResponse> squareHandler(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));

        return ServerResponse.ok().body(this.mathService.findSquare(input), Response.class);
    }

    public Mono<ServerResponse> tableHandler(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));

        return ServerResponse.ok().body(this.mathService.multiplicationTable(input), Response.class);
    }

    public Mono<ServerResponse> tableStreamHandler(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));

        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(this.mathService.multiplicationTable(input), Response.class);
    }

    public Mono<ServerResponse> multiplyHandler(ServerRequest serverRequest) {

        Mono<MultiplyRequestDTO> multiplyRequestDTOMono = serverRequest.bodyToMono(MultiplyRequestDTO.class);

        return ServerResponse
                .ok()
//                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(this.mathService.multiply(multiplyRequestDTOMono), Response.class);
    }


    public Mono<ServerResponse> squareHandlerWithValidation(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));

        if (input < 10 || input > 20) {
            return Mono.error(new InputFailedValidationException(input));
        }

        return ServerResponse.ok().body(this.mathService.findSquare(input), Response.class);
    }

}
