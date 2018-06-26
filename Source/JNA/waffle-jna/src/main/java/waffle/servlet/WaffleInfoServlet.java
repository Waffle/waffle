/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import waffle.util.WaffleInfo;

/**
 * A servlet that returns WaffleInfo as XML.
 */
public class WaffleInfoServlet extends HttpServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        this.getWaffleInfoResponse(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        this.getWaffleInfoResponse(request, response);
    }

    /**
     * Gets the waffle info response.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    public void getWaffleInfoResponse(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
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
            final Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            final StreamResult result = new StreamResult(response.getWriter());
            final DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            response.setContentType("application/xml");
        } catch (final ParserConfigurationException e) {
            throw new ServletException(e);
        } catch (final TransformerConfigurationException e) {
            throw new ServletException(e);
        } catch (final TransformerException e) {
            throw new ServletException(e);
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

        final Enumeration<?> headers = request.getHeaderNames();
        if (headers.hasMoreElements()) {
            String name;
            final Element child = doc.createElement("headers");
            while (headers.hasMoreElements()) {
                name = (String) headers.nextElement();

                value = doc.createElement(name);
                value.setTextContent(request.getHeader(name));
                child.appendChild(value);
            }
            node.appendChild(child);
        }
        return node;
    }
}
