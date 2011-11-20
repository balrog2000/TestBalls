package com.tkraus.greenball.model;

public class JenkinsException extends Exception {
	private static final long serialVersionUID = -1100941854441653341L;
	private JenkinsFailEnum reason;

	public JenkinsException(JenkinsFailEnum reason) {
		this.reason = reason;
	}

	public JenkinsFailEnum getReason() {
		return reason;
	}

	@Override
	public String getMessage() {
		return reason.toString();
	}
}
