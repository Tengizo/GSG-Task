package com.gsg.task.gsgtask.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTToken {
    @JsonProperty("token")
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }
}
