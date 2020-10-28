package com.gsg.task.gsgtask.config.init;

import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.service.UserService;
import com.gsg.task.helper.CountryHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultDataLoader implements ApplicationRunner {
    private final UserService userService;

    public DefaultDataLoader(UserService userService) {
        this.userService = userService;
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
                .country(CountryHelper.getAllCountryCodes().get(15))
                .password("test")
                .jobInterval(30)
                .build();
        User u2 = User.builder()
                .username("test_user2")
                .country(CountryHelper.getAllCountryCodes().get(50))
                .password("test")
                .jobInterval(30)
                .build();
        if (this.userService.getUser(u1.getUsername()) == null) {
            this.userService.addUser(u1);
        }
        if (this.userService.getUser(u2.getUsername()) == null) {
            this.userService.addUser(u2);
        }
    }

}


