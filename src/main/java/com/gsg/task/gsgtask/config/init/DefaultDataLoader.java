package com.gsg.task.gsgtask.config.init;

import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultDataLoader implements ApplicationRunner {
    private final UserRepository userRepository;

    public DefaultDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting default Loader");
        loadStarterUsers();
        log.info("starter users loaded");

    }

    private void loadStarterUsers() {
        User u1 = User.builder()
                .username("test_user1")
                .country("Georgia")
                .password("test")
                .jobInterval(30)
                .build();
        User u2 = User.builder()
                .username("test_user2")
                .country("Germany")
                .password("test")
                .jobInterval(30)
                .build();
        if (this.userRepository.getUserByUsername(u1.getUsername()).isEmpty()) {
            this.userRepository.addUser(u1);
        }
        if (this.userRepository.getUserByUsername(u2.getUsername()).isEmpty()) {
            this.userRepository.addUser(u2);
        }
    }

}


