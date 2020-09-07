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
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;

import waffle.windows.auth.IWindowsDomain;

/**
 * Windows Domain.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsDomainImpl implements IWindowsDomain {

    /**
     * The Enum TrustDirection.
     */
    private enum TrustDirection {

        /** The inbound. */
        INBOUND,
        /** The outbound. */
        OUTBOUND,
        /** The bidirectional. */
        BIDIRECTIONAL
    }

    /**
     * The Enum TrustType.
     */
    private enum TrustType {

        /** The tree root. */
        TREE_ROOT,
        /** The parent child. */
        PARENT_CHILD,
        /** The cross link. */
        CROSS_LINK,
        /** The external. */
        EXTERNAL,
        /** The forest. */
        FOREST,
        /** The kerberos. */
        KERBEROS,
        /** The unknown. */
        UNKNOWN
    }

    /** The fqn. */
    private String fqn;

    /** The trust direction. */
    private TrustDirection trustDirection = TrustDirection.BIDIRECTIONAL;

    /** The trust type. */
    private TrustType trustType = TrustType.UNKNOWN;

    /**
     * Instantiates a new windows domain impl.
     *
     * @param newFqn
     *            the new fqn
     */
    public WindowsDomainImpl(final String newFqn) {
        this.fqn = newFqn;
    }

    /**
     * Instantiates a new windows domain impl.
     *
     * @param trust
     *            the trust
     */
    public WindowsDomainImpl(final DomainTrust trust) {
        // fqn
        this.fqn = trust.DnsDomainName;
        if (this.fqn == null || this.fqn.length() == 0) {
            this.fqn = trust.NetbiosDomainName;
        }
        // trust direction
        if (trust.isInbound() && trust.isOutbound()) {
            this.trustDirection = TrustDirection.BIDIRECTIONAL;
        } else if (trust.isOutbound()) {
            this.trustDirection = TrustDirection.OUTBOUND;
        } else if (trust.isInbound()) {
            this.trustDirection = TrustDirection.INBOUND;
        }
        // trust type
        if (trust.isInForest()) {
            this.trustType = TrustType.FOREST;
        } else if (trust.isRoot()) {
            this.trustType = TrustType.TREE_ROOT;
        }
    }

    @Override
    public String getFqn() {
        return this.fqn;
    }

    @Override
    public String getTrustDirectionString() {
        return this.trustDirection.toString();
    }

    @Override
    public String getTrustTypeString() {
        return this.trustType.toString();
    }

}
