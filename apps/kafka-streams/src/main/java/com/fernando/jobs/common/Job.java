package com.fernando.jobs.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public abstract class Job {
    protected static final Properties props = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(Job.class);

//    protected static final SpecificAvroSerde<ClientCreatedEvent> clientCreatedSerde = new SpecificAvroSerde<>();


    public abstract void startStreaming();
}
