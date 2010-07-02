/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.windows.auth.impl;

import waffle.windows.auth.IWindowsDomain;

import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;

/**
 * Windows Domain
 * @author dblock[at]dblock[dot]org
 */
public class WindowsDomainImpl implements IWindowsDomain {

	private enum TrustDirection {
		Inbound,
		Outbound,
		Bidirectional
	}

	private enum TrustType {
		TreeRoot,
		ParentChild,
		CrossLink,
		External, 
		Forest,
		Kerberos,
		Unknown
	}

	private String _fqn;
	private TrustDirection _trustDirection = TrustDirection.Bidirectional;
	private TrustType _trustType = TrustType.Unknown;
	
	public String getFqn() {
		return _fqn;
	}

	public String getTrustDirectionString() {
		return _trustDirection.toString();
	}

	public String getTrustTypeString() {
		return _trustType.toString();
	}
	
	public WindowsDomainImpl(String fqn) {
		_fqn = fqn;
	}

	public WindowsDomainImpl(DomainTrust trust) {
		// fqn
		_fqn = trust.DnsDomainName;
		if (_fqn == null || _fqn.length() == 0) {
			_fqn = trust.NetbiosDomainName;
		}
		// trust direction
		if (trust.isInbound() && trust.isOutbound()) {
			_trustDirection = TrustDirection.Bidirectional;
		} else if (trust.isOutbound()) {
			_trustDirection = TrustDirection.Outbound;
		} else if (trust.isInbound()) {
			_trustDirection = TrustDirection.Inbound;
		}
		// trust type
		if (trust.isInForest()) {
			_trustType = TrustType.Forest;
		} else if (trust.isRoot()) {
			_trustType = TrustType.TreeRoot;
		}
	}
}
