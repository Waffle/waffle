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
 * A Windows Domain.
 *
 * @author dblock[at]dblock[dot]org
 */
public interface IWindowsDomain {

    /**
     * Fully qualified domain name.
     *
     * @return String.
     */
    String getFqn();

    /**
     * Trust direction.
     *
     * @return String.
     */
    String getTrustDirectionString();

    /**
     * Trust type.
     *
     * @return String.
     */
    String getTrustTypeString();
}
