package waffle.servlet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

public class AutoDisposableWindowsPrincipal extends WindowsPrincipal implements HttpSessionBindingListener {

	private static final long serialVersionUID = 1L;
	
	public AutoDisposableWindowsPrincipal(IWindowsIdentity windowsIdentity) {
		super(windowsIdentity);
	}

	public AutoDisposableWindowsPrincipal(IWindowsIdentity windowsIdentity, 
			PrincipalFormat principalFormat, PrincipalFormat roleFormat) {
		super(windowsIdentity, principalFormat, roleFormat);
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent evt) { }

	@Override
	public void valueUnbound(HttpSessionBindingEvent evt) {
		if (getIdentity() != null) {
			getIdentity().dispose();
		}
	}

}
