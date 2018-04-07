/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth;

/**
 * A Windows Computer.
 *
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsComputer {

    /**
     * Computer name.
     *
     * @return String.
     */
    String getComputerName();

    /**
     * Member of (domain).
     *
     * @return String.
     */
    String getMemberOf();

    /**
     * Join status.
     *
     * @return String.
     */
    String getJoinStatus();

    /**
     * Groups.
     *
     * @return Array of group names.
     */
    String[] getGroups();
}
