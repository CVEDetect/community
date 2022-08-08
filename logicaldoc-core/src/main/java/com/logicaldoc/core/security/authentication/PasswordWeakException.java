package com.logicaldoc.core.security.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Raised when you try to change a password that is too weak
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 8.8.2
 */
public class PasswordWeakException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	/**
	 * The reasons to explain the weakness
	 */
	private List<String> messages = new ArrayList<String>();

	public PasswordWeakException(List<String> messages) {
		super("passwordweak");
		this.messages = messages;
	}

	public PasswordWeakException() {
		super("passwordweak");
	}

	@Override
	public String getMessage() {
		if (messages == null || messages.isEmpty())
			return super.getMessage();
		else {
			return super.getMessage() + " - " + messages.stream().collect(Collectors.joining(" "));
		}
	}

	@Override
	public boolean mustRecordFailure() {
		return false;
	}

	public List<String> getMessages() {
		return messages;
	}
}
