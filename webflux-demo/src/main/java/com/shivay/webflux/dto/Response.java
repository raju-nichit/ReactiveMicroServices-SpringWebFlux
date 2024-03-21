package com.shivay.webflux.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Response {
    private Date date = new Date();
    private int output;

    public Response(int output) {
        this.output = output;
    }

}
