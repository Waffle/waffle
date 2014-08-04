/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsSecurityContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsPrincipalTests {

    private WindowsPrincipal windowsPrincipal;

    @Before
    public void setUp() {
        MockWindowsSecurityContext ctx = new MockWindowsSecurityContext("Administrator");
        this.windowsPrincipal = new WindowsPrincipal(ctx.getIdentity());
    }

    @Test
    public void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(this.windowsPrincipal);
        oos.close();
        assertTrue(out.toByteArray().length > 0);
        // deserialize
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();
        WindowsPrincipal copy = (WindowsPrincipal) o;
        // test
        assertEquals(this.windowsPrincipal.getName(), copy.getName());
        assertEquals(this.windowsPrincipal.getRolesString(), copy.getRolesString());
        assertEquals(this.windowsPrincipal.getSidString(), copy.getSidString());
        assertEquals(Boolean.valueOf(Arrays.equals(this.windowsPrincipal.getSid(), copy.getSid())), Boolean.TRUE);
    }

    @Test
    public void testHasRole() {
        assertTrue(this.windowsPrincipal.hasRole("Administrator"));
        assertTrue(this.windowsPrincipal.hasRole("Users"));
        assertTrue(this.windowsPrincipal.hasRole("Everyone"));
        assertFalse(this.windowsPrincipal.hasRole("RoleDoesNotExist"));
    }
}
