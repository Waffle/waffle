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
package waffle.apache.catalina;

import org.apache.catalina.Container;
import org.apache.catalina.Engine;
import org.apache.catalina.Pipeline;

/**
 * Simple Engine.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimpleEngine implements Engine {

    private Pipeline pipeline;

    /**
     * Return Null Parent for Waffle Tests.
     */
    @Override
    public Container getParent() {
        return null;
    }

    /**
     * Get Pipeline Used By Waffle.
     */
    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * Set Pipeline Used By Waffle.
     * 
     * @param value
     */
    public void setPipeline(final Pipeline value) {
        this.pipeline = value;
    }

}
