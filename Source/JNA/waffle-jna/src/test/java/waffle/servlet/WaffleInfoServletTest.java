/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import waffle.util.WaffleInfo;

/**
 * Tests for {@link WaffleInfoServlet}.
 */
class WaffleInfoServletTest {

    /** The mocked WaffleInfo (intercepts all WaffleInfo instances). */
    @Mocked
    private WaffleInfo waffleInfo;

    /**
     * Creates a simple waffle document for testing purposes.
     *
     * @return a simple Document with a root waffle element containing an auth child
     *
     * @throws Exception
     *             the exception
     */
    private static Document createSimpleWaffleDocument() throws Exception {
        final DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        final Document doc = df.newDocumentBuilder().newDocument();
        final Element root = doc.createElement("waffle");
        doc.appendChild(root);
        final Element auth = doc.createElement("auth");
        root.appendChild(auth);
        return doc;
    }

    /**
     * Test do get without principal and headers writes XML response.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoGetNoPrincipalNoHeaders(@Mocked final HttpServletRequest request,
            @Mocked final HttpServletResponse response) throws Exception {
        final Document doc = WaffleInfoServletTest.createSimpleWaffleDocument();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        new Expectations() {
            {
                WaffleInfoServletTest.this.waffleInfo.getWaffleInfo();
                this.result = doc;
                request.getAuthType();
                this.result = null;
                request.getUserPrincipal();
                this.result = null;
                request.getHeaderNames();
                this.result = Collections.emptyEnumeration();
                request.getParameterValues("lookup");
                this.result = null;
                response.getWriter();
                this.result = pw;
            }
        };

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        pw.flush();
        final String output = sw.toString();
        Assertions.assertNotNull(output);
        Assertions.assertTrue(output.contains("waffle"));
    }

    /**
     * Test do post delegates to get waffle info response.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoPost(@Mocked final HttpServletRequest request, @Mocked final HttpServletResponse response)
            throws Exception {
        final Document doc = WaffleInfoServletTest.createSimpleWaffleDocument();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        new Expectations() {
            {
                WaffleInfoServletTest.this.waffleInfo.getWaffleInfo();
                this.result = doc;
                request.getAuthType();
                this.result = null;
                request.getUserPrincipal();
                this.result = null;
                request.getHeaderNames();
                this.result = Collections.emptyEnumeration();
                request.getParameterValues("lookup");
                this.result = null;
                response.getWriter();
                this.result = pw;
            }
        };

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doPost(request, response);

        pw.flush();
        Assertions.assertTrue(sw.toString().contains("waffle"));
    }

    /**
     * Test do get includes auth type in output.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoGetWithAuthType(@Mocked final HttpServletRequest request, @Mocked final HttpServletResponse response)
            throws Exception {
        final Document doc = WaffleInfoServletTest.createSimpleWaffleDocument();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        new Expectations() {
            {
                WaffleInfoServletTest.this.waffleInfo.getWaffleInfo();
                this.result = doc;
                request.getAuthType();
                this.result = "NTLM";
                request.getUserPrincipal();
                this.result = null;
                request.getHeaderNames();
                this.result = Collections.emptyEnumeration();
                request.getParameterValues("lookup");
                this.result = null;
                response.getWriter();
                this.result = pw;
            }
        };

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        pw.flush();
        Assertions.assertTrue(sw.toString().contains("NTLM"));
    }

    /**
     * Test do get with principal includes principal info in output.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @param principal
     *            the principal
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoGetWithPrincipal(@Mocked final HttpServletRequest request, @Mocked final HttpServletResponse response,
            @Mocked final Principal principal) throws Exception {
        final Document doc = WaffleInfoServletTest.createSimpleWaffleDocument();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        new Expectations() {
            {
                WaffleInfoServletTest.this.waffleInfo.getWaffleInfo();
                this.result = doc;
                request.getAuthType();
                this.result = null;
                request.getUserPrincipal();
                this.result = principal;
                principal.getName();
                this.result = "testuser";
                principal.toString();
                this.result = "testuser";
                request.getHeaderNames();
                this.result = Collections.emptyEnumeration();
                request.getParameterValues("lookup");
                this.result = null;
                response.getWriter();
                this.result = pw;
            }
        };

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        pw.flush();
        Assertions.assertTrue(sw.toString().contains("testuser"));
    }

    /**
     * Test do get with headers includes header info in output.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoGetWithHeaders(@Mocked final HttpServletRequest request, @Mocked final HttpServletResponse response)
            throws Exception {
        final Document doc = WaffleInfoServletTest.createSimpleWaffleDocument();
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        new Expectations() {
            {
                WaffleInfoServletTest.this.waffleInfo.getWaffleInfo();
                this.result = doc;
                request.getAuthType();
                this.result = null;
                request.getUserPrincipal();
                this.result = null;
                request.getHeaderNames();
                this.result = Collections.enumeration(java.util.Arrays.asList("Authorization", "Host"));
                request.getHeader("Authorization");
                this.result = "Bearer token";
                request.getHeader("Host");
                this.result = "localhost";
                request.getParameterValues("lookup");
                this.result = null;
                response.getWriter();
                this.result = pw;
            }
        };

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        pw.flush();
        final String output = sw.toString();
        Assertions.assertTrue(output.contains("Authorization") || output.contains("Host"));
    }

    /**
     * Test do get with lookup parameter includes lookup in output.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     *
     * @throws Exception
     *             the exception
     */
    @Test
    void testDoGetWithLookup(@Mocked final HttpServletRequest request, @Mocked final HttpServletResponse response)
            throws Exception {
        final Document doc = WaffleInfoServletTest.createSimpleWaffleDocument();
        final Element lookupElem = doc.createElement("lookup");
        lookupElem.setAttribute("name", "testlookup");
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        new Expectations() {
            {
                WaffleInfoServletTest.this.waffleInfo.getWaffleInfo();
                this.result = doc;
                request.getAuthType();
                this.result = null;
                request.getUserPrincipal();
                this.result = null;
                request.getHeaderNames();
                this.result = Collections.emptyEnumeration();
                request.getParameterValues("lookup");
                this.result = new String[] { "testlookup" };
                WaffleInfoServletTest.this.waffleInfo.getLookupInfo(doc, "testlookup");
                this.result = lookupElem;
                response.getWriter();
                this.result = pw;
            }
        };

        final WaffleInfoServlet servlet = new WaffleInfoServlet();
        servlet.doGet(request, response);

        pw.flush();
        Assertions.assertTrue(sw.toString().contains("lookup"));
    }

}
