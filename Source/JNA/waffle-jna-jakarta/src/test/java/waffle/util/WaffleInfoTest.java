/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
class WaffleInfoTest {

    /**
     * Test waffle info.
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     */
    @Test
    void testWaffleInfo() throws ParserConfigurationException {
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
