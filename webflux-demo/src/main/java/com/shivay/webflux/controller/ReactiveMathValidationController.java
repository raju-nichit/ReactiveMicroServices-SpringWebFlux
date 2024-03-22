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

    @GetMapping("square/{input}")
    public Mono<Response> findSquare(@PathVariable("input") int input) {
        if(input<10 || input >20){
            throw new InputFailedValidationException(input);
        }
        return this.reactiveMathService.findSquare(input);
    }
}
