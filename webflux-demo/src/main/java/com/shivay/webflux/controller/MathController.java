package com.shivay.webflux.controller;

import com.shivay.webflux.dto.Response;
import com.shivay.webflux.service.MathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("math")
public class MathController {
    @Autowired
    private MathService mathService;

    @GetMapping("square/{input}")
    public Response findSqaure(@PathVariable("input") int input){
        return  this.mathService.findSquare(input);
    }

    @GetMapping("table/{input}")
    public List<Response> multiplocationTable(@PathVariable("input") int input){
        return this.mathService.multiplicationTable(input);
    }
}
