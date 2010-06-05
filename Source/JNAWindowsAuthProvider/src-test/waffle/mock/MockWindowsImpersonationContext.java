package waffle.mock;

import waffle.windows.auth.IWindowsImpersonationContext;

public class MockWindowsImpersonationContext implements IWindowsImpersonationContext {

	@Override
	public void RevertToSelf() {
	}
}
