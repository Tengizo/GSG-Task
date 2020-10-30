package com.gsg.task.gsgtask.scheduler;

import com.gsg.task.gsgtask.external.YTService;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.socket.notification.YTSocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.stream.Collectors;

import static com.gsg.task.gsgtask.utils.TestUtils.getTestUser;
import static com.gsg.task.gsgtask.utils.TestUtils.getUserList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskRunnerTest {
    @Mock
    private ApplicationArguments appArgs;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleTaskService scheduleTaskService;
    @Mock
    private YTService ytService;
    @Mock
    private YTSocketService ytSocketService;
    @InjectMocks
    private TaskRunner taskRunner;
    @Captor
    ArgumentCaptor<Long> idCaptor;
    @Captor
    ArgumentCaptor<Long> intervalCaptor;
    @Captor
    ArgumentCaptor<UserUpdateTask> taskCaptor;
    private final long MINUTE = 1000 * 60;

    @Test
    void testAddTask() {
        User user = getTestUser();
        this.taskRunner.addTask(user);
        verify(this.scheduleTaskService).addTaskToScheduler(idCaptor.capture(), taskCaptor.capture(), intervalCaptor.capture());
        assertEquals(idCaptor.getValue(), user.getId());
        UserUpdateTask task = taskCaptor.getValue();
        assertEquals(task.getTaskUser(), user);
        assertEquals(intervalCaptor.getValue(), user.getJobInterval() * MINUTE);
    }

    @Test
    void testRun() {
        List<User> userList = getUserList();
        when(this.userRepository.getAll()).thenReturn(userList);
        this.taskRunner.run(appArgs);

        verify(this.scheduleTaskService, times(userList.size())).addTaskToScheduler(idCaptor.capture(), taskCaptor.capture(), intervalCaptor.capture());
        assertEquals(idCaptor.getAllValues(), userList.stream().map(User::getId).collect(Collectors.toList()));
        assertEquals(intervalCaptor.getAllValues(), userList.stream().map(e -> e.getJobInterval() * MINUTE).collect(Collectors.toList()));
        assertEquals(taskCaptor.getAllValues().stream().map(UserUpdateTask::getTaskUser).collect(Collectors.toList()), userList);

    }
}
