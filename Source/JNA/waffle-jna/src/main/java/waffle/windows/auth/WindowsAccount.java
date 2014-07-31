/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.windows.auth;

import java.io.Serializable;

/**
 * A flattened Windows Account used in a Windows principal.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    private String            sidString;
    private String            fqn;
    private String            name;
    private String            domain;

    public WindowsAccount(IWindowsAccount account) {
        this.sidString = account.getSidString();
        this.fqn = account.getFqn();
        this.name = account.getName();
        this.domain = account.getDomain();
    }

    public String getSidString() {
        return this.sidString;
    }

    public String getFqn() {
        return this.fqn;
    }

    public String getName() {
        return this.name;
    }

    public String getDomain() {
        return this.domain;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof WindowsAccount)) {
            return false;
        }

        return ((WindowsAccount) o).getSidString().equals(getSidString());
    }

    @Override
    public int hashCode() {
        return getSidString().hashCode();
    }
}
