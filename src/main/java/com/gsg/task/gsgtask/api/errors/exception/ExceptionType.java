package com.gsg.task.gsgtask.api.errors.exception;

public enum ExceptionType {
    USER_NOT_FOUND("user.not.found", 404, "User not found"),
    USERNAME_IS_USED("username.is.used", 400, "Username is already used"),
    INVALID_REQUEST_PARAMETERS("invalid.request.parameters", 400, "request parameters are invalid"),
    INVALID_USERNAME_OR_PASSWORD("invalid.username.or.password", 400, "Invalid username or password"),
    INVALID_USER("invalid.user", 400, "Invalid user"),
    INVALID_COUNTRY("invalid.country", 400, "Country name is Invalid"),
    CSV_DB_ERROR("csv.db.error", 500, "Error with communicating with db"),
    INVALID_TOKEN("invalid.token", 400, "Invalid JWT token"),
    YOUTUBE_SERVICE_PROBLEM("yt.service.problem", -1, "Problem with youtube service connection"),
    SERVER_SIDE_EXCEPTION("server.side.exception", 500, "Unexpected exception occurred on the server");

    private final String keyWord;
    private final int status;
    private final String details;

    ExceptionType(String keyWord, int status, String details) {
        this.keyWord = keyWord;
        this.status = status;
        this.details = details;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public int getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}
