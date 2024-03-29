/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
package waffle.apache;

import com.google.errorprone.annotations.InlineMe;

import java.security.Principal;

import org.apache.catalina.realm.RealmBase;

/**
 * A rudimentary Windows realm.
 */
public class WindowsRealm extends RealmBase {

    /**
     * Gets the name simple class name.
     *
     * @return a short name for this Realm implementation, for use in log messages.
     *
     * @deprecated This will be removed in Tomcat 9 onwards. Use {@link Class#getSimpleName()} instead.
     */
    @Deprecated
    @InlineMe(replacement = "WindowsRealm.class.getSimpleName()", imports = "waffle.apache.WindowsRealm")
    @Override
    protected final String getName() {
        return WindowsRealm.class.getSimpleName();
    }

    @Override
    protected String getPassword(final String value) {
        return null;
    }

    @Override
    protected Principal getPrincipal(final String value) {
        return null;
    }

}
