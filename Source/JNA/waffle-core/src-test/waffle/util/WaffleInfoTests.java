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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import junit.framework.TestCase;

/**
 */
public class WaffleInfoTests extends TestCase {
	public void testWaffleInfo() throws Exception {
	  Document info = new WaffleInfo().getSystemInfo();
	 
	  Node elem = info.getDocumentElement() // waffle
	      .getFirstChild()   // auth
	      .getFirstChild()   // currentUser
	      .getNextSibling(); // computer
	  
	  assertEquals("computer", elem.getNodeName() );
	  
    IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
	  IWindowsComputer computer = auth.getCurrentComputer();
	  
	  NodeList nodes = elem.getChildNodes();
    assertEquals(computer.getComputerName(), nodes.item(0).getTextContent());
    assertEquals(computer.getMemberOf(), nodes.item(1).getTextContent());
    assertEquals(computer.getJoinStatus(), nodes.item(2).getTextContent());  
	}
}
