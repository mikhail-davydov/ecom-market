package com.mkhldvdv.ecommarket.controller;

import com.mkhldvdv.ecommarket.aspect.Throttling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Controller {

    @GetMapping("/")
    @Throttling
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

// throttling settings below will also work
// rewriting default settings from application.properties
// for the method which the annotation is applied to

//    @GetMapping("/customThrottlingSettings")
//    @Throttling(requestCount = 1, retentionPeriodInMinutes = 1)
//    public ResponseEntity<String> customThrottlingSettings() {
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

}
