package com.github.iahrari.temporal.allocation.config;

import com.github.iahrari.temporal.api.Shared;
import com.github.iahrari.temporal.api.config.TemporalConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TemporalConfig.class)
public class TemporalAllocationConfig {

    @Bean
    public String taskQueue(){
        return Shared.ALLOCATION_TASK_QUEUE;
    }
}
