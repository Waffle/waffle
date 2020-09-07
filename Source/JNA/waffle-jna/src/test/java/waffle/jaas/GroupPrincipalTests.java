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
package waffle.jaas;

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

/**
 * The Class GroupPrincipalTests.
 *
 * @author rockchip[dot]tv[at]gmail[dot]com
 */
public class GroupPrincipalTests {

    /** The group principal. */
    private GroupPrincipal groupPrincipal;

    /**
     * Equals_other object.
     */
    @Test
    void equals_otherObject() {
        Assertions.assertNotEquals("", this.groupPrincipal);
    }

    /**
     * Equals_same object.
     */
    @Test
    void equals_sameObject() {
        Assertions.assertEquals(this.groupPrincipal, this.groupPrincipal);
    }

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.groupPrincipal = new GroupPrincipal("localhost\\Administrator");
    }

    /**
     * Test equals_ symmetric.
     */
    @Test
    void testEquals_Symmetric() {
        final GroupPrincipal x = new GroupPrincipal("localhost\\Administrator");
        final GroupPrincipal y = new GroupPrincipal("localhost\\Administrator");
        Assertions.assertEquals(x, y);
        Assertions.assertEquals(x.hashCode(), y.hashCode());
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
        try (final ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.groupPrincipal);
        }
        assertThat(out.toByteArray().length).isGreaterThan(0);
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final GroupPrincipal copy = (GroupPrincipal) ois.readObject();
        // test
        Assertions.assertEquals(this.groupPrincipal, copy);
        Assertions.assertEquals(this.groupPrincipal.getName(), copy.getName());
    }

}
