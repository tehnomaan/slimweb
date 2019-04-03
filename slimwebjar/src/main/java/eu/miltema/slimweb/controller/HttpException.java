package eu.miltema.slimweb.controller;

import java.text.MessageFormat;

/**
 * Indicates an HTTP specific error
 * @author Margus
 */
public class HttpException extends RuntimeException {

	private int httpCode;

	/**
	 * @param httpCode http code
	 * @param message error message
	 * @param arguments message arguments
	 */
	public HttpException(int httpCode, String message, String ... arguments) {
		super(formatMessage(message, arguments));
		this.httpCode = httpCode;
	}

	private static String formatMessage(String message, String[] arguments) {
		return new MessageFormat(message).format(arguments);
	}

	/**
	 * @return http code
	 */
	public int getHttpCode() {
		return httpCode;
	}
}
