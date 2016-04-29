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

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class WindowsAccountTests.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccountTests {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsAccountTests.class);

    /**
     * Test get current username.
     */
    @Test
    public void testGetCurrentUsername() {
        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        WindowsAccountTests.LOGGER.info("Current username: {}", currentUsername);
        Assertions.assertThat(currentUsername.length()).isGreaterThan(0);
    }

    /**
     * Test get current account.
     */
    @Test
    public void testGetCurrentAccount() {
        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        final IWindowsAccount account = new WindowsAccountImpl(currentUsername);
        Assertions.assertThat(account.getName().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Name: {}", account.getName());
        Assertions.assertThat(account.getDomain().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Domain: {}", account.getDomain());
        Assertions.assertThat(account.getFqn().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Fqn: {}", account.getFqn());
        Assertions.assertThat(account.getSidString().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Sid: {}", account.getSidString());
        // To avoid errors with machine naming being all upper-case, use test in this manner
        Assert.assertTrue(currentUsername.equalsIgnoreCase(account.getFqn()));
        Assert.assertTrue(currentUsername.endsWith("\\" + account.getName()));
        // To avoid errors with machine naming being all upper-case, use test in this manner
        Assert.assertTrue(currentUsername.toLowerCase().startsWith(account.getDomain().toLowerCase() + "\\"));
    }
}
