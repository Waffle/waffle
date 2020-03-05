/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private WindowsAccount windowsAccount;

    /**
     * Sets the up.
     */
    @BeforeEach
    public void setUp() {
        this.windowsAccount = new WindowsAccount(this.mockWindowsAccount);
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals() {
        Assertions.assertEquals(this.windowsAccount, new WindowsAccount(this.mockWindowsAccount));
        final MockWindowsAccount mockWindowsAccount2 = new MockWindowsAccount("localhost\\Administrator2");
        Assertions.assertFalse(this.windowsAccount.equals(new WindowsAccount(mockWindowsAccount2)));
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
        assertThat(out.toByteArray().length).isGreaterThan(0);
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final WindowsAccount copy = (WindowsAccount) ois.readObject();
        // test
        Assertions.assertEquals(this.windowsAccount, copy);
        Assertions.assertEquals(this.windowsAccount.getDomain(), copy.getDomain());
        Assertions.assertEquals(this.windowsAccount.getFqn(), copy.getFqn());
        Assertions.assertEquals(this.windowsAccount.getName(), copy.getName());
        Assertions.assertEquals(this.windowsAccount.getSidString(), copy.getSidString());
    }

    /**
     * Test properties.
     */
    @Test
    public void testProperties() {
        Assertions.assertEquals("localhost", this.windowsAccount.getDomain());
        Assertions.assertEquals("localhost\\Administrator", this.windowsAccount.getFqn());
        Assertions.assertEquals("Administrator", this.windowsAccount.getName());
        Assertions.assertTrue(this.windowsAccount.getSidString().startsWith("S-"));
    }
}
