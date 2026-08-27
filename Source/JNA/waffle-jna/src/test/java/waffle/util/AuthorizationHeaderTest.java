/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AuthorizationHeader}.
 */
class AuthorizationHeaderTest {

    /** Valid NTLM Type 1 token (base64-encoded). */
    private static final String NTLM_TYPE1_TOKEN = "TlRMTVNTUAABAAAABzIAAAYABgArAAAACwALACAAAABXT1JLU1RBVElPTkRPTUFJTg==";

    /** Valid SPNego NegTokenInit token (base64-encoded). */
    private static final String SPNEGO_TOKEN = "YHYGBisGAQUFAqBsMGqgMDAuBgorBgEEAYI3AgIKBgkqhkiC9xIBAgIGCSqGSIb3EgECAgYKKwYBBAGCNwICHqI2BDROVExNU1NQAAEAAACXsgjiAwADADEAAAAJAAkAKAAAAAYBsR0AAAAPR0xZQ0VSSU5FU0FE";

    /** The mocked HTTP servlet request. */
    @Mocked
    private HttpServletRequest request;

    /**
     * Test get header returns value from request.
     */
    @Test
    void testGetHeader() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM token123";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("NTLM token123", header.getHeader());
    }

    /**
     * Test is null when header is absent.
     */
    @Test
    void testIsNullWhenAbsent() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNull());
    }

    /**
     * Test is null when header is empty string.
     */
    @Test
    void testIsNullWhenEmpty() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNull());
    }

    /**
     * Test is null returns false when header has a value.
     */
    @Test
    void testIsNullFalseWhenPresent() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNull());
    }

    /**
     * Test get security package for NTLM.
     */
    @Test
    void testGetSecurityPackageNtlm() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("NTLM", header.getSecurityPackage());
    }

    /**
     * Test get security package for Negotiate.
     */
    @Test
    void testGetSecurityPackageNegotiate() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("Negotiate", header.getSecurityPackage());
    }

    /**
     * Test get security package for Bearer.
     */
    @Test
    void testGetSecurityPackageBearer() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Bearer sometoken";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("Bearer", header.getSecurityPackage());
    }

    /**
     * Test get security package throws when header is null.
     */
    @Test
    void testGetSecurityPackageThrowsWhenNull() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertThrows(RuntimeException.class, header::getSecurityPackage);
    }

    /**
     * Test get security package throws when no space in header.
     */
    @Test
    void testGetSecurityPackageThrowsWhenNoSpace() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NoSpaceToken";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertThrows(RuntimeException.class, header::getSecurityPackage);
    }

    /**
     * Test to string when header is null.
     */
    @Test
    void testToStringWhenNull() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("<none>", header.toString());
    }

    /**
     * Test to string when header is present.
     */
    @Test
    void testToStringWhenPresent() {
        final String headerValue = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = headerValue;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals(headerValue, header.toString());
    }

    /**
     * Test get token.
     */
    @Test
    void testGetToken() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals(AuthorizationHeaderTest.NTLM_TYPE1_TOKEN, header.getToken());
    }

    /**
     * Test get token bytes decodes base64.
     */
    @Test
    void testGetTokenBytes() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        final byte[] bytes = header.getTokenBytes();
        Assertions.assertNotNull(bytes);
        Assertions.assertTrue(bytes.length > 0);
    }

    /**
     * Test get token bytes throws on invalid base64.
     */
    @Test
    void testGetTokenBytesThrowsOnInvalidBase64() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM not-valid-base64!!!";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertThrows(RuntimeException.class, header::getTokenBytes);
    }

    /**
     * Test is NTLM type 1 message returns true for valid NTLM type 1.
     */
    @Test
    void testIsNtlmType1MessageTrue() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1Message());
    }

    /**
     * Test is NTLM type 1 message returns false when header is null.
     */
    @Test
    void testIsNtlmType1MessageFalseWhenNull() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1Message());
    }

    /**
     * Test is NTLM type 1 message returns false for SPNego token.
     */
    @Test
    void testIsNtlmType1MessageFalseForSpnego() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1Message());
    }

    /**
     * Test is SPNego NegTokenInit message returns true for valid SPNego token.
     */
    @Test
    void testIsSPNegTokenInitMessageTrue() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is SPNego NegTokenInit message returns false when header is null.
     */
    @Test
    void testIsSPNegTokenInitMessageFalseWhenNull() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is SPNego NegTokenInit message returns false for NTLM token.
     */
    @Test
    void testIsSPNegTokenInitMessageFalseForNtlm() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns true for POST with NTLM and no content.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderPostNoContent() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "POST";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns true for PUT.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderPut() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "PUT";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns true for DELETE.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderDelete() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "DELETE";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns false for GET. Note: getHeader is never called for GET
     * since method check comes first.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderGetReturnsFalse() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "GET";
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                this.minTimes = 0;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns false when content length is non-zero. Note: getHeader is
     * never called since contentLength check comes before NTLM check.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderWithContentReturnsFalse() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "POST";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 100;
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                this.minTimes = 0;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST returns true for SPNego POST with no content.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderSpnegoPost() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "POST";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns true.
     */
    @Test
    void testIsBearerAuthorizationHeaderTrue() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Bearer sometoken";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns true for uppercase BEARER.
     */
    @Test
    void testIsBearerAuthorizationHeaderTrueUpperCase() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "BEARER sometoken";
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns false for NTLM.
     */
    @Test
    void testIsBearerAuthorizationHeaderFalseForNtlm() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns false when null.
     */
    @Test
    void testIsBearerAuthorizationHeaderFalseWhenNull() {
        new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        };
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isBearerAuthorizationHeader());
    }

}
