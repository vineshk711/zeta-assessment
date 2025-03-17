package com.zeta.banking_api.dto;

import java.math.BigDecimal;

public class DebitRequest {
    private BigDecimal amount;

    // Getter and Setter
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
