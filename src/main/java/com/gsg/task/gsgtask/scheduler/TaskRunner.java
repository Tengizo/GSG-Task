package com.gsg.task.gsgtask.scheduler;

import com.gsg.task.gsgtask.external.YTService;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.socket.notification.YTSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TaskRunner implements ApplicationRunner {
    private final UserRepository userRepository;
    private final ScheduleTaskService scheduleTaskService;
    private final YTService ytService;
    private final YTSocketService ytSocketService;

    public TaskRunner(UserRepository userRepository, ScheduleTaskService scheduleTaskService, YTService ytService, YTSocketService ytSocketService) {
        this.userRepository = userRepository;
        this.scheduleTaskService = scheduleTaskService;
        this.ytService = ytService;
        this.ytSocketService = ytSocketService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting Tasks");
        List<User> users = this.userRepository.getAll();
        for (User u : users) {
            runTaskForUser(u);

        }
        log.info("Tasks Started");
    }

    public void addTask(User user) {
        runTaskForUser(user);
    }

    public void removeTask(User user) {
        this.scheduleTaskService.removeTaskFromScheduler(user.getId());
    }

    private void runTaskForUser(User u) {
        UserUpdateTask task = new UserUpdateTask(u, userRepository, ytService, ytSocketService);
        long periodInMills = u.getJobInterval() * 1000 * 60;
        this.scheduleTaskService.addTaskToScheduler(u.getId(), task, periodInMills);
        log.info("Task started for: " + u.getUsername());
    }


}


