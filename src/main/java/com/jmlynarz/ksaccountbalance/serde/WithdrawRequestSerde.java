package com.jmlynarz.ksaccountbalance.serde;

import com.jmlynarz.ksaccountbalance.model.WithdrawRequest;
import org.apache.kafka.common.serialization.Serdes;

public class WithdrawRequestSerde extends Serdes.WrapperSerde<WithdrawRequest> {
    public WithdrawRequestSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(WithdrawRequest.class));
    }
}
