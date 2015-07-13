/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.servlet;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.jna.Platform;

import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;

/**
 * Test the WaffleInfoServlet
 */
public class WaffleInfoServletTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaffleInfoServletTests.class);

    @Test
    public void testGetInfo() throws Exception {
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("hello", "waffle");

        SimpleHttpResponse response = new SimpleHttpResponse();

        WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        String xml = response.getOutputText();
        Document doc = WaffleInfoServletTests.loadXMLFromString(xml);

        WaffleInfoServletTests.LOGGER.info("GOT: {}", xml);

        // Make sure JNA Version is properly noted
        Assert.assertEquals(Platform.class.getPackage().getImplementationVersion(),
                doc.getDocumentElement().getAttribute("jna"));

        Node node = doc.getDocumentElement().getFirstChild().getNextSibling() // request
                .getFirstChild().getNextSibling() // AuthType
                .getNextSibling().getNextSibling();

        // Make sure the headers were added correctly
        Assert.assertEquals("headers", node.getNodeName());
        Node child = node.getFirstChild().getNextSibling();
        Assert.assertEquals("hello", child.getNodeName());
    }

    private static Document loadXMLFromString(String xml) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}