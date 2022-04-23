package com.mkhldvdv.ecommarket.exception;

import com.mkhldvdv.ecommarket.model.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ControllerAdvice
public class ThrottlingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ThrottlingException.class)
    protected ResponseEntity<Response> handleThrottlingException(ThrottlingException ex) {
        Response response = Response.builder()
                .statusCode(BAD_GATEWAY.value())
                .statusName(BAD_GATEWAY.getReasonPhrase())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), BAD_GATEWAY);
    }

}
