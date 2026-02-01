/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock;

import waffle.windows.auth.IWindowsImpersonationContext;

/**
 * The Class MockWindowsImpersonationContext.
 */
public class MockWindowsImpersonationContext implements IWindowsImpersonationContext {

    @Override
    public void revertToSelf() {
        // Do Nothing
    }

}
