package com.jmlynarz.ksaccountbalance.serde;

import com.jmlynarz.ksaccountbalance.model.DepositRequest;
import org.apache.kafka.common.serialization.Serdes;

public class DepositRequestSerde extends Serdes.WrapperSerde<DepositRequest> {
    public DepositRequestSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(DepositRequest.class));
    }
}
