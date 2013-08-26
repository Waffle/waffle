/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package compressionFilters;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Very Simple test servlet to test compression filter
 * @author Amy Roh
 * @version $Id: CompressionFilterTestServlet.java 939521 2010-04-30 00:16:33Z kkolinko $
 */

public class CompressionFilterTestServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        ServletOutputStream out = response.getOutputStream();
        response.setContentType("text/plain");

        Enumeration e = ((HttpServletRequest)request).getHeaders("Accept-Encoding");
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            out.println(name);
            if (name.indexOf("gzip") != -1) {
                out.println("gzip supported -- able to compress");
            }
            else {
                out.println("gzip not supported");
            }
        }


        out.println("Compression Filter Test Servlet");
        out.close();
    }

}

