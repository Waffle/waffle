/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.util;

import jakarta.servlet.http.HttpServletRequest;

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
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM token123";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("NTLM token123", header.getHeader());
    }

    /**
     * Test is null when header is absent.
     */
    @Test
    void testIsNullWhenAbsent() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNull());
    }

    /**
     * Test is null when header is empty string.
     */
    @Test
    void testIsNullWhenEmpty() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNull());
    }

    /**
     * Test is null returns false when header has a value.
     */
    @Test
    void testIsNullFalseWhenPresent() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNull());
    }

    /**
     * Test get security package for NTLM.
     */
    @Test
    void testGetSecurityPackageNtlm() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("NTLM", header.getSecurityPackage());
    }

    /**
     * Test get security package for Negotiate.
     */
    @Test
    void testGetSecurityPackageNegotiate() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("Negotiate", header.getSecurityPackage());
    }

    /**
     * Test get security package for Bearer.
     */
    @Test
    void testGetSecurityPackageBearer() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Bearer sometoken";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("Bearer", header.getSecurityPackage());
    }

    /**
     * Test get security package throws when header is null.
     */
    @Test
    void testGetSecurityPackageThrowsWhenNull() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertThrows(RuntimeException.class, header::getSecurityPackage);
    }

    /**
     * Test get security package throws when no space in header.
     */
    @Test
    void testGetSecurityPackageThrowsWhenNoSpace() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NoSpaceToken";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertThrows(RuntimeException.class, header::getSecurityPackage);
    }

    /**
     * Test to string when header is null.
     */
    @Test
    void testToStringWhenNull() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals("<none>", header.toString());
    }

    /**
     * Test to string when header is present.
     */
    @Test
    void testToStringWhenPresent() {
        final String headerValue = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = headerValue;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals(headerValue, header.toString());
    }

    /**
     * Test get token.
     */
    @Test
    void testGetToken() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertEquals(AuthorizationHeaderTest.NTLM_TYPE1_TOKEN, header.getToken());
    }

    /**
     * Test get token bytes decodes base64.
     */
    @Test
    void testGetTokenBytes() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
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
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM not-valid-base64!!!";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertThrows(RuntimeException.class, header::getTokenBytes);
    }

    /**
     * Test is NTLM type 1 message returns true for valid NTLM type 1.
     */
    @Test
    void testIsNtlmType1MessageTrue() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1Message());
    }

    /**
     * Test is NTLM type 1 message returns false when header is null.
     */
    @Test
    void testIsNtlmType1MessageFalseWhenNull() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1Message());
    }

    /**
     * Test is NTLM type 1 message returns false for SPNego token.
     */
    @Test
    void testIsNtlmType1MessageFalseForSpnego() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1Message());
    }

    /**
     * Test is SPNego NegTokenInit message returns true for valid SPNego token.
     */
    @Test
    void testIsSPNegTokenInitMessageTrue() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is SPNego NegTokenInit message returns false when header is null.
     */
    @Test
    void testIsSPNegTokenInitMessageFalseWhenNull() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is SPNego NegTokenInit message returns false for NTLM token.
     */
    @Test
    void testIsSPNegTokenInitMessageFalseForNtlm() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isSPNegTokenInitMessage());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns true for POST with NTLM and no content.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderPostNoContent() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "POST";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns true for PUT.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderPut() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "PUT";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns true for DELETE.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderDelete() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "DELETE";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns false for GET. Note: getHeader is never called for GET
     * since method check comes first.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderGetReturnsFalse() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "GET";
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                this.minTimes = 0;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST authorization header returns false when content length is non-zero. Note: getHeader is
     * never called since contentLength check comes before NTLM check.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderWithContentReturnsFalse() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "POST";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 100;
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
                this.minTimes = 0;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is NTLM type 1 POST returns true for SPNego POST with no content.
     */
    @Test
    void testIsNtlmType1PostAuthorizationHeaderSpnegoPost() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Negotiate " + AuthorizationHeaderTest.SPNEGO_TOKEN;
                AuthorizationHeaderTest.this.request.getMethod();
                this.result = "POST";
                AuthorizationHeaderTest.this.request.getContentLength();
                this.result = 0;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isNtlmType1PostAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns true.
     */
    @Test
    void testIsBearerAuthorizationHeaderTrue() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "Bearer sometoken";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns true for uppercase BEARER.
     */
    @Test
    void testIsBearerAuthorizationHeaderTrueUpperCase() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "BEARER sometoken";
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertTrue(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns false for NTLM.
     */
    @Test
    void testIsBearerAuthorizationHeaderFalseForNtlm() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = "NTLM " + AuthorizationHeaderTest.NTLM_TYPE1_TOKEN;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isBearerAuthorizationHeader());
    }

    /**
     * Test is bearer authorization header returns false when null.
     */
    @Test
    void testIsBearerAuthorizationHeaderFalseWhenNull() {
        Assertions.assertNotNull(new Expectations() {
            {
                AuthorizationHeaderTest.this.request.getHeader("Authorization");
                this.result = null;
            }
        });
        final AuthorizationHeader header = new AuthorizationHeader(this.request);
        Assertions.assertFalse(header.isBearerAuthorizationHeader());
    }

}
