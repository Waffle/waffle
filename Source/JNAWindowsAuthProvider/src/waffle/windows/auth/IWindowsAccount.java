package waffle.windows.auth;

/**
 * Windows account.
 */
public interface IWindowsAccount {

	/**
	 * Security identifier.
	 * @return
	 *  String in the S- format.
	 */
	public String getSidString();


	/**
	 * Fully qualified username.
	 * @return
	 *  String
	 */
	public String getFqn();
	
	/**
	 * User name.
	 * @return
	 *  String.
	 */
	public String getName();
	
	/**
	 * Domain name.
	 * @return
	 */
	public String getDomain();
}
