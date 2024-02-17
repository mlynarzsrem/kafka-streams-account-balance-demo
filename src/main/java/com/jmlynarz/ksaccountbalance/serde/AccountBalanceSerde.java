package com.jmlynarz.ksaccountbalance.serde;

import com.jmlynarz.ksaccountbalance.model.AccountBalance;
import org.apache.kafka.common.serialization.Serdes;

public class AccountBalanceSerde extends Serdes.WrapperSerde<AccountBalance> {
    public AccountBalanceSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(AccountBalance.class));
    }
}
