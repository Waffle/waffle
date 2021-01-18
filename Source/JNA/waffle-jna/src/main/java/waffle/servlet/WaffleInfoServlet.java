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

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import waffle.util.WaffleInfo;

/**
 * A servlet that returns WaffleInfo as XML.
 */
public class WaffleInfoServlet extends HttpServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant Logger. */
    private static final Logger logger = LoggerFactory.getLogger(WaffleInfoServlet.class);

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        this.getWaffleInfoResponse(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        this.getWaffleInfoResponse(request, response);
    }

    /**
     * Gets the waffle info response.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public void getWaffleInfoResponse(final HttpServletRequest request, final HttpServletResponse response) {
        final WaffleInfo info = new WaffleInfo();
        try {
            final Document doc = info.getWaffleInfo();
            final Element root = doc.getDocumentElement();

            // Add the Request Information Here
            final Element http = this.getRequestInfo(doc, request);
            root.insertBefore(http, root.getFirstChild());

            // Lookup Accounts By Name
            final String[] lookup = request.getParameterValues("lookup");
            if (lookup != null) {
                for (final String name : lookup) {
                    root.appendChild(info.getLookupInfo(doc, name));
                }
            }

            // Write the XML Response
            final TransformerFactory transfac = TransformerFactory.newInstance();
            transfac.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transfac.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            final Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            final StreamResult result = new StreamResult(response.getWriter());
            final DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            response.setContentType("application/xml");
        } catch (final ParserConfigurationException | TransformerException | IOException e) {
            WaffleInfoServlet.logger.error("", e);
            throw new RuntimeException("See logs for underlying error condition");
        }
    }

    /**
     * Gets the request info.
     *
     * @param doc
     *            the doc
     * @param request
     *            the request
     * @return the request info
     */
    private Element getRequestInfo(final Document doc, final HttpServletRequest request) {
        final Element node = doc.createElement("request");

        Element value = doc.createElement("AuthType");
        value.setTextContent(request.getAuthType());
        node.appendChild(value);

        final Principal p = request.getUserPrincipal();
        if (p != null) {
            final Element child = doc.createElement("principal");
            child.setAttribute("class", p.getClass().getName());

            value = doc.createElement("name");
            value.setTextContent(p.getName());
            child.appendChild(value);

            value = doc.createElement("string");
            value.setTextContent(p.toString());
            child.appendChild(value);

            node.appendChild(child);
        }

        final List<String> headers = Collections.list(request.getHeaderNames());
        if (!headers.isEmpty()) {
            final Element child = doc.createElement("headers");
            for (String header : headers) {
                value = doc.createElement(header);
                value.setTextContent(request.getHeader(header));
                child.appendChild(value);
            }
            node.appendChild(child);
        }
        return node;
    }

}
