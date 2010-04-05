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
	public String getTrustDirectionString();

	/**
	 * 
	 * @return
	 */
	public String getTrustTypeString();
}
