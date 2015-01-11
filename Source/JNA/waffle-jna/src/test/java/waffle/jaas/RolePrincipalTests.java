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
public class RolePrincipalTests {

    private RolePrincipal rolePrincipal;

    @Test
    public void equals_otherObject() {
        Assert.assertNotEquals(this.rolePrincipal, new String());
    }

    @Test
    public void equals_sameObject() {
        Assert.assertEquals(this.rolePrincipal, this.rolePrincipal);
    }

    @Before
    public void setUp() {
        this.rolePrincipal = new RolePrincipal("localhost\\Administrator");
    }

    @Test
    public void testEquals_Symmetric() {
        final RolePrincipal x = new RolePrincipal("localhost\\Administrator");
        final RolePrincipal y = new RolePrincipal("localhost\\Administrator");
        Assert.assertEquals(x, y);
        Assert.assertEquals(x.hashCode(), y.hashCode());
    }

    @Test
    public void testIsSerializable() throws IOException, ClassNotFoundException {
        // serialize
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(this.rolePrincipal);
        oos.close();
        Assertions.assertThat(out.toByteArray().length).isGreaterThan(0);
        // deserialize
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(in);
        final RolePrincipal copy = (RolePrincipal) ois.readObject();
        // test
        Assert.assertEquals(this.rolePrincipal, copy);
        Assert.assertEquals(this.rolePrincipal.getName(), copy.getName());
    }

}
