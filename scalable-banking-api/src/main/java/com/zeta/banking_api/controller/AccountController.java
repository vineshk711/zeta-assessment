package com.zeta.banking_api.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zeta.banking_api.dto.DebitRequest;
import com.zeta.banking_api.dto.TransactionRequest;
import com.zeta.banking_api.exception.AccountNotFoundException;
import com.zeta.banking_api.exception.InsufficientFundsException;
import com.zeta.banking_api.service.TransactionService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final TransactionService transactionService;

    public AccountController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Endpoint for debiting an account
    @PostMapping("/{accountId}/debit")
    public ResponseEntity<?> debitAccount(@PathVariable Long accountId, @RequestBody DebitRequest debitRequest) {
        try {
            transactionService.debit(accountId, debitRequest.getAmount());
            return ResponseEntity.ok("Debit successful");
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/{accountId}/credit")
    public ResponseEntity<String> credit(@PathVariable Long accountId, @RequestBody TransactionRequest request) {

        try {
            transactionService.credit(accountId, request.getAmount());
            return ResponseEntity.ok("Credit successful");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getBalance(accountId));
    }
}
