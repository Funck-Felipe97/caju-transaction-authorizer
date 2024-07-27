package com.funck.caju.transactionauthorizer.controllers;

import com.funck.caju.transactionauthorizer.controllers.response.TransactionResponse;
import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
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
    public ResponseEntity<TransactionResponse> authorize(@RequestBody @Valid final ValidateTransactionCommand validateTransactionCommand) {
        final var transactionResult = transactionAuthorizerUseCase.execute(validateTransactionCommand);

        final var response = new TransactionResponse(transactionResult);

        return ResponseEntity.ok(response);
    }

}
