package com.fernando.jobs;

import com.fernando.events.*;
import com.fernando.jobs.common.Job;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

public class StreamingClienteCriadoDividindoPorProdutoJob  extends Job {

    protected static final Logger logger = LoggerFactory.getLogger(StreamingClienteCriadoDividindoPorProdutoJob.class);
    private static final String topic = "client-created-event";

    private static final SpecificAvroSerde<ClientCreatedEvent> clientCreatedEventSpecificAvroSerde = new SpecificAvroSerde<>();

    private static final Map<String, String> outputTopic = new HashMap<>(){{
        put("consignado", "event-new-client-consignado");
        put("private", "event-new-client-private");
    }};


    static {
        var bootstrapServers = System.getenv("KAFKA_BROKER");

        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            logger.error("A variável de ambiente KAFKA_BROKER não foi definida!");
            System.exit(1);
        }

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-cliente-pelo-produto");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                LogAndFailExceptionHandler.class);

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        props.put(StreamsConfig.STATE_DIR_CONFIG, "../../infrastructure/stream-state");
        props.put("allow.auto.create.topics", "true");

        var schemaRegistryUrl = System.getenv("SCHEMA_REGISTRY_URL");

        if (schemaRegistryUrl == null || schemaRegistryUrl.isEmpty()) {
            logger.error("A variável de ambiente SCHEMA_REGISTRY_URL não foi definida!");
            System.exit(1);
        }

        final Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schemaRegistryUrl);
        serdeConfig.put("auto.register.schemas", "true");

        clientCreatedEventSpecificAvroSerde.configure(serdeConfig, false);
    }

    @Override
    public void startStreaming() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ClientCreatedEvent> clientCreatedEventKStream = builder.stream(
                topic, Consumed.with(Serdes.String(), clientCreatedEventSpecificAvroSerde));

        clientCreatedEventKStream.foreach((key, value) -> logger.info("KStream clientCreatedEventKStream Key=" + key + ", Value=" + value));

        KStream<String, ClientCreatedEvent> productAStream = clientCreatedEventKStream.filter(
                (key, value) -> "consignado".equalsIgnoreCase(value.getProduct().toString())
        );

        productAStream.foreach((key, value) -> logger.info("KStream productAStream Key=" + key + ", Value=" + value));

        KStream<String, ClientCreatedEvent> productBStream = clientCreatedEventKStream.filter(
                (key, value) -> "private".equalsIgnoreCase(value.getProduct().toString())
        );

        productAStream.foreach((key, value) -> logger.info("KStream productBStream Key=" + key + ", Value=" + value));

        productAStream.to(outputTopic.get("consignado"), Produced.with(Serdes.String(), clientCreatedEventSpecificAvroSerde));
        productBStream.to(outputTopic.get("private"), Produced.with(Serdes.String(), clientCreatedEventSpecificAvroSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
