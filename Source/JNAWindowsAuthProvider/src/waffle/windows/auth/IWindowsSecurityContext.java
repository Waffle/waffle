package waffle.windows.auth;

import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.Sspi.CtxtHandle;
import com.sun.jna.platform.win32.Sspi.SecBufferDesc;

/**
 * 
 */
public interface IWindowsSecurityContext {

	/**
	 * 
	 * @return
	 */	
	public String getSecurityPackage();

	/**
	 * 
	 * @return
	 */
	public String getPrincipalName();
	
	/**
	 * 
	 * @return
	 */
	public byte[] getToken();

	/**
	 * 
	 * @return
	 */
	public boolean getContinue();

	/**
	 * 
	 * @return
	 */
	public IWindowsIdentity getIdentity();
	
	public Sspi.CtxtHandle getHandle();

	/**
	 * Initialize the security context.
	 */
	public void initialize();
	
	/**
	 * Initialize the security context, continuing from a previous one.
	 * @param continueCtx
	 */
	public void initialize(CtxtHandle continueCtx, SecBufferDesc continueToken);
	
	/**
	 * Disposes of the context.
	 */
	public void dispose();
}
