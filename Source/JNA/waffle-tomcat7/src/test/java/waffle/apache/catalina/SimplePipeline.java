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

import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;

/**
 * Simple Pipeline.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimplePipeline implements Pipeline {

    private Valve[] valves;

    @Override
    public Valve[] getValves() {
        return this.valves.clone();
    }

    public void setValves(final Valve[] value) {
        this.valves = value;
    }

}
