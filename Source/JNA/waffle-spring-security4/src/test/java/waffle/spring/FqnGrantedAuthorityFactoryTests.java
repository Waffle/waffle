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
package waffle.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import waffle.mock.MockWindowsAccount;
import waffle.windows.auth.WindowsAccount;

/**
 * The Class FqnGrantedAuthorityFactoryTests.
 */
public class FqnGrantedAuthorityFactoryTests {

    /** The group. */
    private WindowsAccount group;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        this.group = new WindowsAccount(new MockWindowsAccount("group"));
    }

    /**
     * Test prefix and uppercase.
     */
    @Test
    void testPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", true);
        Assertions.assertEquals(new SimpleGrantedAuthority("PREFIX_GROUP"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test prefix and lowercase.
     */
    @Test
    void testPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory("prefix_", false);
        Assertions.assertEquals(new SimpleGrantedAuthority("prefix_group"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test no prefix and uppercase.
     */
    @Test
    void testNoPrefixAndUppercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, true);
        Assertions.assertEquals(new SimpleGrantedAuthority("GROUP"), factory.createGrantedAuthority(this.group));
    }

    /**
     * Test no prefix and lowercase.
     */
    @Test
    void testNoPrefixAndLowercase() {
        final FqnGrantedAuthorityFactory factory = new FqnGrantedAuthorityFactory(null, false);
        Assertions.assertEquals(new SimpleGrantedAuthority("group"), factory.createGrantedAuthority(this.group));
    }

}
