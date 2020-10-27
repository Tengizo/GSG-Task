package com.gsg.task.gsgtask.persistance.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String country;
    private Integer jobInterval;
    private String ytVideoLink;
    private String commentLink;
}
