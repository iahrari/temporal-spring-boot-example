package com.github.iahrari.temporal.driver.config;

import com.github.iahrari.temporal.api.Shared;
import com.github.iahrari.temporal.api.config.TemporalConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TemporalConfig.class)
public class TemporalDriverConfig {
    @Bean
    public String taskQueue(){
        return Shared.DRIVER_TASK_QUEUE;
    }
}