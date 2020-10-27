package com.gsg.task.gsgtask.api.errors.exception;

public enum ExceptionType {
    USER_NOT_FOUND("user.not.found", 404),
    USERNAME_IS_USED("username.is.used", 400),
    INVALID_REQUEST_PARAMETERS("invalid.request.parameters", 400),
    INVALID_USERNAME_OR_PASSWORD("invalid.username.or.password", 400),
    INVALID_USER("invalid.user", 400),
    CSV_DB_ERROR("csv.db.error", 500),
    INVALID_TOKEN("invalid.token", 400),
    SERVER_SIDE_EXCEPTION("server.side.exception", 500);

    private final String keyWord;
    private final int status;

    ExceptionType(String keyWord, int status) {
        this.keyWord = keyWord;
        this.status = status;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public int getStatus() {
        return status;
    }
}
