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
package waffle.jaas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class UserPrincipalTests {

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() {
        this.userPrincipal = new UserPrincipal("localhost\\Administrator");
    }

    @Test
    public void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(this.userPrincipal);
        oos.close();
        assertTrue(out.toByteArray().length > 0);
        // deserialize
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();
        UserPrincipal copy = (UserPrincipal) o;
        // test
        assertEquals(this.userPrincipal, copy);
        assertEquals(this.userPrincipal.getName(), copy.getName());
    }
}
