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
package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsDomain;

import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;

/**
 * Windows Domain
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsDomainImpl implements IWindowsDomain {

    private enum TrustDirection {
        INBOUND, OUTBOUND, BIDIRECTIONAL
    }

    private enum TrustType {
        TREE_ROOT, PARENT_CHILD, CROSS_LINK, EXTERNAL, FOREST, KERBEROS, UNKNOWN
    }

    private String         fqn;
    private TrustDirection trustDirection = TrustDirection.BIDIRECTIONAL;
    private TrustType      trustType      = TrustType.UNKNOWN;

    public WindowsDomainImpl(final String newFqn) {
        this.fqn = newFqn;
    }

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
