package com.shivay.webflux.service;


import com.shivay.webflux.dto.MultiplyRequestDTO;
import com.shivay.webflux.dto.Response;
import com.shivay.webflux.util.SleepUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReactiveMathService {

    public Mono<Response> findSquare(int input){
        return Mono.fromSupplier(()-> input * input).map(Response::new);
    }

    public Flux<Response> multiplicationTable(int input){
        return Flux.range(1,10)
                .doOnNext(i-> SleepUtil.sleepSeconds(1))
                .doOnNext(i-> System.out.println("reactive math-service processing:"+i))
                .map(i-> new Response(i*input));
    }

    public Mono<Response> multiply(Mono<MultiplyRequestDTO> requestDTOMono){
        return
                requestDTOMono
                        .map(dto-> dto.getNumber1()*dto.getNumber2())
                        .map(Response::new);
    }
}
