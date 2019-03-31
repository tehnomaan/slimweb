package eu.miltema.slimweb.controller;

import java.text.MessageFormat;

public class HttpException extends RuntimeException {

	private int httpCode;

	public HttpException(int httpCode, String message, String ... messageArguments) {
		super(formatMessage(message, messageArguments));
		this.httpCode = httpCode;
	}

	private static String formatMessage(String message, String[] messageArguments) {
		return new MessageFormat(message).format(messageArguments);
	}

	public int getHttpCode() {
		return httpCode;
	}
}
