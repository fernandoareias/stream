package com.fernando.jobs.common;

import com.fernando.jobs.StreamingEventosIniciaisJob;

import java.util.Objects;

public class JobFactory {
    public static Job getJob(String name){

        if(Objects.equals(name.trim(), "streaming-eventos-inicias"))
            return new StreamingEventosIniciaisJob();

        return null;
    }
}
