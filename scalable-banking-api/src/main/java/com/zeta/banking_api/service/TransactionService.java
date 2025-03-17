package com.zeta.banking_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeta.banking_api.entity.Account;
import com.zeta.banking_api.entity.Transaction;
import com.zeta.banking_api.exception.AccountNotFoundException;
import com.zeta.banking_api.exception.InsufficientFundsException;
import com.zeta.banking_api.repository.AccountRepository;
import com.zeta.banking_api.repository.TransactionRepository;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
        ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void debit(Long accountId, BigDecimal amount) {
        // Retrieve account with a pessimistic lock to ensure concurrency safety
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        // Check for sufficient funds
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account id: " + accountId);
        }

        // Update balance and persist
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void credit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        transactionRepository.save(new Transaction(account, "CREDIT", amount));
    }

    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId))
                .getBalance();
    }
}

