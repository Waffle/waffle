/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.servlet.spi;

import com.sun.jna.platform.win32.Win32Exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A collection of security filter providers.
 */
public class SecurityFilterProviderCollection {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilterProviderCollection.class);

    /** The providers. */
    private final List<SecurityFilterProvider> providers = new ArrayList<>();

    /**
     * Instantiates a new security filter provider collection.
     *
     * @param providerArray
     *            the provider array
     */
    public SecurityFilterProviderCollection(final SecurityFilterProvider[] providerArray) {
        for (final SecurityFilterProvider provider : providerArray) {
            SecurityFilterProviderCollection.LOGGER.info("using '{}'", provider.getClass().getName());
            this.providers.add(provider);
        }
    }

    /**
     * Instantiates a new security filter provider collection.
     *
     * @param providerNames
     *            the provider names
     * @param auth
     *            the auth
     */
    @SuppressWarnings("unchecked")
    public SecurityFilterProviderCollection(final String[] providerNames, final IWindowsAuthProvider auth) {
        Class<SecurityFilterProvider> providerClass;
        Constructor<SecurityFilterProvider> providerConstructor;
        for (String providerName : providerNames) {
            providerName = providerName.trim();
            SecurityFilterProviderCollection.LOGGER.info("loading '{}'", providerName);
            try {
                providerClass = (Class<SecurityFilterProvider>) Class.forName(providerName);
                providerConstructor = providerClass.getConstructor(IWindowsAuthProvider.class);
                final SecurityFilterProvider provider = providerConstructor.newInstance(auth);
                this.providers.add(provider);
            } catch (final ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (final SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException
                    | IllegalAccessException | InvocationTargetException e) {
                SecurityFilterProviderCollection.LOGGER.error("error loading '{}': {}", providerName, e.getMessage());
                SecurityFilterProviderCollection.LOGGER.trace("", e);
            }
        }
    }

    /**
     * Instantiates a new security filter provider collection.
     *
     * @param auth
     *            the auth
     */
    public SecurityFilterProviderCollection(final IWindowsAuthProvider auth) {
        this.providers.add(new NegotiateSecurityFilterProvider(auth));
        this.providers.add(new BasicSecurityFilterProvider(auth));
    }

    /**
     * Tests whether a specific security package is supported by any of the underlying providers.
     *
     * @param securityPackage
     *            Security package.
     *
     * @return True if the security package is supported, false otherwise.
     */
    public boolean isSecurityPackageSupported(final String securityPackage) {
        return this.get(securityPackage) != null;
    }

    /**
     * Gets the.
     *
     * @param securityPackage
     *            the security package
     *
     * @return the security filter provider
     */
    private SecurityFilterProvider get(final String securityPackage) {
        for (final SecurityFilterProvider provider : this.providers) {
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
     *
     * @return Windows Identity or NULL.
     *
     * @throws IOException
     *             on doFilter.
     */
    public IWindowsIdentity doFilter(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
        final SecurityFilterProvider provider = this.get(authorizationHeader.getSecurityPackage());
        if (provider == null) {
            throw new RuntimeException("Unsupported security package: " + authorizationHeader.getSecurityPackage());
        }
        try {
            return provider.doFilter(request, response);
        } catch (final Win32Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Returns true if authentication still needs to happen despite an existing principal.
     *
     * @param request
     *            Http Request
     *
     * @return True if authentication is required.
     */
    public boolean isPrincipalException(final HttpServletRequest request) {
        for (final SecurityFilterProvider provider : this.providers) {
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
    public void sendUnauthorized(final HttpServletResponse response) {
        for (final SecurityFilterProvider provider : this.providers) {
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
     *
     * @return A security provider instance.
     *
     * @throws ClassNotFoundException
     *             when class not found.
     */
    public SecurityFilterProvider getByClassName(final String name) throws ClassNotFoundException {
        for (final SecurityFilterProvider provider : this.providers) {
            if (provider.getClass().getName().equals(name)) {
                return provider;
            }
        }
        throw new ClassNotFoundException(name);
    }
}
