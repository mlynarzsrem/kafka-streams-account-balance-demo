package com.jmlynarz.ksaccountbalance.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record AccountBalance(
        Long accountId,
        BigDecimal balance,
        Long operationsCount
) implements Serializable {
}
