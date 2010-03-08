package waffle.windows.auth;

/**
 * 
 * @author dblock
 *
 */
public interface IWindowsImpersonationContext {

	/**
	 * 
	 */
	public void revertToSelf();
}
