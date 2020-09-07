/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
package waffle.spring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.windows.auth.WindowsAccount;

/**
 * A {@link GrantedAuthorityFactory} that uses the {@link WindowsAccount}'s fqn as the basis of the
 * {@link GrantedAuthority} string, and (optionally) applies two transformations:
 * <ul>
 * <li>prepending a prefix, and</li>
 * <li>converting to uppercase</li>
 * </ul>
 * .
 */
public class FqnGrantedAuthorityFactory implements GrantedAuthorityFactory {

    /** The prefix. */
    private final String prefix;

    /** The convert to upper case. */
    private final boolean convertToUpperCase;

    /**
     * Instantiates a new fqn granted authority factory.
     *
     * @param newPrefix
     *            the new prefix
     * @param newConvertToUpperCase
     *            the new convert to upper case
     */
    public FqnGrantedAuthorityFactory(final String newPrefix, final boolean newConvertToUpperCase) {
        this.prefix = newPrefix;
        this.convertToUpperCase = newConvertToUpperCase;
    }

    @Override
    public GrantedAuthority createGrantedAuthority(final WindowsAccount windowsAccount) {

        String grantedAuthorityString = windowsAccount.getFqn();

        if (this.prefix != null) {
            grantedAuthorityString = this.prefix + grantedAuthorityString;
        }

        if (this.convertToUpperCase) {
            grantedAuthorityString = grantedAuthorityString.toUpperCase();
        }

        return new SimpleGrantedAuthority(grantedAuthorityString);
    }

}
