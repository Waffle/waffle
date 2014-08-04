/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.servlet.spi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A collection of security filter providers.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SecurityFilterProviderCollection {

    private static final Logger          LOGGER    = LoggerFactory.getLogger(SecurityFilterProviderCollection.class);
    private List<SecurityFilterProvider> providers = new ArrayList<SecurityFilterProvider>();

    public SecurityFilterProviderCollection(SecurityFilterProvider[] providers) {
        for (SecurityFilterProvider provider : providers) {
            LOGGER.info("using '{}'", provider.getClass().getName());
            this.providers.add(provider);
        }
    }

    @SuppressWarnings("unchecked")
    public SecurityFilterProviderCollection(String[] providerNames, IWindowsAuthProvider auth) {
        for (String providerName : providerNames) {
            providerName = providerName.trim();
            LOGGER.info("loading '{}'", providerName);
            try {
                Class<SecurityFilterProvider> providerClass = (Class<SecurityFilterProvider>) Class
                        .forName(providerName);
                Constructor<SecurityFilterProvider> c = providerClass.getConstructor(IWindowsAuthProvider.class);
                SecurityFilterProvider provider = c.newInstance(auth);
                this.providers.add(provider);
            } catch (ClassNotFoundException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
            } catch (NoSuchMethodException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
            } catch (IllegalArgumentException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
            } catch (InstantiationException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                LOGGER.trace("{}", e);
            }
        }
    }

    public SecurityFilterProviderCollection(IWindowsAuthProvider auth) {
        this.providers.add(new NegotiateSecurityFilterProvider(auth));
        this.providers.add(new BasicSecurityFilterProvider(auth));
    }

    /**
     * Tests whether a specific security package is supported by any of the underlying providers.
     * 
     * @param securityPackage
     *            Security package.
     * @return True if the security package is supported, false otherwise.
     */
    public boolean isSecurityPackageSupported(String securityPackage) {
        return get(securityPackage) != null;
    }

    private SecurityFilterProvider get(String securityPackage) {
        for (SecurityFilterProvider provider : this.providers) {
            if (provider.isSecurityPackageSupported(securityPackage)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Filter.
     * 
     * @param request
     *            Http Request
     * @param response
     *            Http Response
     * @return Windows Identity or NULL.
     * @throws IOException
     */
    public IWindowsIdentity doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        SecurityFilterProvider provider = get(authorizationHeader.getSecurityPackage());
        if (provider == null) {
            throw new RuntimeException("Unsupported security package: " + authorizationHeader.getSecurityPackage());
        }
        return provider.doFilter(request, response);
    }

    /**
     * Returns true if authentication still needs to happen despite an existing principal.
     * 
     * @param request
     *            Http Request
     * @return True if authentication is required.
     */
    public boolean isPrincipalException(HttpServletRequest request) {

        for (SecurityFilterProvider provider : this.providers) {
            if (provider.isPrincipalException(request)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Send authorization headers.
     * 
     * @param response
     *            Http Response
     */
    public void sendUnauthorized(HttpServletResponse response) {
        for (SecurityFilterProvider provider : this.providers) {
            provider.sendUnauthorized(response);
        }
    }

    /**
     * Number of providers.
     * 
     * @return Number of providers.
     */
    public int size() {
        return this.providers.size();
    }

    /**
     * Get a security provider by class name.
     * 
     * @param name
     *            Class name.
     * @return A security provider instance.
     * @throws ClassNotFoundException
     */
    public SecurityFilterProvider getByClassName(String name) throws ClassNotFoundException {
        for (SecurityFilterProvider provider : this.providers) {
            if (provider.getClass().getName().equals(name)) {
                return provider;
            }
        }
        throw new ClassNotFoundException(name);
    }
}
