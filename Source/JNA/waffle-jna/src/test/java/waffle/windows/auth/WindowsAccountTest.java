/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.windows.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.windows.auth.impl.WindowsAccountImpl;

/**
 * The Class WindowsAccountTest.
 */
class WindowsAccountTest {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsAccountTest.class);

    /**
     * Test get current username.
     */
    @Test
    void testGetCurrentUsername() {
        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        WindowsAccountTest.LOGGER.info("Current username: {}", currentUsername);
        assertThat(currentUsername).isNotEmpty();
    }

    /**
     * Test get current account.
     */
    @Test
    void testGetCurrentAccount() {
        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        final IWindowsAccount account = new WindowsAccountImpl(currentUsername);
        assertThat(account.getName()).isNotEmpty();
        WindowsAccountTest.LOGGER.info("Name: {}", account.getName());
        assertThat(account.getDomain()).isNotEmpty();
        WindowsAccountTest.LOGGER.info("Domain: {}", account.getDomain());
        assertThat(account.getFqn()).isNotEmpty();
        WindowsAccountTest.LOGGER.info("Fqn: {}", account.getFqn());
        assertThat(account.getSidString()).isNotEmpty();
        WindowsAccountTest.LOGGER.info("Sid: {}", account.getSidString());
        // To avoid errors with machine naming being all upper-case, use test in this manner
        Assertions.assertTrue(currentUsername.equalsIgnoreCase(account.getFqn()));
        Assertions.assertTrue(currentUsername.endsWith("\\" + account.getName()));
        // To avoid errors with machine naming being all upper-case, use test in this manner
        Assertions.assertTrue(currentUsername.toLowerCase(Locale.ENGLISH)
                .startsWith(account.getDomain().toLowerCase(Locale.ENGLISH) + "\\"));
    }
}
