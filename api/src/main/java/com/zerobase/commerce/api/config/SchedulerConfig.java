package com.zerobase.commerce.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        var threadPool = new ThreadPoolTaskScheduler();
        var n = Runtime.getRuntime().availableProcessors();
        threadPool.setPoolSize(n);
        threadPool.initialize();
        taskRegistrar.setTaskScheduler(threadPool);
    }
}
