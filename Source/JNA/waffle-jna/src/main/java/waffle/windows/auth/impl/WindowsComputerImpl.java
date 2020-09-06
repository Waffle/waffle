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

import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Netapi32Util.LocalGroup;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsComputer;

/**
 * Windows Computer.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsComputerImpl implements IWindowsComputer {

    /** The computer name. */
    private final String computerName;

    /** The domain name. */
    private final String domainName;

    /**
     * Instantiates a new windows computer impl.
     *
     * @param newComputerName
     *            the new computer name
     */
    public WindowsComputerImpl(final String newComputerName) {
        this.computerName = newComputerName;
        this.domainName = Netapi32Util.getDomainName(newComputerName);
    }

    @Override
    public String getComputerName() {
        return this.computerName;
    }

    @Override
    public String[] getGroups() {
        final List<String> groupNames = new ArrayList<>();
        final LocalGroup[] groups = Netapi32Util.getLocalGroups(this.computerName);
        for (final LocalGroup group : groups) {
            groupNames.add(group.name);
        }
        return groupNames.toArray(new String[0]);
    }

    @Override
    public String getJoinStatus() {
        final int joinStatus = Netapi32Util.getJoinStatus(this.computerName);
        switch (joinStatus) {
            case LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName:
                return "NetSetupDomainName";
            case LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnjoined:
                return "NetSetupUnjoined";
            case LMJoin.NETSETUP_JOIN_STATUS.NetSetupWorkgroupName:
                return "NetSetupWorkgroupName";
            case LMJoin.NETSETUP_JOIN_STATUS.NetSetupUnknownStatus:
                return "NetSetupUnknownStatus";
            default:
                throw new RuntimeException("Unsupported join status: " + joinStatus);
        }
    }

    @Override
    public String getMemberOf() {
        return this.domainName;
    }

}
