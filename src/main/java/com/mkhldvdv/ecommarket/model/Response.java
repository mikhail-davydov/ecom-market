package com.mkhldvdv.ecommarket.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {

    private int statusCode;
    private String statusName;
    private String message;

}
