/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;

/**
 * The Class WindowsCredentialsHandleTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsCredentialsHandleTests {

    /**
     * Test get current.
     */
    @Test
    public void testGetCurrent() {
        final IWindowsCredentialsHandle handle = WindowsCredentialsHandleImpl.getCurrent("Negotiate");
        Assertions.assertNotNull(handle);
        handle.initialize();
        handle.dispose();
    }
}
