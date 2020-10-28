package com.gsg.task.gsgtask.api.errors;

import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.api.errors.exception.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    public ExceptionController() {
    }


    @ResponseBody
    @ExceptionHandler(AppException.class)
    public ResponseEntity<Problem> getErrorResponse(AppException e) {
        return getEntity(e);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleUnexpectedException(Exception e) {
        log.error("unhandled exception", e);
        return getEntity(ExceptionType.SERVER_SIDE_EXCEPTION);

    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Problem> getErrorResponse(BadCredentialsException e) {
        return getEntity(ExceptionType.INVALID_USERNAME_OR_PASSWORD);
    }

    private ResponseEntity<Problem> getEntity(ExceptionType et) {
        Problem problem = Problem.builder()
                .keyword(et.getKeyWord())
                .status(et.getStatus())
                .details(et.getDetails())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.valueOf(et.getStatus()));
    }

    private ResponseEntity<Problem> getEntity(AppException et) {
        Problem problem = Problem.builder()
                .keyword(et.getType().getKeyWord())
                .details(et.getType().getDetails())
                .status(et.getType().getStatus())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.valueOf(et.getStatus()));
    }

}
