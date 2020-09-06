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
package waffle.mock;

import java.util.ArrayList;
import java.util.List;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * A Mock windows identity.
 *
 * @author dblock[at]dblock[dot]org
 */
public class MockWindowsIdentity implements IWindowsIdentity {

    /** The fqn. */
    private final String fqn;

    /** The groups. */
    private final List<String> groups;

    /**
     * Instantiates a new mock windows identity.
     *
     * @param newFqn
     *            the new fqn
     * @param newGroups
     *            the new groups
     */
    public MockWindowsIdentity(final String newFqn, final List<String> newGroups) {
        this.fqn = newFqn;
        this.groups = newGroups;
    }

    @Override
    public String getFqn() {
        return this.fqn;
    }

    @Override
    public IWindowsAccount[] getGroups() {
        final List<MockWindowsAccount> groupsList = new ArrayList<>();
        for (final String group : this.groups) {
            groupsList.add(new MockWindowsAccount(group));
        }
        return groupsList.toArray(new IWindowsAccount[0]);
    }

    @Override
    public byte[] getSid() {
        return new byte[0];
    }

    @Override
    public String getSidString() {
        return "S-" + this.fqn.hashCode();
    }

    @Override
    public void dispose() {
        // Do Nothing
    }

    @Override
    public boolean isGuest() {
        return "Guest".equals(this.fqn);
    }

    @Override
    public IWindowsImpersonationContext impersonate() {
        return new MockWindowsImpersonationContext();
    }

}
