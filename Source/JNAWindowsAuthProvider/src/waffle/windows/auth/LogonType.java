/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth;

/**
 * Windows Logon Type.
 * @author dblock[at]dblock[dot]org
 */
public abstract class LogonType {
	public static final int LogonType_LOGON32_LOGON_INTERACTIVE = 2;
	public static final int LogonType_LOGON32_LOGON_NETWORK = 3;
	public static final int LogonType_LOGON32_LOGON_BATCH = 4;
	public static final int LogonType_LOGON32_LOGON_SERVICE = 5;
	public static final int LogonType_LOGON32_LOGON_UNLOCK = 7;
	public static final int LogonType_LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
	public static final int LogonType_LOGON32_LOGON_NEW_CREDENTIALS = 9;
}
