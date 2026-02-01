/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;

import waffle.windows.auth.IWindowsDomain;

/**
 * Windows Domain.
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
