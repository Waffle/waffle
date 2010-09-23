/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests extends TestCase {
	
	MockWindowsAccount _mockWindowsAccount = new MockWindowsAccount("localhost\\Administrator");
	WindowsAccount _windowsAccount = null;
	
	@Override
	public void setUp() {
		_windowsAccount = new WindowsAccount(_mockWindowsAccount);
	}
	
	public void testProperties() {
		assertEquals("localhost", _windowsAccount.getDomain());
		assertEquals("localhost\\Administrator", _windowsAccount.getFqn());
		assertEquals("Administrator", _windowsAccount.getName());
		assertTrue(_windowsAccount.getSidString().startsWith("S-"));
	}
	
	public void testEquals() {
		assertTrue(_windowsAccount.equals(new WindowsAccount(_mockWindowsAccount)));
		MockWindowsAccount mockWindowsAccount2 = new MockWindowsAccount("localhost\\Administrator2");
		assertFalse(_windowsAccount.equals(new WindowsAccount(mockWindowsAccount2)));
	}
	
	public void testIsSerializable() throws IOException, ClassNotFoundException {
		// serialize
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(out);
	    oos.writeObject(_windowsAccount);
	    oos.close();
	    assertTrue(out.toByteArray().length > 0);	    
	    // deserialize
	    InputStream in = new ByteArrayInputStream(out.toByteArray());
	    ObjectInputStream ois = new ObjectInputStream(in);
	    Object o = ois.readObject();
	    WindowsAccount copy = (WindowsAccount) o;
	    // test
	    assertEquals(_windowsAccount, copy);
	    assertEquals(_windowsAccount.getDomain(), copy.getDomain());
	    assertEquals(_windowsAccount.getFqn(), copy.getFqn());
	    assertEquals(_windowsAccount.getName(), copy.getName());
	    assertEquals(_windowsAccount.getSidString(), copy.getSidString());
	}
}
