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
package waffle.apache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Windows Realm Tests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsRealmTests {

    /**
     * Test properties.
     */
    @Test
    public void testProperties() {
        final WindowsRealm realm = new WindowsRealm();
        Assertions.assertNull(realm.getPassword(null));
        Assertions.assertNull(realm.getPrincipal(null));
        // To support tomcat 9.0.0.M13 and lower this will still work
        Assertions.assertEquals("waffle.apache.WindowsRealm/1.0", realm.getName());
        // In tomcat 9.0.0.M15+, this is all that will now report
        Assertions.assertEquals("WindowsRealm", realm.getClass().getSimpleName());
    }
}
