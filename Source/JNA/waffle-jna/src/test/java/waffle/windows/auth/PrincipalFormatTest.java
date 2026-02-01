/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The Class PrincipalFormatTest.
 */
class PrincipalFormatTest {

    /**
     * Test known.
     */
    @Test
    void testKnown() {
        Assertions.assertEquals(PrincipalFormat.FQN, PrincipalFormat.valueOf("FQN"));
        Assertions.assertEquals(PrincipalFormat.SID, PrincipalFormat.valueOf("SID"));
        Assertions.assertEquals(PrincipalFormat.BOTH, PrincipalFormat.valueOf("BOTH"));
        Assertions.assertEquals(PrincipalFormat.NONE, PrincipalFormat.valueOf("NONE"));
        Assertions.assertEquals(4, PrincipalFormat.values().length);
    }

    /**
     * Test unknown.
     */
    @Test
    void testUnknown() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            PrincipalFormat.valueOf("garbage");
        });
    }

}
