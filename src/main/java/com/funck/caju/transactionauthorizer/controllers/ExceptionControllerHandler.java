package com.funck.caju.transactionauthorizer.controllers;

import com.funck.caju.transactionauthorizer.controllers.response.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.funck.caju.transactionauthorizer.usecases.model.TransactionResponseType.GENERIC_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TransactionResponse> handleException(Exception ex) {
        log.error("handling exception: ", ex);

        return ResponseEntity.ok(new TransactionResponse(GENERIC_ERROR.getCode()));
    }

}
