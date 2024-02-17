package com.jmlynarz.ksaccountbalance.consts;

import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;

public class Topics {
    public static final String DEPOSIT = "deposit";
    public static final String WITHDRAW = "withdraw";
    public static final String FINANCE = "finance";
    public static final String BALANCE = "balance";

    public static List<NewTopic> TOPICS = List.of(
            new NewTopic(DEPOSIT, 3, (short)1),
            new NewTopic(WITHDRAW,3, (short)1),
            new NewTopic(FINANCE, 3, (short)1),
            new NewTopic(BALANCE, 3, (short)1)
    );
}
