package com.gsg.task.gsgtask.scheduler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduleTaskService {

    // Task Scheduler
    private final TaskScheduler scheduler;

    // A map for keeping scheduled tasks
    Map<Long, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public ScheduleTaskService(@Qualifier("app-scheduler") TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * adds new task in the scheduler
     */
    public void addTaskToScheduler(Long id, Runnable task, long period) {
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, new PeriodicTrigger(period));
        jobsMap.put(id, scheduledTask);
    }

    // Remove scheduled task
    public void removeTaskFromScheduler(Long id) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(id);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            jobsMap.remove(id, null);
        }
    }
}
