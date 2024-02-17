package com.jmlynarz.ksaccountbalance.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record FinanceOperation(
        Long accountId,
        BigDecimal amount,
        FinanceOperationType type
) implements Serializable  {
}
