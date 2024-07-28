package com.funck.caju.transactionauthorizer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funck.caju.transactionauthorizer.controllers.response.TransactionResponse;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResponseType;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(dataSource = "dataSource", transactionManager = "transactionManager")),
        @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(dataSource = "dataSource", transactionManager = "transactionManager")),
        @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, config = @SqlConfig(dataSource = "dataSource", transactionManager = "transactionManager"))
})
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setupSchema() {

    }

    @BeforeEach
    public void setupData() {

    }

    @AfterEach
    public void cleanupData() {

    }

    @Test
    @DisplayName("Should approve transaction when balance is enough")
    void testApproveTransaction() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = MEAL
         * TRANSACTION = 100
         */

        // a
        final var command = new ValidateTransactionCommand(1L,  BigInteger.valueOf(100), "5811", "1234");
        final var response = new TransactionResponse(TransactionResponseType.APPROVED.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should approve transaction when balance for mcc plus cash is enough")
    void testApproveTransaction2() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = MEAL
         * TRANSACTION = 100
         */

        // a
        final var command = new ValidateTransactionCommand(1L,  BigInteger.valueOf(499), "5811", "1234");
        final var response = new TransactionResponse(TransactionResponseType.APPROVED.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should approve transaction when cash balance is enough")
    void testApproveTransaction3() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = CASH
         * TRANSACTION = 300
         */

        // a
        final var command = new ValidateTransactionCommand(1L,  BigInteger.valueOf(300), "5555", "1234");
        final var response = new TransactionResponse(TransactionResponseType.APPROVED.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should reject transaction when balance is not enough")
    void testRejectTransaction() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = MEAL
         * TRANSACTION = 501
         */

        // a
        final var command = new ValidateTransactionCommand(1L, BigInteger.valueOf(501), "5811", "1234");
        final var response = new TransactionResponse(TransactionResponseType.REJECTED.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should reject transaction when cash balance is not enough")
    void testRejectTransaction2() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = CASH
         * TRANSACTION = 301
         */

        // a
        final var command = new ValidateTransactionCommand(1L, BigInteger.valueOf(301), "5555", "1234");
        final var response = new TransactionResponse(TransactionResponseType.REJECTED.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should return generic error code when transaction value is not positive")
    void testReturnGenericErrorCode() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = CASH
         * TRANSACTION = -1
         */

        // a
        final var command = new ValidateTransactionCommand(1L, BigInteger.valueOf(-1), "5555", "1234");
        final var response = new TransactionResponse(TransactionResponseType.GENERIC_ERROR.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should return generic error code when account is null")
    void testReturnGenericErrorCode2() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = CASH
         * TRANSACTION = 100
         */

        // a
        final var command = new ValidateTransactionCommand(null, BigInteger.valueOf(100), "5555", "1234");
        final var response = new TransactionResponse(TransactionResponseType.GENERIC_ERROR.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should return generic error code when mcc is empty")
    void testReturnGenericErrorCode3() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = CASH
         * TRANSACTION = 100
         */

        // a
        final var command = new ValidateTransactionCommand(1L, BigInteger.valueOf(100), "  ", "1234");
        final var response = new TransactionResponse(TransactionResponseType.GENERIC_ERROR.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should return generic error code when merchant is empty")
    void testReturnGenericErrorCod4() throws Exception {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = CASH
         * TRANSACTION = 100
         */

        // a
        final var command = new ValidateTransactionCommand(1L, BigInteger.valueOf(100), "5555", "   ");
        final var response = new TransactionResponse(TransactionResponseType.GENERIC_ERROR.getCode());

        // a a
        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

}