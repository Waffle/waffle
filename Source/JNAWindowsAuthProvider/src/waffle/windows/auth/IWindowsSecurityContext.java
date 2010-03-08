package waffle.windows.auth;

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
}
