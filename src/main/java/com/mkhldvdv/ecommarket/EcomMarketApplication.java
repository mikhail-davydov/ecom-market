package com.mkhldvdv.ecommarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class EcomMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomMarketApplication.class, args);
    }

}
