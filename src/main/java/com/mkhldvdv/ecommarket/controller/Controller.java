package com.mkhldvdv.ecommarket.controller;

import com.mkhldvdv.ecommarket.aop.Throttling;
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
}
