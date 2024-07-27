package com.funck.caju.transactionauthorizer.controllers;

import com.funck.caju.transactionauthorizer.controllers.response.TransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.funck.caju.transactionauthorizer.usecases.model.TransactionResponseType.GENERIC_ERROR;

@RestControllerAdvice
public class ExceptionControllerHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TransactionResponse> handleException(Exception ex) {
        return ResponseEntity.ok(new TransactionResponse(GENERIC_ERROR.getCode()));
    }

}
