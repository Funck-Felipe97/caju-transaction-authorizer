package com.funck.caju.transactionauthorizer.controllers;

import com.funck.caju.transactionauthorizer.controllers.request.TransactionRequest;
import com.funck.caju.transactionauthorizer.controllers.response.TransactionResponse;
import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionAuthorizerUseCase transactionAuthorizerUseCase;

    @PostMapping
    public ResponseEntity<TransactionResponse> authorize(@RequestBody @Valid final TransactionRequest transactionRequest) {
        final var validateTransactionCommand = transactionRequest.toValidateTransactionCommand();

        final var transactionResult = transactionAuthorizerUseCase.execute(validateTransactionCommand);

        return ResponseEntity.ok(new TransactionResponse(transactionResult));
    }

}
