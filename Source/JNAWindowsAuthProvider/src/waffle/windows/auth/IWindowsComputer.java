package waffle.windows.auth;

public interface IWindowsComputer {

	/**
	 * 
	 * @return
	 */
	public String getComputerName();

	/**
	 * 
	 * @return
	 */
	public String getMemberOf();

	/**
	 * 
	 * @return
	 */
	public String getJoinStatus();

	/**
	 * 
	 * @return
	 */
	public String[] getGroups();
}
