/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * Windows Account Tests.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests {

    /** The mock windows account. */
    private final MockWindowsAccount mockWindowsAccount = new MockWindowsAccount("localhost\\Administrator");

    /** The windows account. */
    private WindowsAccount           windowsAccount;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.windowsAccount = new WindowsAccount(this.mockWindowsAccount);
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals() {
        Assert.assertEquals(this.windowsAccount, new WindowsAccount(this.mockWindowsAccount));
        final MockWindowsAccount mockWindowsAccount2 = new MockWindowsAccount("localhost\\Administrator2");
        Assert.assertFalse(this.windowsAccount.equals(new WindowsAccount(mockWindowsAccount2)));
    }

    /**
     * Test is serializable.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    @Test
    public void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (final ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.windowsAccount);
        }
        Assertions.assertThat(out.toByteArray().length).isGreaterThan(0);
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final WindowsAccount copy = (WindowsAccount) ois.readObject();
        // test
        Assert.assertEquals(this.windowsAccount, copy);
        Assert.assertEquals(this.windowsAccount.getDomain(), copy.getDomain());
        Assert.assertEquals(this.windowsAccount.getFqn(), copy.getFqn());
        Assert.assertEquals(this.windowsAccount.getName(), copy.getName());
        Assert.assertEquals(this.windowsAccount.getSidString(), copy.getSidString());
    }

    /**
     * Test properties.
     */
    @Test
    public void testProperties() {
        Assert.assertEquals("localhost", this.windowsAccount.getDomain());
        Assert.assertEquals("localhost\\Administrator", this.windowsAccount.getFqn());
        Assert.assertEquals("Administrator", this.windowsAccount.getName());
        Assert.assertTrue(this.windowsAccount.getSidString().startsWith("S-"));
    }
}
