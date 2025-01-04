package com.fernando.jobs.common;

import com.fernando.jobs.StreamingCartaoCriadoEventJob;
import com.fernando.jobs.StreamingEventosClienteCriadoJob;
import com.fernando.jobs.StreamingPropostaCriadaEventJob;

import java.util.Objects;

public class JobFactory {
    public static Job getJob(String name){

        if(Objects.equals(name.trim(), "streaming-proposta-criada-event"))
            return new StreamingPropostaCriadaEventJob();

        if(Objects.equals(name.trim(), "streaming-cartao-criado-event"))
            return new StreamingCartaoCriadoEventJob();

        if(Objects.equals(name.trim(), "client-created-event"))
            return new StreamingEventosClienteCriadoJob();

        return null;
    }
}
