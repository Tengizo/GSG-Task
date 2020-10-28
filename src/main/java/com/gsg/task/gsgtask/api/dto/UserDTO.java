package com.gsg.task.gsgtask.api.dto;

import com.gsg.task.gsgtask.persistance.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String country;
    @Min(1)
    @Max(60)
    private Integer jobInterval;
    private String ytVideoLink;
    private String commentLink;
    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.country = user.getCountry();
        this.jobInterval = user.getJobInterval();
        this.ytVideoLink = user.getYtVideoLink();
        this.commentLink = user.getCommentLink();
    }

    public User toUser() {
        return User.builder()
                .id(id)
                .username(username)
                .country(country)
                .jobInterval(jobInterval)
                .password(password)
                .build();
    }
}
