/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util;

import com.sun.jna.Platform;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * Build an info document and check that it has the right values.
 */
public class WaffleInfoTests {

    /**
     * Test waffle info.
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     */
    @Test
    public void testWaffleInfo() throws ParserConfigurationException {
        final WaffleInfo helper = new WaffleInfo();
        final Document info = helper.getWaffleInfo();

        // Make sure JNA Version is properly noted
        Assertions.assertEquals(Platform.class.getPackage().getImplementationVersion(),
                info.getDocumentElement().getAttribute("jna"));

        // waffle auth currentUser computer
        final Node node = info.getDocumentElement().getFirstChild().getFirstChild().getNextSibling();

        Assertions.assertEquals("computer", node.getNodeName());

        final IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
        final IWindowsComputer computer = auth.getCurrentComputer();

        final NodeList nodes = node.getChildNodes();
        Assertions.assertEquals(computer.getComputerName(), nodes.item(0).getTextContent());
        Assertions.assertEquals(computer.getMemberOf(), nodes.item(1).getTextContent());
        Assertions.assertEquals(computer.getJoinStatus(), nodes.item(2).getTextContent());

        // Add Lookup Info for Various accounts
        String lookup = WindowsAccountImpl.getCurrentUsername();
        final IWindowsAccount account = new WindowsAccountImpl(lookup);
        Element elem = helper.getLookupInfo(info, lookup);
        Assertions.assertEquals(lookup, elem.getAttribute("name"));
        Assertions.assertEquals(account.getName(), elem.getFirstChild().getTextContent());

        // Report an error when unknown name
        lookup = "__UNKNOWN_ACCOUNT_NAME___";
        elem = helper.getLookupInfo(info, lookup);
        Assertions.assertEquals(lookup, elem.getAttribute("name"));
        Assertions.assertEquals("exception", elem.getFirstChild().getNodeName());
    }
}
