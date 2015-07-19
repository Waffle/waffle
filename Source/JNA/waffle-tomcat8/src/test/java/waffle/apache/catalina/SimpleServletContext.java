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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.mockito.Mockito;

/**
 * Simple Servlet Context.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimpleServletContext implements ServletContext {

    /**
     * Get Request Dispatcher used by Waffle.
     *
     * @param url the url
     * @return the request dispatcher
     */
    @Override
    public RequestDispatcher getRequestDispatcher(final String url) {
        final SimpleRequestDispatcher dispatcher = Mockito.mock(SimpleRequestDispatcher.class,
                Mockito.CALLS_REAL_METHODS);
        dispatcher.setUrl(url);
        return dispatcher;
    }

}
