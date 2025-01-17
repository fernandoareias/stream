package com.fernando.jobs;

import com.fernando.events.ClientCreatedEvent;
import com.fernando.events.CreditCardCreatedEvent;
import com.fernando.events.ProposalCreatedEvent;

import com.fernando.jobs.common.Job;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StreamingEventosClienteCriadoJob extends Job {

    protected static final Logger logger = LoggerFactory.getLogger(StreamingEventosClienteCriadoJob.class);

    private static final String creditCardTopic = "credit-card-created-event";
    private static final String proposalTopic = "proposal-created-event";
    private static final String customerCreatedEventTopic = "client-created-event";

    private static final SpecificAvroSerde<CreditCardCreatedEvent> creditCardCreatedEventSpecificAvroSerde = new SpecificAvroSerde<>();
    private static final SpecificAvroSerde<ProposalCreatedEvent> proposalCreatedEventSpecificAvroSerde = new SpecificAvroSerde<>();
    private static final SpecificAvroSerde<ClientCreatedEvent> clientCreatedEventSpecificAvroSerde = new SpecificAvroSerde<>();


    static {
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "event-created-stream");

        var bootstrapServers = System.getenv("KAFKA_BROKER");

        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            logger.error("A variável de ambiente KAFKA_BROKER não foi definida!");
            System.exit(1);
        }

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                LogAndContinueExceptionHandler.class);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 20);
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

        creditCardCreatedEventSpecificAvroSerde.configure(serdeConfig, false);
        proposalCreatedEventSpecificAvroSerde.configure(serdeConfig, false);
        clientCreatedEventSpecificAvroSerde.configure(serdeConfig, false);
    }

    @Override
    public void startStreaming() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, ProposalCreatedEvent> proposalTable = builder.table(
                proposalTopic,
                Consumed.with(Serdes.String(), proposalCreatedEventSpecificAvroSerde)
        );

        proposalTable.toStream().foreach((key, value) -> logger.info("KTable ProposalCreatedEvent Key=" + key + ", Value=" + value));

        KStream<String, CreditCardCreatedEvent> creditCardStream = builder.stream(
                creditCardTopic,
                Consumed.with(Serdes.String(), creditCardCreatedEventSpecificAvroSerde)
        );

        creditCardStream.foreach((key, value) -> logger.info("KStream CreditCardCreatedEvent Key=" + key + ", Value=" + value));

        KStream<String, ClientCreatedEvent> clientCreatedEventKStream = creditCardStream.leftJoin(
                proposalTable,
                (creditCard, proposal) -> {
                    logger.info("Join attempt - creditCard: {}, proposal: {}", creditCard, proposal);

                    if (creditCard == null || proposal == null)  return null;

                    var event = new ClientCreatedEvent();
                    event.setKey(creditCard.getPortadordocument().toString());
                    event.setCardNumber(creditCard.getCardnumber().toString());
                    event.setProposalNumber(proposal.getProposalnumber().toString());
                    event.setDocument(creditCard.getPortadordocument().toString());
                    event.setProduct(proposal.getProduct().toString());
                    event.setStatus("CREATED");
                    return event;
                },
                Joined.with(Serdes.String(), creditCardCreatedEventSpecificAvroSerde, proposalCreatedEventSpecificAvroSerde)
        );

        clientCreatedEventKStream
                .foreach((key, value) ->
                        logger.info("ClientCreatedEvent Key=" + key + ", Value=" + value)
                );

        clientCreatedEventKStream.filter((key, value) -> value != null).to(customerCreatedEventTopic, Produced.with(Serdes.String(), clientCreatedEventSpecificAvroSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}