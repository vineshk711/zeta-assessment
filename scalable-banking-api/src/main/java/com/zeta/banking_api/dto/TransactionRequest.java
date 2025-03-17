package com.zeta.banking_api.dto;

import java.math.BigDecimal;

public class TransactionRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }
}
