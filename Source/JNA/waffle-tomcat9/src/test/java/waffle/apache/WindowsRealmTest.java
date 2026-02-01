/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.apache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Windows Realm Test.
 */
class WindowsRealmTest {

    /**
     * Test properties.
     */
    @Test
    void testProperties() {
        final WindowsRealm realm = new WindowsRealm();
        Assertions.assertNull(realm.getPassword(null));
        Assertions.assertNull(realm.getPrincipal(null));
        Assertions.assertEquals("WindowsRealm", realm.getClass().getSimpleName());
    }

}
