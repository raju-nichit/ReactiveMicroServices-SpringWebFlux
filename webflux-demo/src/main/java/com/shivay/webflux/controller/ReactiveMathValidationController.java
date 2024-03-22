package com.shivay.webflux.controller;


import com.shivay.webflux.dto.Response;
import com.shivay.webflux.exception.InputFailedValidationException;
import com.shivay.webflux.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("reactive-math-validation")
public class ReactiveMathValidationController {

    @Autowired
    private ReactiveMathService reactiveMathService;

    @GetMapping("square/{input}/throw")
    public Mono<Response> findSquare(@PathVariable("input") int input) {
        if (input < 10 || input > 20) {
            throw new InputFailedValidationException(input);
        }
        return this.reactiveMathService.findSquare(input);
    }

    @GetMapping("square/{input}/mono-error")
    public Mono<Response> monoError(@PathVariable("input") int input) {
        return Mono.just(input)
                .handle((interger, sink) -> {
                    if (interger >= 10 && interger <= 20)
                        sink.next(interger);
                    else
                        sink.error(new InputFailedValidationException(input
                        ));
                }).cast(Integer.class)
                .flatMap(i-> this.reactiveMathService.findSquare(i));

    }
}