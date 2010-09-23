/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipalTests extends TestCase {
	
	private RolePrincipal _rolePrincipal = null;
	
	public void setUp() {
		_rolePrincipal = new RolePrincipal("localhost\\Administrator");
	}
	
	public void testIsSerializable() throws IOException, ClassNotFoundException {
		// serialize
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(out);
	    oos.writeObject(_rolePrincipal);
	    oos.close();
	    assertTrue(out.toByteArray().length > 0);	    
	    // deserialize
	    InputStream in = new ByteArrayInputStream(out.toByteArray());
	    ObjectInputStream ois = new ObjectInputStream(in);
	    Object o = ois.readObject();
	    RolePrincipal copy = (RolePrincipal) o;
	    // test
	    assertEquals(_rolePrincipal, copy);
	    assertEquals(_rolePrincipal.getName(), copy.getName());
	}
}
