package com.gsg.task.gsgtask.scheduler;

import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduleTaskService {

    private UserRepository userRepository;
    // Task Scheduler
    private final TaskScheduler scheduler;

    // A map for keeping scheduled tasks
    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public ScheduleTaskService(@Qualifier("app-scheduler")TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }


    // Schedule Task to be executed every night at 00 or 12 am
    public void addTaskToScheduler(Long id, Runnable task, long period) {
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, new PeriodicTrigger(period));
        jobsMap.put(id, scheduledTask);
    }

    // Remove scheduled task
    public void removeTaskFromScheduler(Long id) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(id);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            jobsMap.put(id, null);
        }
    }

    // A context refresh event listener
    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() {
        // Get all tasks from DB and reschedule them in case of context restarted
    }
}
