/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Java Security login module for Windows authentication.
 * @author dblock[at]dblock[dot]org
 * @see javax.security.auth.spi.LoginModule
 */
public class WindowsLoginModule implements LoginModule {

	private String _username = null;
    private boolean _debug = false;
    private Subject _subject = null;
    private CallbackHandler _callbackHandler = null;
	private static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    private List<Principal> _principals = null;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		
		_subject = subject;
		_callbackHandler = callbackHandler;
        _debug = "true".equalsIgnoreCase((String) options.get("debug"));
	}

	/**
	 * Use Windows SSPI to authenticate a username with a password.
	 */
	@Override
	public boolean login() throws LoginException {
        if (_callbackHandler == null) {
            throw new LoginException("Missing callback to gather information from the user.");
        }
        
        NameCallback usernameCallback = new NameCallback("user name: ");
        PasswordCallback passwordCallback = new PasswordCallback("password: ", false);
        
        Callback[] callbacks = new Callback[2];
        callbacks[0] = usernameCallback;
        callbacks[1] = passwordCallback; 
        
        String username = null;
        String password = null;
        
        try {
            _callbackHandler.handle(callbacks);
            username = usernameCallback.getName();
            password = passwordCallback.getPassword() == null ? "" 
            		: new String(passwordCallback.getPassword());
            passwordCallback.clearPassword();
        } catch (java.io.IOException e) {
            throw new LoginException(e.toString());
        } catch (UnsupportedCallbackException e) {
            throw new LoginException("Callback " + e.getCallback().getClass().getName() +
                    " not available to gather authentication information from the user.");
        }

        IWindowsIdentity windowsIdentity = null;
        try {
        	windowsIdentity = _auth.logonUser(username, password);
        } catch (Exception e) {
        	throw new LoginException(e.getMessage());
        }
        
        _principals = new ArrayList<Principal>();
        _principals.add(new UserPrincipal(windowsIdentity.getFqn()));
        for(IWindowsAccount group : windowsIdentity.getGroups()) {
        	_principals.add(new RolePrincipal(group.getFqn()));
        }
        
        _username = windowsIdentity.getFqn();
        debug("successfully logged in " + _username);
        return true;
	}
	    
	@Override
	public boolean abort() throws LoginException {
		return logout();
	}

	@Override
	public boolean commit() throws LoginException {
		if (_principals == null) {
			return false;
		}

        if (_subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }
    
        Set<Principal> principals = _subject.getPrincipals();
        principals.addAll(_principals);
        
        debug("committing " + _subject.getPrincipals().size() + " principals");
        if (_debug) {
        	for (Principal principal : principals) {
        		debug(" principal: " + principal.getName());
        	}
        }
        
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
        if (_subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }

        _subject.getPrincipals().clear();
		_principals = null;
        debug("logging out " + _username);
		return true;
	}
	
	private void debug(String message) {
		if (_debug) {
			System.out.println("[waffle.jaas.WindowsLoginModule] " + message);
		}
	}
	
	/**
	 * True if Debug is enabled.
	 * @return
	 *  True or false.
	 */
	public boolean getDebug() {
		return _debug;
	}
	
	/**
	 * Windows auth provider.
	 * @return
	 *  IWindowsAuthProvider.
	 */
	public static IWindowsAuthProvider getAuth() {
		return _auth;
	}
	
	/**
	 * Set Windows auth provider.
	 * @param provider
	 *  Class implements IWindowsAuthProvider.
	 */
	public static void setAuth(IWindowsAuthProvider provider) {
		_auth = provider;
	}
}
