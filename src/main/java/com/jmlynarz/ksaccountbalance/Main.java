package com.jmlynarz.ksaccountbalance;

import com.jmlynarz.ksaccountbalance.consts.Topics;
import com.jmlynarz.ksaccountbalance.model.AccountBalance;
import com.jmlynarz.ksaccountbalance.model.DepositRequest;
import com.jmlynarz.ksaccountbalance.model.FinanceOperation;
import com.jmlynarz.ksaccountbalance.model.WithdrawRequest;
import com.jmlynarz.ksaccountbalance.serde.AccountBalanceSerde;
import com.jmlynarz.ksaccountbalance.serde.DepositRequestSerde;
import com.jmlynarz.ksaccountbalance.serde.FinanceOperationSerde;
import com.jmlynarz.ksaccountbalance.serde.WithdrawRequestSerde;
import com.jmlynarz.ksaccountbalance.utils.FinanceRequestUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.math.BigDecimal;
import java.util.Properties;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.REPLICATION_FACTOR_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;

public class Main {
    private static final String BOOTSTRAP_ADDRESS = "localhost:29092";
    private static final String APP_NAME = "account-balance";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(APPLICATION_ID_CONFIG, APP_NAME);
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(REPLICATION_FACTOR_CONFIG, 1);
        props.put(COMMIT_INTERVAL_MS_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, DepositRequest> depositStream = streamsBuilder.stream(Topics.DEPOSIT, Consumed.with(
                Serdes.String(),
                new DepositRequestSerde()
        )).filter((key, val) -> FinanceRequestUtils.isValidFinanceRequest(val));

        KStream<String, WithdrawRequest> withdrawStream = streamsBuilder.stream(Topics.WITHDRAW, Consumed.with(
                Serdes.String(),
                new WithdrawRequestSerde()
        )).filter((key, val) -> FinanceRequestUtils.isValidFinanceRequest(val));

        KStream<String, FinanceOperation> depositStreamToFOP = depositStream.mapValues(FinanceRequestUtils::mapToFinanceOperation);
        /*
         * In theory map function can be used instead of map value, but we shouldn't use it if we don't need to change a key
         * Following code triggers repartitioning
         * depositStream.map((k, v) -> KeyValue.pair(k, FinanceRequestUtils.mapToFinanceOperation(v)))
         */
        KStream<String, FinanceOperation> withdrawStreamToFOP = withdrawStream.mapValues(FinanceRequestUtils::mapToFinanceOperation);
        KStream<String, FinanceOperation> mergedFinanceOperation = depositStreamToFOP.merge(withdrawStreamToFOP).selectKey((k, v) -> v.accountId().toString());

        mergedFinanceOperation.to(
                Topics.FINANCE,
                Produced.with(Serdes.String(), new FinanceOperationSerde()));

       KTable<String, AccountBalance> balances = mergedFinanceOperation.groupByKey(
                Grouped.with(Serdes.String(), new FinanceOperationSerde())
        ).aggregate(
                () -> new AccountBalance(0L, BigDecimal.ZERO, 0L),
                (k, v, aggV) -> new AccountBalance(v.accountId(), aggV.balance().add(v.amount()), aggV.operationsCount() + 1),
                Materialized.with(Serdes.String(), new AccountBalanceSerde())
        );
        balances.toStream().to(
                Topics.BALANCE,
                Produced.with(Serdes.String(), new AccountBalanceSerde())
        );

        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe());
        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);

        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }
}