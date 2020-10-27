package com.gsg.task.gsgtask.api.errors.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException {
    private ExceptionType type;

    public AppException(ExceptionType type) {
        super(type.getKeyWord());
        this.type = type;
    }

    public String getExceptionKeyWord() {
        return type.getKeyWord();
    }

    public int getStatus() {
        return type.getStatus();
    }

    public ExceptionType getType() {
        return type;
    }
}
