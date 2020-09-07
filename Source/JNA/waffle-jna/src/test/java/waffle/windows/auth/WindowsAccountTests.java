/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.windows.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
    void testGetCurrentUsername() {
        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        WindowsAccountTests.LOGGER.info("Current username: {}", currentUsername);
        assertThat(currentUsername.length()).isGreaterThan(0);
    }

    /**
     * Test get current account.
     */
    @Test
    void testGetCurrentAccount() {
        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        final IWindowsAccount account = new WindowsAccountImpl(currentUsername);
        assertThat(account.getName().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Name: {}", account.getName());
        assertThat(account.getDomain().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Domain: {}", account.getDomain());
        assertThat(account.getFqn().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Fqn: {}", account.getFqn());
        assertThat(account.getSidString().length()).isGreaterThan(0);
        WindowsAccountTests.LOGGER.info("Sid: {}", account.getSidString());
        // To avoid errors with machine naming being all upper-case, use test in this manner
        Assertions.assertTrue(currentUsername.equalsIgnoreCase(account.getFqn()));
        Assertions.assertTrue(currentUsername.endsWith("\\" + account.getName()));
        // To avoid errors with machine naming being all upper-case, use test in this manner
        Assertions.assertTrue(currentUsername.toLowerCase().startsWith(account.getDomain().toLowerCase() + "\\"));
    }
}
