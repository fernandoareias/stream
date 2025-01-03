package com.fernando;

import com.fernando.jobs.common.Job;
import com.fernando.jobs.common.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final JobFactory factory = new JobFactory();
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        log.info("Iniciando projeto...");
        log.info("Job selecionado = 'streaming-eventos-inicias'");

        Job job = factory.getJob("streaming-eventos-inicias");

        if(job == null)
            throw new NullPointerException("Nao foi possivel encontrar o job informado.");

        job.startStreaming();

    }
}