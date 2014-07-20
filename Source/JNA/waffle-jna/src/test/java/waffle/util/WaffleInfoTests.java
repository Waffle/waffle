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
package waffle.util;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import com.sun.jna.Platform;

/**
 * Build an info document and check that it has the right values
 */
public class WaffleInfoTests {

	@Test
	public void testWaffleInfo() throws ParserConfigurationException {
		WaffleInfo helper = new WaffleInfo();
		Document info = helper.getWaffleInfo();

		// Make sure JNA Version is properly noted
		assertEquals(Platform.class.getPackage().getImplementationVersion(),
				info.getDocumentElement().getAttribute("jna"));

		Node node = info.getDocumentElement() // waffle
				.getFirstChild() // auth
				.getFirstChild() // currentUser
				.getNextSibling(); // computer

		assertEquals("computer", node.getNodeName());

		IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
		IWindowsComputer computer = auth.getCurrentComputer();

		NodeList nodes = node.getChildNodes();
		assertEquals(computer.getComputerName(), nodes.item(0).getTextContent());
		assertEquals(computer.getMemberOf(), nodes.item(1).getTextContent());
		assertEquals(computer.getJoinStatus(), nodes.item(2).getTextContent());

		// Add Lookup Info for Various accounts
		String lookup = WindowsAccountImpl.getCurrentUsername();
		IWindowsAccount account = new WindowsAccountImpl(lookup);
		Element elem = helper.getLookupInfo(info, lookup);
		assertEquals(lookup, elem.getAttribute("name"));
		assertEquals(account.getName(), elem.getFirstChild().getTextContent());

		// Report an error when unknown name
		lookup = "__UNKNOWN_ACCOUNT_NAME___";
		elem = helper.getLookupInfo(info, lookup);
		assertEquals(lookup, elem.getAttribute("name"));
		assertEquals("exception", elem.getFirstChild().getNodeName());
	}
}
