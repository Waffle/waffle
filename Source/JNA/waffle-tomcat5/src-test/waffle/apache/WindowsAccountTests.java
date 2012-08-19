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
package waffle.apache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests {

	MockWindowsAccount _mockWindowsAccount = new MockWindowsAccount(
			"localhost\\Administrator");
	WindowsAccount _windowsAccount = null;

	@Before
	public void setUp() {
		_windowsAccount = new WindowsAccount(_mockWindowsAccount);
	}

	@Test
	public void testProperties() {
		assertEquals("localhost", _windowsAccount.getDomain());
		assertEquals("localhost\\Administrator", _windowsAccount.getFqn());
		assertEquals("Administrator", _windowsAccount.getName());
		assertTrue(_windowsAccount.getSidString().startsWith("S-"));
	}

	@Test
	public void testEquals() {
		assertEquals(_windowsAccount, new WindowsAccount(
				_mockWindowsAccount));
		MockWindowsAccount mockWindowsAccount2 = new MockWindowsAccount(
				"localhost\\Administrator2");
		assertFalse(_windowsAccount.equals(new WindowsAccount(
				mockWindowsAccount2)));
	}

	@Test
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
