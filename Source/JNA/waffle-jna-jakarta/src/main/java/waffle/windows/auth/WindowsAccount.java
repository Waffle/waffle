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
package waffle.windows.auth;

import java.io.Serializable;

/**
 * A flattened Windows Account used in a Windows principal.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccount implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The sid string. */
    private final String sidString;

    /** The fqn. */
    private final String fqn;

    /** The name. */
    private final String name;

    /** The domain. */
    private final String domain;

    /**
     * Instantiates a new windows account.
     *
     * @param account
     *            the account
     */
    public WindowsAccount(final IWindowsAccount account) {
        this.sidString = account.getSidString();
        this.fqn = account.getFqn();
        this.name = account.getName();
        this.domain = account.getDomain();
    }

    /**
     * Gets the sid string.
     *
     * @return the sid string
     */
    public String getSidString() {
        return this.sidString;
    }

    /**
     * Gets the fqn.
     *
     * @return the fqn
     */
    public String getFqn() {
        return this.fqn;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return this.domain;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof WindowsAccount)) {
            return false;
        }

        return ((WindowsAccount) o).getSidString().equals(this.getSidString());
    }

    @Override
    public int hashCode() {
        return this.getSidString().hashCode();
    }
}
