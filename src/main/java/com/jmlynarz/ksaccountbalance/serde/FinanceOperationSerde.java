package com.jmlynarz.ksaccountbalance.serde;

import com.jmlynarz.ksaccountbalance.model.FinanceOperation;
import org.apache.kafka.common.serialization.Serdes;

public class FinanceOperationSerde extends Serdes.WrapperSerde<FinanceOperation> {
    public FinanceOperationSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(FinanceOperation.class));
    }
}
