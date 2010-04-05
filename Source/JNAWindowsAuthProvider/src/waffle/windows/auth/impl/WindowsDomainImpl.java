package waffle.windows.auth.impl;

import com.sun.jna.platform.win32.Netapi32Util.DomainTrust;

import waffle.windows.auth.IWindowsDomain;

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
	
	@Override
	public String getFqn() {
		return _fqn;
	}

	@Override
	public String getTrustDirectionString() {
		return _trustDirection.toString();
	}

	@Override
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
