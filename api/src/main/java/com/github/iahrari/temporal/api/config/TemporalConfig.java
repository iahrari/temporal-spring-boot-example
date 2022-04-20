package com.github.iahrari.temporal.api.config;

import com.github.iahrari.temporal.api.annotations.TemporalActivity;
import com.github.iahrari.temporal.api.annotations.TemporalWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class TemporalConfig {

    @Bean
    public WorkflowOptions workflowOptions(String taskQueue){
        return WorkflowOptions
                .newBuilder()
                .setTaskQueue(taskQueue)
                .build();
    }

    @Bean
    public WorkflowClient workflowClient(){
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        return WorkflowClient.newInstance(service);
    }

    @Bean
    @Qualifier("workflowTypes")
    public Set<Class<?>> workflowTypes() throws ClassNotFoundException{
        var set = new HashSet<Class<?>>();
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents("com.github.iahrari.temporal")) {
            set.add(Class.forName(beanDef.getBeanClassName()));
        }
        return set;
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(TemporalWorkflow.class));
        return provider;
    }

    @Bean
    public CommandLineRunner worker(
            WorkflowClient workflowClient,
            Set<Class<?>> workflowTypes,
            @TemporalActivity List<Object> activities,
            String taskQueue
    ){
        return args -> {
            WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
            Worker worker = factory.newWorker(taskQueue);
            worker.registerWorkflowImplementationTypes(workflowTypes.toArray(new Class[0]));
            worker.registerActivitiesImplementations(activities.toArray(activities.toArray()));
            factory.start();
        };
    }
}
