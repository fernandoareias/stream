package com.fernando;

import com.fernando.jobs.common.Job;
import com.fernando.jobs.common.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final JobFactory factory = new JobFactory();
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String jobName = System.getenv("JOB_NAME");

        if (jobName == null || jobName.isEmpty()) {
            log.error("A variável de ambiente JOB_NAME não foi definida!");
            System.exit(1);
        }


        log.info("Iniciando projeto...");
        log.info("Job selecionado = {}", jobName);

        Job job = factory.getJob(jobName);

        if(job == null)
            throw new NullPointerException("Nao foi possivel encontrar o job informado.");

        job.startStreaming();

    }
}