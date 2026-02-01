/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import waffle.mock.MockWindowsSecurityContext;

/**
 * The Class WindowsPrincipalTest.
 */
class WindowsPrincipalTest {

    /** The windows principal. */
    private WindowsPrincipal windowsPrincipal;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        final MockWindowsSecurityContext ctx = new MockWindowsSecurityContext("Administrator");
        this.windowsPrincipal = new WindowsPrincipal(ctx.getIdentity());
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
            oos.writeObject(this.windowsPrincipal);
        }
        assertThat(out.toByteArray()).isNotEmpty();
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final WindowsPrincipal copy = (WindowsPrincipal) ois.readObject();
        // test
        Assertions.assertEquals(this.windowsPrincipal.getName(), copy.getName());
        Assertions.assertEquals(this.windowsPrincipal.getRolesString(), copy.getRolesString());
        Assertions.assertEquals(this.windowsPrincipal.getSidString(), copy.getSidString());
        Assertions.assertTrue(Arrays.equals(this.windowsPrincipal.getSid(), copy.getSid()));
    }

    /**
     * Test has role.
     */
    @Test
    void testHasRole() {
        Assertions.assertTrue(this.windowsPrincipal.hasRole("Administrator"));
        Assertions.assertTrue(this.windowsPrincipal.hasRole("Users"));
        Assertions.assertTrue(this.windowsPrincipal.hasRole("Everyone"));
        Assertions.assertFalse(this.windowsPrincipal.hasRole("RoleDoesNotExist"));
    }
}
