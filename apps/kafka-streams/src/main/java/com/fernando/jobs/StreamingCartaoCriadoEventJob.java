package com.fernando.jobs;


import com.fernando.events.CDCCreditCardEvent;
import com.fernando.events.CDCProposalEvent;
import com.fernando.events.CreditCardCreatedEvent;
import com.fernando.events.ProposalCreatedEvent;
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


public class StreamingCartaoCriadoEventJob extends Job {

    private static final String bootstrapServers = "localhost:19091";
    protected static final Logger logger = LoggerFactory.getLogger(StreamingCartaoCriadoEventJob.class);
    private static final String topic = "credit-card-created-event.public.credit_card";
    private static final String outputTopic = "credit-card-created-event";
    private static final StreamsBuilder builder = new StreamsBuilder();
    private static final JsonSerde<CDCCreditCardEvent> cdcCreditCardEventJsonSerde = new JsonSerde<>(CDCCreditCardEvent.class);
    private static final SpecificAvroSerde<CreditCardCreatedEvent> creditCardCreatedEventSpecificAvroSerde = new SpecificAvroSerde<>();

    static {

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "cdc-credit-card-stream");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                LogAndFailExceptionHandler.class);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        props.put(StreamsConfig.STATE_DIR_CONFIG, "/app/kafka-stream-state");
        props.put("allow.auto.create.topics", "true");


        final Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", "http://localhost:8081");
        serdeConfig.put("auto.register.schemas", "true");

        creditCardCreatedEventSpecificAvroSerde.configure(serdeConfig, false);
        cdcCreditCardEventJsonSerde.configure(serdeConfig, false);
    }

    @Override
    public void startStreaming() {

        KStream<String, CDCCreditCardEvent> clientCreditCardKStream = builder.stream(topic,
                Consumed.with(
                        Serdes.String(),
                        cdcCreditCardEventJsonSerde
                )
        );

        clientCreditCardKStream
                .peek((key, value) -> logger.info(
                        String.format("Consuming event credit card by CDC: %s", value)
                    )
                )
                .map((key, value) -> {

                    CreditCardCreatedEvent avroEvent = new CreditCardCreatedEvent(
                                value.getKey(),
                                value.getCardnumber(),
                                value.getPortadordocument(),
                                value.getStatus()
                        );

                        return new KeyValue<>(avroEvent.getPortadordocument().toString(), avroEvent);

                })
                .to(outputTopic, Produced.with(Serdes.String(), creditCardCreatedEventSpecificAvroSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
