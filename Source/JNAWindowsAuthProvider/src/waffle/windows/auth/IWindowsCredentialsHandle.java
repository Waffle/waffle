package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi.CredHandle;

/**
 * Windows credentials handle.
 */
public interface IWindowsCredentialsHandle {
	/**
	 * Initialize.
	 */
	void initialize();
	/**
	 * Dispose.
	 */
	void dispose();
	
	/**
	 * Return a security handle.
	 */
	CredHandle getHandle();
}
