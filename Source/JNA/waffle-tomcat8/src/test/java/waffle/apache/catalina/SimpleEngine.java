/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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

    /** The pipeline. */
    private Pipeline pipeline;

    /**
     * Return Null Parent for Waffle Tests.
     *
     * @return the parent
     */
    @Override
    public Container getParent() {
        return null;
    }

    /**
     * Get Pipeline Used By Waffle.
     *
     * @return the pipeline
     */
    @Override
    public Pipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * Set Pipeline Used By Waffle.
     *
     * @param value the new pipeline
     */
    public void setPipeline(final Pipeline value) {
        this.pipeline = value;
    }

}
