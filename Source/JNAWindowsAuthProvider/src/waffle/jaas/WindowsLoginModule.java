/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import waffle.windows.auth.PrincipalFormat;
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
	private IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    private Set<Principal> _principals = null;
    private PrincipalFormat _principalFormat = PrincipalFormat.fqn;
    private PrincipalFormat _roleFormat = PrincipalFormat.fqn;
    private boolean _allowGuestLogin = true;

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		
		_subject = subject;
		_callbackHandler = callbackHandler;
		
		for(Entry<String, ?> option : options.entrySet()) {
			if (option.getKey().equalsIgnoreCase("debug")) {
				_debug = Boolean.parseBoolean((String) option.getValue());
			} else if (option.getKey().equalsIgnoreCase("principalFormat")) {
				_principalFormat = PrincipalFormat.valueOf((String) option.getValue());
			} else if (option.getKey().equalsIgnoreCase("roleFormat")) {
				_roleFormat = PrincipalFormat.valueOf((String) option.getValue());
			}
		}
	}

	/**
	 * Use Windows SSPI to authenticate a username with a password.
	 */
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
        
        try {
			// disable guest login
			if (! _allowGuestLogin && windowsIdentity.isGuest()) {
				debug("guest login disabled: " + windowsIdentity.getFqn());
				throw new LoginException("Guest login disabled");			
			}
			
	        _principals = new LinkedHashSet<Principal>();
	        _principals.addAll(getUserPrincipals(windowsIdentity, _principalFormat));
	        if (_roleFormat != PrincipalFormat.none) {
		        for(IWindowsAccount group : windowsIdentity.getGroups()) {
		        	_principals.addAll(getRolePrincipals(group, _roleFormat));
		        }
	        }
	        
	        _username = windowsIdentity.getFqn();
	        debug("successfully logged in " + _username + " (" + windowsIdentity.getSidString() + ")");
        } finally {
        	windowsIdentity.dispose();
        }
        
        return true;
	}
	    
	/**
	 * Abort a login process.
	 */
	public boolean abort() throws LoginException {
		return logout();
	}

	/**
	 * Commit principals to the subject.
	 */
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

	public boolean logout() throws LoginException {
        if (_subject.isReadOnly()) {
            throw new LoginException("Subject cannot be read-only.");
        }

        _subject.getPrincipals().clear();
		_principals = null;
		
		if (_username != null) {
			debug("logging out " + _username);
			_username = null;
		}
		
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
	public IWindowsAuthProvider getAuth() {
		return _auth;
	}
	
	/**
	 * Set Windows auth provider.
	 * @param provider
	 *  Class implements IWindowsAuthProvider.
	 */
	public void setAuth(IWindowsAuthProvider provider) {
		_auth = provider;
	}
	
	/**
	 * Returns a list of user principal objects.
	 * @param windowsIdentity
	 *  Windows identity.
	 * @param principalFormat
	 *  Principal format.
	 * @return
	 *  A list of user principal objects.
	 */
	private static List<Principal> getUserPrincipals(
			IWindowsIdentity windowsIdentity, PrincipalFormat principalFormat) {
		
		List<Principal> principals = new ArrayList<Principal>();
        switch(principalFormat) {
        case fqn:
            principals.add(new UserPrincipal(windowsIdentity.getFqn()));
        	break;
        case sid:
            principals.add(new UserPrincipal(windowsIdentity.getSidString()));
        	break;
        case both:
            principals.add(new UserPrincipal(windowsIdentity.getFqn()));
            principals.add(new UserPrincipal(windowsIdentity.getSidString()));
        	break;
        case none:
        	break;
        }
        
        return principals;
	}
	
	/**
	 * Returns a list of role principal objects.
	 * @param group
	 *  Windows group.
	 * @param principalFormat
	 *  Principal format.
	 * @return
	 *  List of role principal objects.
	 */
	private static List<Principal> getRolePrincipals(
			IWindowsAccount group, PrincipalFormat principalFormat) {
		
		List<Principal> principals = new ArrayList<Principal>();
        switch(principalFormat) {
        case fqn:
            principals.add(new RolePrincipal(group.getFqn()));
        	break;
        case sid:
            principals.add(new RolePrincipal(group.getSidString()));
        	break;
        case both:
            principals.add(new RolePrincipal(group.getFqn()));
            principals.add(new RolePrincipal(group.getSidString()));
        	break;
        case none:
        	break;
        }
        
        return principals;
	}
	
	/**
	 * True if Guest login permitted.
	 * @return
	 *  True if Guest login permitted, false otherwise.
	 */
	public boolean getAllowGuestLogin() {
		return _allowGuestLogin;
	}
	
	/**
	 * Set whether Guest login is permitted.
	 * Default is true, if the Guest account is enabled, an invalid username/password
	 * results in a Guest login.
	 * @param value
	 *  True or false.
	 */
	public void setAllowGuestLogin(boolean value) {
		_allowGuestLogin = value;
	}
}
