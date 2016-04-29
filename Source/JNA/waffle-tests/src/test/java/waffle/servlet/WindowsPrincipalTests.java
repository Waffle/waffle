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
package waffle.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsSecurityContext;

/**
 * The Class WindowsPrincipalTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsPrincipalTests {

    /** The windows principal. */
    private WindowsPrincipal windowsPrincipal;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
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
    public void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (final ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this.windowsPrincipal);
        }
        Assertions.assertThat(out.toByteArray().length).isGreaterThan(0);
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final WindowsPrincipal copy = (WindowsPrincipal) ois.readObject();
        // test
        Assert.assertEquals(this.windowsPrincipal.getName(), copy.getName());
        Assert.assertEquals(this.windowsPrincipal.getRolesString(), copy.getRolesString());
        Assert.assertEquals(this.windowsPrincipal.getSidString(), copy.getSidString());
        Assert.assertEquals(Boolean.valueOf(Arrays.equals(this.windowsPrincipal.getSid(), copy.getSid())), Boolean.TRUE);
    }

    /**
     * Test has role.
     */
    @Test
    public void testHasRole() {
        Assert.assertTrue(this.windowsPrincipal.hasRole("Administrator"));
        Assert.assertTrue(this.windowsPrincipal.hasRole("Users"));
        Assert.assertTrue(this.windowsPrincipal.hasRole("Everyone"));
        Assert.assertFalse(this.windowsPrincipal.hasRole("RoleDoesNotExist"));
    }
}
