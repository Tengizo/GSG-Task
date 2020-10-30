package com.gsg.task.gsgtask.service;

import com.gsg.task.gsgtask.api.dto.UserDTO;
import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.scheduler.TaskRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.gsg.task.gsgtask.utils.TestUtils.getTestUser;
import static com.gsg.task.gsgtask.utils.TestUtils.getTestUserDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder pEncoder;
    @Mock
    private TaskRunner taskRunner;

    @InjectMocks
    private UserService userService;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;


    @Test
    void add_whenUsernameIsUsed() {
        User user = getTestUser();
        when(this.userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        AppException exception = assertThrows(AppException.class, () -> userService.addUser(user));
        assertEquals(exception.getType(), ExceptionType.USERNAME_IS_USED);
    }

    @Test
    void add_whenCountryIsInvalid() {
        when(this.userRepository.getUserByUsername(any(String.class))).thenReturn(Optional.empty());
        UserDTO user = getTestUserDTO();
        whenCountryIsInvalid(() -> userService.addUser(user), user);
    }

    @Test
    void add_whenAllRight() {
        when(this.userRepository.getUserByUsername(any(String.class))).thenReturn(Optional.empty());
        when(this.userRepository.addUser(any(User.class))).then(returnsFirstArg());
        User user = getTestUser();
        String pass = user.getPassword();
        userService.addUser(user);
        verify(this.pEncoder).encode(pass);
        verify(this.userRepository).addUser(user);
        verify(this.taskRunner).addTask(user);
    }

    @Test
    void update_whenUserNotFound() {
        when(this.userRepository.getUserById(any(Long.class))).thenReturn(Optional.empty());
        AppException exception = assertThrows(AppException.class, () -> userService.updateUser(getTestUserDTO()));
        assertEquals(exception.getType(), ExceptionType.USER_NOT_FOUND);
    }


    @Test
    void update_whenCountryIsInvalid() {
        when(this.userRepository.getUserById(any(Long.class))).thenReturn(Optional.of(getTestUser()));
        UserDTO user = getTestUserDTO();
        whenCountryIsInvalid(() -> userService.updateUser(user), user);
    }

    @Test
    void update_whenAllRight() {
        when(this.userRepository.getUserById(any(Long.class))).thenReturn(Optional.of(new User()));
        doNothing().when(this.userRepository).updateUser(userArgumentCaptor.capture());
        UserDTO dto = getTestUserDTO();
        userService.updateUser(dto);
        User updated = userArgumentCaptor.getValue();
        assertEquals(updated.getCountry(), dto.getCountry());
        assertEquals(updated.getJobInterval(), dto.getJobInterval());
        assertNull(updated.getId());
        assertNull(updated.getPassword());
        assertNull(updated.getYtVideoLink());
        assertNull(updated.getUsername());
    }

    private void whenCountryIsInvalid(Executable executable, UserDTO user) {
        user.setCountry("NOT_VALID_COUNTRY_CODE");
        AppException exception = assertThrows(AppException.class, executable);
        assertEquals(exception.getType(), ExceptionType.INVALID_COUNTRY);
    }


}
