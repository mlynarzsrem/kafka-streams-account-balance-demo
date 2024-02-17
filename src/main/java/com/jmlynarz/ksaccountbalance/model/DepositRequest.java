package com.jmlynarz.ksaccountbalance.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record DepositRequest(
        Long accountId,
        BigDecimal amount
) implements Serializable, FinanceRequest {
}
