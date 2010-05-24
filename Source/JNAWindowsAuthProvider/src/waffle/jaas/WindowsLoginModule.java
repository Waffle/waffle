/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.security.Principal;
import java.util.HashSet;
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

    private Subject _subject = null;
    private CallbackHandler _callbackHandler = null;
	private static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    private Set<Principal> _principals = null;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		
		_subject = subject;
		_callbackHandler = callbackHandler;
        System.out.println("initialize: " + _subject.getPrincipals());
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
            throw new LoginException("Callback " + e.getCallback().toString() +
                    " not available to gather authentication information from the user.");
        }
        
        IWindowsIdentity windowsIdentity = _auth.logonUser(username, password);
        
        _principals = new HashSet<Principal>();
        _principals.add(new UserPrincipal(windowsIdentity.getFqn()));
        for(IWindowsAccount group : windowsIdentity.getGroups()) {
        	_principals.add(new RolePrincipal(group.getFqn()));
        }
        
        System.out.println("logged in: " + windowsIdentity.getFqn());
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
        System.out.println("commit: " + _subject.getPrincipals().size());
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
        if (_subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }

        _subject.getPrincipals().clear();
		_principals = null;
        System.out.println("logout: " + _subject.getPrincipals().size());
		return true;
	}
}
