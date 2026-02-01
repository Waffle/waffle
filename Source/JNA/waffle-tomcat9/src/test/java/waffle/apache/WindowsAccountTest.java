/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
 * Windows Account Test.
 */
class WindowsAccountTest {

    /** The mock windows account. */
    private final MockWindowsAccount mockWindowsAccount = new MockWindowsAccount("localhost\\Administrator");

    /** The windows account. */
    private WindowsAccount windowsAccount;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.windowsAccount = new WindowsAccount(this.mockWindowsAccount);
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals() {
        Assertions.assertEquals(this.windowsAccount, new WindowsAccount(this.mockWindowsAccount));
        final MockWindowsAccount mockWindowsAccount2 = new MockWindowsAccount("localhost\\Administrator2");
        Assertions.assertNotEquals(this.windowsAccount, new WindowsAccount(mockWindowsAccount2));
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
    void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.windowsAccount);
        }
        assertThat(out.toByteArray()).isNotEmpty();
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
    void testProperties() {
        Assertions.assertEquals("localhost", this.windowsAccount.getDomain());
        Assertions.assertEquals("localhost\\Administrator", this.windowsAccount.getFqn());
        Assertions.assertEquals("Administrator", this.windowsAccount.getName());
        Assertions.assertTrue(this.windowsAccount.getSidString().startsWith("S-"));
    }

}
