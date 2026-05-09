/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import com.sun.jna.Platform;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import mockit.Expectations;
import mockit.Mocked;

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

    /**
     * Test get exception element.
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     */
    @Test
    void testGetException() throws ParserConfigurationException {
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final RuntimeException ex = new RuntimeException("test error message");
        final Element exElem = WaffleInfo.getException(doc, ex);

        Assertions.assertNotNull(exElem);
        Assertions.assertEquals("exception", exElem.getNodeName());
        Assertions.assertEquals(ex.getClass().getName(), exElem.getAttribute("class"));

        // message child
        final Node messageNode = exElem.getFirstChild();
        Assertions.assertEquals("message", messageNode.getNodeName());
        Assertions.assertEquals("test error message", messageNode.getTextContent());
    }

    /**
     * Test get exception element with null message.
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     */
    @Test
    void testGetExceptionWithNullMessage() throws ParserConfigurationException {
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final RuntimeException ex = new RuntimeException((String) null);
        final Element exElem = WaffleInfo.getException(doc, ex);

        Assertions.assertNotNull(exElem);
        Assertions.assertEquals("exception", exElem.getNodeName());
    }

    /**
     * Test to pretty xml.
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     * @throws TransformerException
     *             the transformer exception
     */
    @Test
    void testToPrettyXml() throws ParserConfigurationException, TransformerException {
        final WaffleInfo helper = new WaffleInfo();
        final Document info = helper.getWaffleInfo();
        final String xml = WaffleInfo.toPrettyXML(info);
        Assertions.assertNotNull(xml);
        Assertions.assertFalse(xml.isEmpty());
        Assertions.assertTrue(xml.contains("waffle"));
    }

    /**
     * Test to pretty xml with a manually created document (no Windows dependency).
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     * @throws TransformerException
     *             the transformer exception
     */
    @Test
    void testToPrettyXmlWithManualDocument() throws ParserConfigurationException, TransformerException {
        final DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        final Document doc = df.newDocumentBuilder().newDocument();
        final Element root = doc.createElement("waffle");
        doc.appendChild(root);
        root.setAttribute("test", "true");

        final String xml = WaffleInfo.toPrettyXML(doc);

        Assertions.assertNotNull(xml);
        Assertions.assertFalse(xml.isEmpty());
        Assertions.assertTrue(xml.contains("waffle"));
        Assertions.assertTrue(xml.contains("test"));
    }

    /**
     * Test add account info populates all fields.
     *
     * @param account
     *            the account
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     */
    @Test
    void testAddAccountInfo(@Mocked final IWindowsAccount account) throws ParserConfigurationException {
        final DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        final Document doc = df.newDocumentBuilder().newDocument();
        final Element node = doc.createElement("testNode");

        Assertions.assertNotNull(new Expectations() {
            {
                account.getName();
                this.result = "testuser";
                account.getFqn();
                this.result = "DOMAIN\\testuser";
                account.getDomain();
                this.result = "DOMAIN";
                account.getSidString();
                this.result = "S-1-5-21-123";
            }
        });

        final WaffleInfo helper = new WaffleInfo();
        helper.addAccountInfo(doc, node, account);

        final NodeList children = node.getChildNodes();
        Assertions.assertEquals(4, children.getLength());
        Assertions.assertEquals("Name", children.item(0).getNodeName());
        Assertions.assertEquals("testuser", children.item(0).getTextContent());
        Assertions.assertEquals("FQN", children.item(1).getNodeName());
        Assertions.assertEquals("DOMAIN\\testuser", children.item(1).getTextContent());
        Assertions.assertEquals("Domain", children.item(2).getNodeName());
        Assertions.assertEquals("DOMAIN", children.item(2).getTextContent());
        Assertions.assertEquals("SID", children.item(3).getNodeName());
        Assertions.assertEquals("S-1-5-21-123", children.item(3).getTextContent());
    }

}
