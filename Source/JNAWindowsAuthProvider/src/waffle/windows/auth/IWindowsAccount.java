package waffle.windows.auth;

/**
 * Windows account.
 */
public interface IWindowsAccount {

	/**
	 * Security identifier.
	 * @return
	 */
	public String getSidString();


	/**
	 * Fully qualified username.
	 * @return
	 */
	public String getFqn();
}
