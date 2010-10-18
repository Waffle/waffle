/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.kerberos;

/**
 * @author michal[ddot]bergmann[at]seznam[dott]cz
 */
public class JavaAuthException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JavaAuthException() {
	}

	public JavaAuthException(String arg0) {
		super(arg0);
	}

	public JavaAuthException(Throwable arg0) {
		super(arg0);
	}

	public JavaAuthException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
