package com.gsg.task.gsgtask.api.errors.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Problem implements Serializable {
    private String keyword;
    private String details;
    private int status;
}
