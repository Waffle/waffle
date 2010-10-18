/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.kerberos;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import jcifs.Config;
import jcifs.UniAddress;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.smb.NtlmChallenge;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;

import waffle.util.NtlmMessage;
import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

import com.google.common.collect.MapMaker;

/**
 * @author michal[ddot]bergmann[at]seznam[dott]cz
 */
public class JavaAuthProvider implements IWindowsAuthProvider {

	private static Log _log = LogFactory.getLog(JavaAuthProvider.class);

	String _defaultDomain;
	String _domainController;
	boolean _loadBalance;
	boolean _jcifsEnabled;
	boolean _kerberosEnabled;

	/** Login Context module name for client auth. */
	String _clientModuleName;

	LoginContext _serverLoginContext;
	
	static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public JavaAuthProvider(Map<String, String> implParameters) {

		try {
			if (initJcifs(implParameters)) {
				_jcifsContexts = new MapMaker().expiration(30, TimeUnit.SECONDS).makeMap();
				_jcifsEnabled = true;
			} else {
				_log.info("NTLM/JCifs not configured");
			}
		} catch (Exception e) {
			_log.error("JCifs init failed", e);
		}

		try {
			if (initKerberos(implParameters)) {
				_kerberosContexts = new MapMaker().expiration(30, TimeUnit.SECONDS).makeMap();
				_kerberosEnabled = true;
			} else {
				_log.info("Kerberos not configured");
			}
		} catch (Exception e) {
			_log.error("Kerberos init failed", e);
		}
		
		if (!_jcifsEnabled && !_kerberosEnabled) {
			throw new JavaAuthException("Neither NTLM/JCifs nor Kerberos is correctly configured.");
		}

	}

	private static final String MISSING_PROPERTY = "Servlet Filter init param(s) in web.xml missing: ";

	private boolean initKerberos(Map<String, String> implParameters)
			throws LoginException, PrivilegedActionException, GSSException {
		_clientModuleName = implParameters.remove(Constants.CLIENT_MODULE);

		String krb5Conf = implParameters.remove(Constants.KRB5_CONF);
		String loginConf = implParameters.remove(Constants.LOGIN_CONF);

		String serverModuleName = implParameters
				.remove(Constants.SERVER_MODULE);
		String preauthPassword = implParameters
				.remove(Constants.PREAUTH_PASSWORD);
		String preauthUsername = implParameters
				.remove(Constants.PREAUTH_USERNAME);
		
		if (isEmpty(_clientModuleName) && isEmpty(krb5Conf) && isEmpty(loginConf) && isEmpty(serverModuleName) && isEmpty(preauthPassword) && isEmpty(preauthUsername)) {
			return false;
		}
		
		// specify krb5 conf as a System property
		if (null == krb5Conf) {
			throw new IllegalArgumentException(MISSING_PROPERTY
					+ Constants.KRB5_CONF);
		}

		// specify login conf as a System property
		if (null == loginConf) {
			throw new IllegalArgumentException(MISSING_PROPERTY
					+ Constants.LOGIN_CONF);
		}
		System.setProperty("java.security.krb5.conf", krb5Conf);
		System.setProperty("java.security.auth.login.config", loginConf);

		CallbackHandler handler = getUsernamePasswordHandler(preauthUsername,
				preauthPassword);
		_serverLoginContext = new LoginContext(serverModuleName, handler);
		_serverLoginContext.login();
		return true;
	}

	private boolean initJcifs(Map<String, String> implParameters) {
		boolean found = false;
		for(String key:implParameters.keySet()) {
			if (key.startsWith("jcifs.")) {
				found = true;
			}
		}
		if (!found) {
			return false;
		}
		_defaultDomain = Config.getProperty("jcifs.smb.client.domain");
		_domainController = Config.getProperty("jcifs.http.domainController",
				_defaultDomain);

		/*
		 * Set jcifs properties we know we want; soTimeout and cachePolicy to
		 * 30min.
		 */
		Config.setProperty("jcifs.smb.client.soTimeout", "1800000");
		Config.setProperty("jcifs.netbios.cachePolicy", "1200");
		/*
		 * The Filter can only work with NTLMv1 as it uses a man-in-the-middle
		 * techinque that NTLMv2 specifically thwarts. A real NTLM Filter would
		 * need to do a NETLOGON RPC that JCIFS will likely never implement
		 * because it requires a lot of extra crypto not used by CIFS.
		 */
		Config.setProperty("jcifs.smb.lmCompatibility", "0");
		Config.setProperty("jcifs.smb.client.useExtendedSecurity", "false");

		for (String key : implParameters.keySet().toArray(
				new String[implParameters.size()])) {
			if (key.startsWith("jcifs.")) {
				Config.setProperty(key, implParameters.remove(key));
			}
		}

		_defaultDomain = Config.getProperty("jcifs.smb.client.domain");
		_domainController = Config.getProperty("jcifs.http.domainController");
		if (_domainController == null) {
			_domainController = _defaultDomain;
			_loadBalance = Config.getBoolean("jcifs.http.loadBalance", true);
		}
		return true;
	}

	ConcurrentMap<String, NtlmChallenge> _jcifsContexts = null;
	ConcurrentMap<String, GSSContext> _kerberosContexts = null;

	@Override
	public IWindowsIdentity logonUser(String username, String password) {
		try {
			if (_kerberosEnabled) {
				return logonUserKerberos(username, password);
			} else if (_jcifsEnabled){
				return logonUserJcifs(username, password);
			} else {
				throw new JavaAuthException("Neither kerberos nor NTLM/jcifs is configured, cannot authenticate user");
			}
		} catch (Exception e) {
			throw new JavaAuthException(e);
		}

	}

	public static CallbackHandler getUsernamePasswordHandler(
			final String username, final String password) {

		final CallbackHandler handler = new CallbackHandler() {
			public void handle(final Callback[] callback)
					throws UnsupportedCallbackException {
				for (int i = 0; i < callback.length; i++) {
					if (callback[i] instanceof NameCallback) {
						final NameCallback nameCallback = (NameCallback) callback[i];
						nameCallback.setName(username);
					} else if (callback[i] instanceof PasswordCallback) {
						final PasswordCallback passCallback = (PasswordCallback) callback[i];
						passCallback.setPassword(password.toCharArray());
					} else {
						throw new UnsupportedCallbackException(callback[i]);
					}
				}
			}
		};

		return handler;
	}

	private IWindowsIdentity logonUserKerberos(String username, String password)
			throws LoginException, UnsupportedEncodingException,
			NoSuchAlgorithmException {
		final CallbackHandler handler = getUsernamePasswordHandler(username,
				password);

		// assert
		if (null == username || username.isEmpty()) {
			throw new LoginException("Username is required.");
		}

		final LoginContext cntxt = new LoginContext(this._clientModuleName,
				handler);

		// validate username/password by login/logout
		cntxt.login();
		cntxt.logout();
		return new JavaIdentity(cntxt.getSubject().getPrincipals().iterator().next().getName());
	}

	private IWindowsIdentity logonUserJcifs(String username, String password)
			throws UnknownHostException, SmbException,
			UnsupportedEncodingException, NoSuchAlgorithmException {
		int index = username.indexOf('\\');
		if (index == -1)
			index = username.indexOf('/');
		String domain = (index != -1) ? username.substring(0, index)
				: _defaultDomain;
		username = (index != -1) ? username.substring(index + 1) : username;
		NtlmPasswordAuthentication ntlm = new NtlmPasswordAuthentication(
				domain, username, password);
		UniAddress dc = UniAddress.getByName(_domainController, true);
		SmbSession.logon(dc, ntlm);
		return new JavaIdentity(domain + "\\" + username);
	}

	@Override
	public IWindowsIdentity logonDomainUser(String username, String domain,
			String password) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWindowsIdentity logonDomainUserEx(String username, String domain,
			String password, int logonType, int logonProvider) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWindowsAccount lookupAccount(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWindowsComputer getCurrentComputer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWindowsDomain[] getDomains() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IWindowsSecurityContext acceptSecurityToken(String connectionId,
			byte[] token, String securityPackage) {
		boolean ntlmMessage = NtlmMessage.isNtlmMessage(token);

		try {
			if (ntlmMessage) {
				if (!_jcifsEnabled) {
					throw new JavaAuthException("Received NTLM message and NTLM/JCifs is not configured.");
				}
				return authJcifs(connectionId, token, securityPackage);
			} else {
				if (!_kerberosEnabled) {
					throw new JavaAuthException("Received kerberos message and kerberos is not configured.");
				}
				return authKerberos(connectionId, token, securityPackage);
			}
		} catch (Exception e) {
			throw new JavaAuthException(e);
		}
	}

	private IWindowsSecurityContext authKerberos(String connectionId,
			byte[] token, String securityPackage) throws GSSException,
			UnsupportedEncodingException, NoSuchAlgorithmException,
			PrivilegedActionException {
		GSSContext context = _kerberosContexts.remove(connectionId);
		if (context == null) {
			final GSSManager manager = GSSManager.getInstance();
			
			final PrivilegedExceptionAction<GSSCredential> action = new PrivilegedExceptionAction<GSSCredential>() {
				public GSSCredential run() throws GSSException {
					// Oid KERBEROS_OID = new Oid("1.2.840.113554.1.2.2");
					Oid SPNEGO_OID = new Oid("1.3.6.1.5.5.2");
					return manager.createCredential(null,
							GSSCredential.INDEFINITE_LIFETIME,
							new Oid[] { SPNEGO_OID }, GSSCredential.ACCEPT_ONLY);
				}
			};
			
			GSSCredential serverCredentials = (GSSCredential) Subject.doAs(
					_serverLoginContext.getSubject(), action);
			context = manager.createContext(serverCredentials);
		}

		token = context.acceptSecContext(token, 0, token.length);

		if (!context.isEstablished()) {
			_kerberosContexts.put(connectionId, context);
			_log.warn("Kerberos context not established");
			return new JavaSecurityContext(token, securityPackage);
		}

		return new JavaSecurityContext(token, securityPackage,
				new JavaIdentity(context.getSrcName().toString()));
	}

	private IWindowsSecurityContext authJcifs(String connectionId,
			byte[] token, String securityPackage) throws IOException,
			NoSuchAlgorithmException {
		NtlmChallenge chal = _jcifsContexts.remove(connectionId);

		try {
			UniAddress dc;
			byte[] challenge;
			if (_loadBalance) {
				if (chal == null) {
					chal = SmbSession.getChallengeForDomain();
				}
				dc = chal.dc;
				challenge = chal.challenge;
			} else {
				dc = UniAddress.getByName(_domainController, true);
				challenge = SmbSession.getChallenge(dc);
			}

			byte[] token2 = null;
			if (token[8] == 1) {
				Type1Message type1 = new Type1Message(token);
				Type2Message type2 = new Type2Message(type1, challenge, null);
				token2 = type2.toByteArray();
			} else {
				chal = null;
				if (token[8] == 3) {
					Type3Message type3 = new Type3Message(token);
					byte[] lmResponse = type3.getLMResponse();
					if (lmResponse == null)
						lmResponse = new byte[0];
					byte[] ntResponse = type3.getNTResponse();
					if (ntResponse == null)
						ntResponse = new byte[0];
					NtlmPasswordAuthentication ntlm = new NtlmPasswordAuthentication(
							type3.getDomain(), type3.getUser(), challenge,
							lmResponse, ntResponse);
					try {
						SmbSession.logon(dc, ntlm);
						_log.info("NtlmHttpFilter: " + ntlm
								+ " successfully authenticated against " + dc);
						return new JavaSecurityContext(null, securityPackage,
								new JavaIdentity(ntlm.getName()));
					} catch (SmbAuthException sae) {
						_log.debug("NtlmHttpFilter: "
								+ ntlm.getName()
								+ ": 0x"
								+ jcifs.util.Hexdump.toHexString(
										sae.getNtStatus(), 8) + ": " + sae);
					}
				}
			}
			return new JavaSecurityContext(token2, securityPackage);
		} finally {
			if (chal != null) {
				_jcifsContexts.put(connectionId, chal);
			}
		}
	}

	@Override
	public void resetSecurityToken(String connectionId) {
		if (_jcifsContexts!=null) {
			_jcifsContexts.remove(connectionId);
		}
		if (_kerberosContexts!=null) {
			_kerberosContexts.remove(connectionId);
		}
	}
}
