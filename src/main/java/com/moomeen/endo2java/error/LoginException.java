package com.moomeen.endo2java.error;


public class LoginException extends InvocationException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public LoginException(Exception e) {
		super(e);
	}

	public LoginException(String msg) {
		super(msg);
	}

}
