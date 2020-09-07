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
package waffle.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * Role principal.
 *
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipal implements Principal, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fqn. */
    private final String fqn;

    /**
     * A windows principal.
     *
     * @param newFqn
     *            Fully qualified name.
     */
    public RolePrincipal(final String newFqn) {
        this.fqn = newFqn;
    }

    /**
     * Role name (Windows Group).
     *
     * @return the name
     */
    @Override
    public String getName() {
        return this.fqn;
    }

    /**
     * Role Principal Equals for FQN.
     *
     * @param o
     *            Object used for Equality Check.
     * @return true, if successful
     */
    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof RolePrincipal) {
            return this.getName().equals(((RolePrincipal) o).getName());
        }

        return false;
    }

    /**
     * Role Principal HashCode for FQN.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

}
