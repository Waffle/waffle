/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dblock[at]dblock[dot]org
 */
public class UserPrincipalTests {

    private UserPrincipal userPrincipal;

    @Test
    public void equals_otherObject() {
        Assert.assertNotEquals(this.userPrincipal, new String());
    }

    @Test
    public void equals_sameObject() {
        Assert.assertEquals(this.userPrincipal, this.userPrincipal);
    }

    @Before
    public void setUp() {
        this.userPrincipal = new UserPrincipal("localhost\\Administrator");
    }

    @Test
    public void testEquals_Symmetric() {
        final UserPrincipal x = new UserPrincipal("localhost\\Administrator");
        final UserPrincipal y = new UserPrincipal("localhost\\Administrator");
        Assert.assertEquals(x, y);
        Assert.assertEquals(x.hashCode(), y.hashCode());
    }

    @Test
    public void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(this.userPrincipal);
        oos.close();
        Assertions.assertThat(out.toByteArray().length).isGreaterThan(0);
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final UserPrincipal copy = (UserPrincipal) ois.readObject();
        // test
        Assert.assertEquals(this.userPrincipal, copy);
        Assert.assertEquals(this.userPrincipal.getName(), copy.getName());
    }

}
