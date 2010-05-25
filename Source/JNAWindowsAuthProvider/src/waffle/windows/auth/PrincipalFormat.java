package waffle.windows.auth;

public enum PrincipalFormat {
	fqn,
	sid,
	both,
	none;
	
	/**
	 * Convert a string to a principal format.
	 * @param value
	 *  String value.
	 * @return
	 *  Principal format.
	 */
	public static PrincipalFormat parse(String value) {
		if (value.equalsIgnoreCase("fqn"))
			return fqn;
		else if (value.equalsIgnoreCase("sid"))
			return sid;
		else if (value.equalsIgnoreCase("both"))
			return both;
		else if (value.equalsIgnoreCase("none"))
			return none;
		else {
			throw new RuntimeException("Invalid principal format '" + value + "'");
		}				
	}
};


