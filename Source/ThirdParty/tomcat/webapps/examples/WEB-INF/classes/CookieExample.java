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
/* $Id: CookieExample.java 500674 2007-01-27 23:15:00Z markt $
 *
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import util.HTMLFilter;

/**
 * Example servlet showing request headers
 *
 * @author James Duncan Davidson <duncan@eng.sun.com>
 */

public class CookieExample extends HttpServlet {

    ResourceBundle rb = ResourceBundle.getBundle("LocalStrings");
    
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body bgcolor=\"white\">");
        out.println("<head>");

        String title = rb.getString("cookies.title");
        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body>");

	// relative links

        // XXX
        // making these absolute till we work out the
        // addition of a PathInfo issue 
	
        out.println("<a href=\"../cookies.html\">");
        out.println("<img src=\"../images/code.gif\" height=24 " +
                    "width=24 align=right border=0 alt=\"view code\"></a>");
        out.println("<a href=\"../index.html\">");
        out.println("<img src=\"../images/return.gif\" height=24 " +
                    "width=24 align=right border=0 alt=\"return\"></a>");

        out.println("<h3>" + title + "</h3>");

        Cookie[] cookies = request.getCookies();
        if ((cookies != null) && (cookies.length > 0)) {
            out.println(rb.getString("cookies.cookies") + "<br>");
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                out.print("Cookie Name: " + HTMLFilter.filter(cookie.getName())
                          + "<br>");
                out.println("  Cookie Value: " 
                            + HTMLFilter.filter(cookie.getValue())
                            + "<br><br>");
            }
        } else {
            out.println(rb.getString("cookies.no-cookies"));
        }

        String cookieName = request.getParameter("cookiename");
        String cookieValue = request.getParameter("cookievalue");
        if (cookieName != null && cookieValue != null) {
            Cookie cookie = new Cookie(cookieName, cookieValue);
            response.addCookie(cookie);
            out.println("<P>");
            out.println(rb.getString("cookies.set") + "<br>");
            out.print(rb.getString("cookies.name") + "  " 
                      + HTMLFilter.filter(cookieName) + "<br>");
            out.print(rb.getString("cookies.value") + "  " 
                      + HTMLFilter.filter(cookieValue));
        }
        
        out.println("<P>");
        out.println(rb.getString("cookies.make-cookie") + "<br>");
        out.print("<form action=\"");
        out.println("CookieExample\" method=POST>");
        out.print(rb.getString("cookies.name") + "  ");
        out.println("<input type=text length=20 name=cookiename><br>");
        out.print(rb.getString("cookies.value") + "  ");
        out.println("<input type=text length=20 name=cookievalue><br>");
        out.println("<input type=submit></form>");
            
            
        out.println("</body>");
        out.println("</html>");
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }

}


