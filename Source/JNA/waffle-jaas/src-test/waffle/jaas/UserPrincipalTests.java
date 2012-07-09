/*******************************************************************************
* Waffle (https://github.com/dblock/waffle)
* 
* Copyright (c) 2010 Application Security, Inc.
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Application Security, Inc.
*******************************************************************************/
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
	
	@Override
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
