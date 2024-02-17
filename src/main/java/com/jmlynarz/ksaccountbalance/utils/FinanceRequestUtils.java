package com.jmlynarz.ksaccountbalance.utils;

import com.jmlynarz.ksaccountbalance.model.DepositRequest;
import com.jmlynarz.ksaccountbalance.model.FinanceOperation;
import com.jmlynarz.ksaccountbalance.model.FinanceOperationType;
import com.jmlynarz.ksaccountbalance.model.FinanceRequest;
import com.jmlynarz.ksaccountbalance.model.WithdrawRequest;

import java.math.BigDecimal;

public class FinanceRequestUtils {
    public static boolean isValidFinanceRequest(FinanceRequest request) {
        return request.amount().compareTo(BigDecimal.ZERO) >= 0;
    }

    public static FinanceOperation mapToFinanceOperation(DepositRequest depositRequest) {
        return new FinanceOperation(
                depositRequest.accountId(),
                depositRequest.amount(),
                FinanceOperationType.DEPOSIT
        );
    }

    public static FinanceOperation mapToFinanceOperation(WithdrawRequest withdrawRequest) {
        return new FinanceOperation(
                withdrawRequest.accountId(),
                withdrawRequest.amount().negate(),
                FinanceOperationType.WITHDRAW
        );
    }
}
