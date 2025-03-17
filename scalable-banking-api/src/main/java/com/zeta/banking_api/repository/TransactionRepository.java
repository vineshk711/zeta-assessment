package com.zeta.banking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zeta.banking_api.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
