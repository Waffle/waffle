/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth;

import org.junit.Assert;
import org.junit.Test;

/**
 * The Class PrincipalFormatTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class PrincipalFormatTests {

    /**
     * Test known.
     */
    @Test
    public void testKnown() {
        Assert.assertEquals(PrincipalFormat.FQN, PrincipalFormat.valueOf("FQN"));
        Assert.assertEquals(PrincipalFormat.SID, PrincipalFormat.valueOf("SID"));
        Assert.assertEquals(PrincipalFormat.BOTH, PrincipalFormat.valueOf("BOTH"));
        Assert.assertEquals(PrincipalFormat.NONE, PrincipalFormat.valueOf("NONE"));
        Assert.assertEquals(4, PrincipalFormat.values().length);
    }

    /**
     * Test unknown.
     */
    @Test(expected = RuntimeException.class)
    public void testUnknown() {
        PrincipalFormat.valueOf("garbage");
    }
}
