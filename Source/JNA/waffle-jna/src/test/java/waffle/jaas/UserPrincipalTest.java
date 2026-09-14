/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
 * The Class UserPrincipalTest.
 */
class UserPrincipalTest {

    /** The user principal. */
    private UserPrincipal userPrincipal;

    /**
     * Equals_other object.
     */
    @Test
    void equals_otherObject() {
        Assertions.assertNotEquals("", this.userPrincipal);
    }

    /**
     * Equals_same object.
     */
    @Test
    void equals_sameObject() {
        Assertions.assertEquals(this.userPrincipal, this.userPrincipal);
    }

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.userPrincipal = new UserPrincipal("localhost\\Administrator");
    }

    /**
     * Test equals_ symmetric.
     */
    @Test
    void testEquals_Symmetric() {
        final UserPrincipal x = new UserPrincipal("localhost\\Administrator");
        final UserPrincipal y = new UserPrincipal("localhost\\Administrator");
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
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.userPrincipal);

            // test serialize
            assertThat(out.toByteArray()).isNotEmpty();

            try (InputStream in = new ByteArrayInputStream(out.toByteArray());
                    ObjectInputStream ois = new ObjectInputStream(in)) {

                UserPrincipal copy = (UserPrincipal) ois.readObject();

                // test deserialize
                Assertions.assertEquals(this.userPrincipal, copy);
                Assertions.assertEquals(this.userPrincipal.getName(), copy.getName());
            }
        }
    }

}
