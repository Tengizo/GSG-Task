package com.gsg.task.gsgtask.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Bean("app-scheduler")
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler ts= new ThreadPoolTaskScheduler();
        ts.setPoolSize(5);
        return ts;
    }
}
