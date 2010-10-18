/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.kerberos;

/**
 * @author michal[ddot]bergmann[at]seznam[dott]cz
 */
public class Constants {
    /** 
     * Servlet init param name in web.xml <b>spnego.login.client.module</b>. 
     * 
     * <p>The LoginModule name that exists in the login.conf file.</p>
     */
    public static final String CLIENT_MODULE = "spnego.login.client.module";

    /** 
     * Servlet init param name in web.xml <b>spnego.krb5.conf</b>. 
     * 
     * <p>The location of the krb5.conf file. On Windows, this file will 
     * sometimes be named krb5.ini and reside <code>%WINDOWS_ROOT%/krb5.ini</code> 
     * here.</p>
     * 
     * <p>By default, Java looks for the file in these locations and order:
     * <li>System Property (java.security.krb5.conf)</li>
     * <li>%JAVA_HOME%/lib/security/krb5.conf</li>
     * <li>%WINDOWS_ROOT%/krb5.ini</li>
     * </p>
     */
    public static final String KRB5_CONF = "spnego.krb5.conf";
    
    /** 
     * Servlet init param name in web.xml <b>spnego.login.conf</b>. 
     * 
     * <p>The location of the login.conf file.</p>
     */
    public static final String LOGIN_CONF = "spnego.login.conf";
    
    /** 
     * Servlet init param name in web.xml <b>spnego.preauth.password</b>. 
     * 
     * <p>Network Domain password. For Windows, this is sometimes known 
     * as the Windows NT password.</p>
     */
    public static final String PREAUTH_PASSWORD = "spnego.preauth.password";
    
    /** 
     * Servlet init param name in web.xml <b>spnego.preauth.username</b>. 
     * 
     * <p>Network Domain username. For Windows, this is sometimes known 
     * as the Windows NT username.</p>
     */
    public static final String PREAUTH_USERNAME = "spnego.preauth.username";
    
    /** 
     * Servlet init param name in web.xml <b>spnego.login.server.module</b>. 
     * 
     * <p>The LoginModule name that exists in the login.conf file.</p>
     */
    public static final String SERVER_MODULE = "spnego.login.server.module";
}