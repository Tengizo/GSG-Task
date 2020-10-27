package com.gsg.task.gsgtask.api.errors.exception;

public class BadRequestException extends AppException {

	public BadRequestException(ExceptionType type) {
		super(type);
	}
}
