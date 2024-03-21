package com.shivay.webflux.controller;

import com.shivay.webflux.dto.Response;
import com.shivay.webflux.service.ReactiveMathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("reactive-math")
public class ReactiveMathController {

    @Autowired
    private ReactiveMathService reactiveMathService;

    @GetMapping("square/{input}")
    public Mono<Response> findSqaure(@PathVariable("input") int input){

        return  this.reactiveMathService.findSquare(input);
    }

    @GetMapping("table/{input}")
    public Flux<Response> multiplicationTable(@PathVariable("input") int input){
        return this.reactiveMathService.multiplicationTable(input);
    }
}
