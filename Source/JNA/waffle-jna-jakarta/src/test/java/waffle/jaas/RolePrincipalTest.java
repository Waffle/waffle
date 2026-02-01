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
 * The Class RolePrincipalTest.
 */
class RolePrincipalTest {

    /** The role principal. */
    private RolePrincipal rolePrincipal;

    /**
     * Equals_other object.
     */
    @Test
    void equals_otherObject() {
        Assertions.assertNotEquals("", this.rolePrincipal);
    }

    /**
     * Equals_same object.
     */
    @Test
    void equals_sameObject() {
        Assertions.assertEquals(this.rolePrincipal, this.rolePrincipal);
    }

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.rolePrincipal = new RolePrincipal("localhost\\Administrator");
    }

    /**
     * Test equals_ symmetric.
     */
    @Test
    void testEquals_Symmetric() {
        final RolePrincipal x = new RolePrincipal("localhost\\Administrator");
        final RolePrincipal y = new RolePrincipal("localhost\\Administrator");
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
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.rolePrincipal);
        }
        assertThat(out.toByteArray()).isNotEmpty();
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final RolePrincipal copy = (RolePrincipal) ois.readObject();
        // test
        Assertions.assertEquals(this.rolePrincipal, copy);
        Assertions.assertEquals(this.rolePrincipal.getName(), copy.getName());
    }

}
