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
public class UserPrincipalTests extends TestCase {
	
	private UserPrincipal _userPrincipal = null;
	
	public void setUp() {
		_userPrincipal = new UserPrincipal("localhost\\Administrator");
	}
	
	public void testIsSerializable() throws IOException, ClassNotFoundException {
		// serialize
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(out);
	    oos.writeObject(_userPrincipal);
	    oos.close();
	    assertTrue(out.toByteArray().length > 0);	    
	    // deserialize
	    InputStream in = new ByteArrayInputStream(out.toByteArray());
	    ObjectInputStream ois = new ObjectInputStream(in);
	    Object o = ois.readObject();
	    UserPrincipal copy = (UserPrincipal) o;
	    // test
	    assertEquals(_userPrincipal, copy);
	    assertEquals(_userPrincipal.getName(), copy.getName());
	}
}
