/*
 * MIT License
 *
 * Copyright (c) 2010-2021 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
 *
 * @author dblock[at]dblock[dot]org
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
    @SuppressWarnings("BanSerializableRead")
    void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (final ObjectOutputStream oos = new ObjectOutputStream(out)) {
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
