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
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;

public class SimplePipeline implements Pipeline {

    private Valve[] valves = new Valve[0];

    @Override
    public void addValve(Valve arg0) {
        // Not Implemented
    }

    @Override
    public Valve getBasic() {
        // Not Implemented
        return null;
    }

    @Override
    public Container getContainer() {
        // Not Implemented
        return null;
    }

    @Override
    public Valve getFirst() {
        // Not Implemented
        return null;
    }

    @Override
    public Valve[] getValves() {
        return this.valves.clone();
    }

    @Override
    public boolean isAsyncSupported() {
        // Not Implemented
        return false;
    }

    @Override
    public void removeValve(Valve arg0) {
        // Not Implemented
    }

    @Override
    public void setBasic(Valve arg0) {
        // Not Implemented
    }

    @Override
    public void setContainer(Container arg0) {
        // Not Implemented
    }

}
