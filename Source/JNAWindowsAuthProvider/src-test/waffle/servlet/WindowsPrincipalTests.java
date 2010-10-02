/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import waffle.mock.MockWindowsSecurityContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsPrincipalTests extends TestCase {
	
	private WindowsPrincipal _windowsPrincipal = null;
	
	public void setUp() {
		MockWindowsSecurityContext ctx = new MockWindowsSecurityContext("Administrator");
		_windowsPrincipal = new WindowsPrincipal(ctx.getIdentity());
	}
	
	public void testIsSerializable() throws IOException, ClassNotFoundException {
		// serialize
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(out);
	    oos.writeObject(_windowsPrincipal);
	    oos.close();
	    assertTrue(out.toByteArray().length > 0);	    
	    // deserialize
	    InputStream in = new ByteArrayInputStream(out.toByteArray());
	    ObjectInputStream ois = new ObjectInputStream(in);
	    Object o = ois.readObject();
	    WindowsPrincipal copy = (WindowsPrincipal) o;
	    // test
	    assertEquals(_windowsPrincipal.getName(), copy.getName());
	    assertEquals(_windowsPrincipal.getRolesString(), copy.getRolesString());
	    assertEquals(_windowsPrincipal.getSidString(), copy.getSidString());
	    assertEquals(_windowsPrincipal.getSid(), copy.getSid());
	}
	
	public void testHasRole() {
		assertTrue(_windowsPrincipal.hasRole("Administrator"));
		assertTrue(_windowsPrincipal.hasRole("Users"));
		assertTrue(_windowsPrincipal.hasRole("Everyone"));
		assertFalse(_windowsPrincipal.hasRole("RoleDoesNotExist"));
	}
}
