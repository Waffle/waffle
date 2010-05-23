package waffle.tomcat;

import java.security.Principal;

import org.apache.catalina.realm.RealmBase;

public class WindowsRealm extends RealmBase {

    protected static final String _name = "waffle.tomcat.WindowsRealm/1.0";

	@Override
	protected String getName() {
		return _name;
	}

	@Override
	protected String getPassword(String arg0) {
		return null;
	}

	@Override
	protected Principal getPrincipal(String arg0) {
		return null;
	}
}
