/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.servlet;

import com.sun.jna.Platform;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import waffle.mock.http.SimpleHttpRequest;
import waffle.mock.http.SimpleHttpResponse;

/**
 * Test the WaffleInfoServlet.
 */
public class WaffleInfoServletTests {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WaffleInfoServletTests.class);

    /**
     * Test get info.
     *
     * @throws ParserConfigurationException
     *             the parser configuration exception
     * @throws SAXException
     *             the SAX exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    @Test
    void testGetInfo() throws ParserConfigurationException, SAXException, IOException, ServletException {
        final SimpleHttpRequest request = new SimpleHttpRequest();
        request.addHeader("hello", "waffle");

        final SimpleHttpResponse response = new SimpleHttpResponse();

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        final String xml = response.getOutputText();
        final Document doc = WaffleInfoServletTests.loadXMLFromString(xml);

        WaffleInfoServletTests.LOGGER.info("GOT: {}", xml);

        // Make sure JNA Version is properly noted
        Assertions.assertEquals(Platform.class.getPackage().getImplementationVersion(),
                doc.getDocumentElement().getAttribute("jna"));

        final Node node = doc.getDocumentElement().getFirstChild().getNextSibling() // request
                .getFirstChild().getNextSibling() // AuthType
                .getNextSibling().getNextSibling();

        // Make sure the headers were added correctly
        Assertions.assertEquals("headers", node.getNodeName());
        final Node child = node.getFirstChild().getNextSibling();
        Assertions.assertEquals("hello", child.getNodeName());
    }

    /**
     * Load xml from string.
     *
     * @param xml
     *            the xml
     * @return the document
     * @throws ParserConfigurationException
     *             the parser configuration exception
     * @throws SAXException
     *             the SAX exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static Document loadXMLFromString(final String xml)
            throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}
