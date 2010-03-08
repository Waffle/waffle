package waffle.windows.auth;

/**
 * 
 *
 */
public interface IWindowsDomain {

	/**
	 * 
	 * @return
	 */
	public String getFqn();

	/**
	 * 
	 * @return
	 */
	public String getCanonicalName();

	/**
	 * 
	 * @return
	 */
	public String getTrustDirectionString();

	/**
	 * 
	 * @return
	 */
	public String getTrustTypeString();

	/**
	 * 
	 * @return
	 */
	public String[] getGroups();
}
