package com.fernando.jobs;

import com.fernando.events.CDCProposalEvent;
import com.fernando.events.ProposalCreatedEvent;
import com.fernando.jobs.common.Job;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

public class StreamingEventosClienteCriadoJob extends Job {
    private static final String bootstrapServers = "localhost:19091";
    protected static final Logger logger = LoggerFactory.getLogger(StreamingEventosClienteCriadoJob.class);
    private static final String topic = "proposal-created-event.public.proposal";
    private static final String outputTopic = "proposal-created-event";
    private static final StreamsBuilder builder = new StreamsBuilder();
    private static final SpecificAvroSerde<ProposalCreatedEvent> proposalCreatedEventSpecificAvroSerde = new SpecificAvroSerde<>();

    static {

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "cdc-proposal-stream");

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

        proposalCreatedEventSpecificAvroSerde.configure(serdeConfig, false);
    }



    @Override
    public void startStreaming() {

    }


}
