package com.helpnearby.exception;

public class InvalidOtpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidOtpException() {
		super("Invalid or expired verification code");
	}

	public InvalidOtpException(String message) {
		super(message);
	}
}