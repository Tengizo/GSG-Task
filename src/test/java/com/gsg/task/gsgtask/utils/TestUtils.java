package com.gsg.task.gsgtask.utils;

import com.gsg.task.gsgtask.api.dto.UserDTO;
import com.gsg.task.gsgtask.persistance.entity.User;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static User getTestUser() {
        return User.builder().
                id(10L)
                .username("test")
                .jobInterval(10)
                .country("GE")
                .password("test_pass")
                .commentLink("test_link_comment")
                .ytVideoLink("test_link_video")
                .build();
    }

    public static User getTestUserWithId(Long id) {
        User u = getTestUser();
        u.setId(id);
        return u;
    }

    public static UserDTO getTestUserDTO() {
        return new UserDTO(getTestUser());
    }

    public static List<User> getUserList() {
        List<User> list = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            list.add(getTestUserWithId(i));
        }
        return list;
    }
}
