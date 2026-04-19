/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleHttpResponse}.
 */
class SimpleHttpResponseTest {

    /** The response. */
    private SimpleHttpResponse response;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.response = new SimpleHttpResponse();
    }

    /**
     * Test initial status is 500.
     */
    @Test
    void testInitialStatus() {
        Assertions.assertEquals(500, this.response.getStatus());
    }

    /**
     * Test send error with int sets status.
     */
    @Test
    void testSendErrorInt() {
        this.response.sendError(403);
        Assertions.assertEquals(403, this.response.getStatus());
    }

    /**
     * Test send error with int and message sets status.
     */
    @Test
    void testSendErrorIntString() {
        this.response.sendError(404, "Not Found");
        Assertions.assertEquals(404, this.response.getStatus());
    }

    /**
     * Test set status.
     */
    @Test
    void testSetStatus() {
        this.response.setStatus(200);
        Assertions.assertEquals(200, this.response.getStatus());
    }

    /**
     * Test get status string for 401.
     */
    @Test
    void testGetStatusStringUnauthorized() {
        this.response.setStatus(401);
        Assertions.assertEquals("Unauthorized", this.response.getStatusString());
    }

    /**
     * Test get status string for unknown status.
     */
    @Test
    void testGetStatusStringUnknown() {
        this.response.setStatus(200);
        Assertions.assertEquals("Unknown", this.response.getStatusString());
    }

    /**
     * Test add header and get header values.
     */
    @Test
    void testAddHeaderAndGetHeaderValues() {
        this.response.addHeader("X-Custom", "value1");
        this.response.addHeader("X-Custom", "value2");
        final String[] values = this.response.getHeaderValues("X-Custom");
        Assertions.assertNotNull(values);
        Assertions.assertEquals(2, values.length);
    }

    /**
     * Test get header values returns null for missing header.
     */
    @Test
    void testGetHeaderValuesNullForMissing() {
        Assertions.assertNull(this.response.getHeaderValues("X-Missing"));
    }

    /**
     * Test set header replaces value.
     */
    @Test
    void testSetHeaderReplacesValue() {
        this.response.addHeader("X-Single", "old");
        this.response.setHeader("X-Single", "new");
        Assertions.assertEquals("new", this.response.getHeader("X-Single"));
    }

    /**
     * Test get header returns null for missing.
     */
    @Test
    void testGetHeaderNullForMissing() {
        Assertions.assertNull(this.response.getHeader("X-NotSet"));
    }

    /**
     * Test get writer.
     */
    @Test
    void testGetWriter() {
        final PrintWriter writer = this.response.getWriter();
        Assertions.assertNotNull(writer);
        writer.print("hello");
        final String text = this.response.getOutputText();
        Assertions.assertEquals("hello", text);
    }

    /**
     * Test get output stream.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testGetOutputStream() throws IOException {
        final ServletOutputStream out = this.response.getOutputStream();
        Assertions.assertNotNull(out);
        out.write('A');
        Assertions.assertTrue(this.response.getOutputText().contains("A"));
    }

    /**
     * Test get output text initially empty.
     */
    @Test
    void testGetOutputTextEmpty() {
        Assertions.assertEquals("", this.response.getOutputText());
    }

    /**
     * Test flush buffer does not throw.
     */
    @Test
    void testFlushBufferDoesNotThrow() {
        this.response.sendError(401);
        Assertions.assertDoesNotThrow(() -> this.response.flushBuffer());
    }

    /**
     * Test header names size.
     */
    @Test
    void testGetHeaderNamesSize() {
        Assertions.assertEquals(0, this.response.getHeaderNamesSize());
        this.response.addHeader("X-One", "v1");
        this.response.addHeader("X-Two", "v2");
        Assertions.assertEquals(2, this.response.getHeaderNamesSize());
    }
}
