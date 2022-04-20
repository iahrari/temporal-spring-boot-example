package com.github.iahrari.temporal.order.config;

import com.github.iahrari.temporal.api.Shared;
import com.github.iahrari.temporal.api.config.TemporalConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TemporalConfig.class)
public class TemporalOrderConfig {
    @Bean
    public String taskQueue(){
        return Shared.ORDER_TASK_QUEUE;
    }

}
