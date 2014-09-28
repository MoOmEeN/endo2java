package com.moomeen.endo2java.error;

public class InvocationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public InvocationException(Exception e) {
		super(e);
	}

	public InvocationException(int status) {
		super("HTTP RESPONSE STATUS: " + status);
	}

	public InvocationException(String msg) {
		super(msg);
	}
}
