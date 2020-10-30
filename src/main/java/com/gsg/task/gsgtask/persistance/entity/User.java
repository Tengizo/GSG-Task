package com.gsg.task.gsgtask.persistance.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String country;
    private Integer jobInterval;
    private String ytVideoLink;
    private String commentLink;
}
