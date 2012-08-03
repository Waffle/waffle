<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page session="false" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
    <title><%= application.getServerInfo() %></title>
    <style type="text/css">
    /*<![CDATA[*/
      body {
          color: #000000;
          background-color: #FFFFFF;
	  font-family: Arial, "Times New Roman", Times, serif;
          margin: 10px 0px;
      }

    img {
       border: none;
    }
    
    a:link, a:visited {
        color: blue
    }

    th {
        font-family: Verdana, "Times New Roman", Times, serif;
        font-size: 110%;
        font-weight: normal;
        font-style: italic;
        background: #D2A41C;
        text-align: left;
    }

    td {
        color: #000000;
	font-family: Arial, Helvetica, sans-serif;
    }
    
    td.menu {
        background: #FFDC75;
    }

    .center {
        text-align: center;
    }

    .code {
        color: #000000;
        font-family: "Courier New", Courier, monospace;
        font-size: 110%;
        margin-left: 2.5em;
    }
    
     #banner {
        margin-bottom: 12px;
     }

     p#congrats {
         margin-top: 0;
         font-weight: bold;
         text-align: center;
     }

     p#footer {
         text-align: right;
         font-size: 80%;
     }
     /*]]>*/
   </style>
</head>

<body>

<!-- Header -->
<table id="banner" width="100%">
    <tr>
      <td align="left" style="width:130px">
        <a href="http://tomcat.apache.org/">
	  <img src="tomcat.gif" height="92" width="130" alt="The Mighty Tomcat - MEOW!"/>
	</a>
      </td>
      <td align="left" valign="top"><b><%= application.getServerInfo() %></b></td>
      <td align="right">
        <a href="http://www.apache.org/">
	  <img src="asf-logo-wide.gif" height="51" width="537" alt="The Apache Software Foundation"/>
	</a>
       </td>
     </tr>
</table>

<table>
    <tr>

        <!-- Table of Contents -->
        <td valign="top">
            <table width="100%" border="1" cellspacing="0" cellpadding="3">
                <tr>
		  <th>Administration</th>
                </tr>
                <tr>
		  <td class="menu">
		    <a href="manager/status">Status</a><br/>
                    <a href="admin">Tomcat&nbsp;Administration</a><br/>
                    <a href="manager/html">Tomcat&nbsp;Manager</a><br/>
                    &nbsp;
                  </td>
                </tr>
            </table>

	    <br />
            <table width="100%" border="1" cellspacing="0" cellpadding="3">
                <tr>
		  <th>Documentation</th>
                </tr>
                <tr>
                  <td class="menu">
                    <a href="RELEASE-NOTES.txt">Release&nbsp;Notes</a><br/>
                    <a href="tomcat-docs/changelog.html">Change&nbsp;Log</a><br/>
                    <a href="tomcat-docs">Tomcat&nbsp;Documentation</a><br/>                        &nbsp;
                    &nbsp;
		    </td>
                </tr>
            </table>
	    
            <br/>
            <table width="100%" border="1" cellspacing="0" cellpadding="3">
                <tr>
                  <th>Tomcat Online</th>
                </tr>
                <tr>
                  <td class="menu">
                    <a href="http://tomcat.apache.org/">Home&nbsp;Page</a><br/>
		    <a href="http://tomcat.apache.org/faq/">FAQ</a><br/>
                    <a href="http://tomcat.apache.org/bugreport.html">Bug&nbsp;Database</a><br/>
                    <a href="http://mail-archives.apache.org/mod_mbox/tomcat-users/">Users&nbsp;Mailing&nbsp;List</a><br/>
                    <a href="http://mail-archives.apache.org/mod_mbox/tomcat-dev/">Developers&nbsp;Mailing&nbsp;List</a><br/>
                    <a href="irc://irc.freenode.net/#tomcat">IRC</a><br/>
		    &nbsp;
                  </td>
                </tr>
            </table>
	    
            <br/>
            <table width="100%" border="1" cellspacing="0" cellpadding="3">
                <tr>
                  <th>Examples</th>
                </tr>
                <tr>
                  <td class="menu">
                    <a href="jsp-examples/">JSP&nbsp;Examples</a><br/>
                    <a href="servlets-examples/">Servlet&nbsp;Examples</a><br/>
                    <a href="webdav/">WebDAV&nbsp;capabilities</a><br/>
     		    &nbsp;
                  </td>
                </tr>
            </table>
	    
            <br/>
            <table width="100%" border="1" cellspacing="0" cellpadding="3">
                <tr>
		  <th>Miscellaneous</th>
                </tr>
                <tr>
                  <td class="menu">
                    <a href="http://java.sun.com/products/jsp">Sun's&nbsp;Java&nbsp;Server&nbsp;Pages&nbsp;Site</a><br/>
                    <a href="http://java.sun.com/products/servlet">Sun's&nbsp;Servlet&nbsp;Site</a><br/>
    		    &nbsp;
                  </td>
                </tr>
            </table>
        </td>

        <td style="width:20px">&nbsp;</td>
	
        <!-- Body -->
        <td align="left" valign="top">
          <p id="congrats">If you're seeing this page via a web browser, it means you've setup Tomcat successfully. Congratulations!</p>
 
          <p>As you may have guessed by now, this is the default Tomcat home page. It can be found on the local filesystem at:</p>
          <p class="code">$CATALINA_HOME/webapps/ROOT/index.jsp</p>
	  
          <p>where "$CATALINA_HOME" is the root of the Tomcat installation directory. If you're seeing this page, and you don't think you should be, then either you're either a user who has arrived at new installation of Tomcat, or you're an administrator who hasn't got his/her setup quite right. Providing the latter is the case, please refer to the <a href="tomcat-docs">Tomcat Documentation</a> for more detailed setup and administration information than is found in the INSTALL file.</p>

            <p><b>NOTE:</b> This page is precompiled. If you change it, this page will not change since
                  it was compiled into a servlet at build time.
                  (See <tt>$CATALINA_HOME/webapps/ROOT/WEB-INF/web.xml</tt> as to how it was mapped.)
            </p>

            <p><b>NOTE: For security reasons, using the administration webapp
            is restricted to users with role "admin". The manager webapp
            is restricted to users with role "manager".</b>
            Users are defined in <code>$CATALINA_HOME/conf/tomcat-users.xml</code>.</p>

            <p>Included with this release are a host of sample Servlets and JSPs (with associated source code), extensive documentation (including the Servlet 2.4 and JSP 2.0 API JavaDoc), and an introductory guide to developing web applications.</p>

            <p>Tomcat mailing lists are available at the Tomcat project web site:</p>

           <ul>
               <li><b><a href="mailto:users@tomcat.apache.org">users@tomcat.apache.org</a></b> for general questions related to configuring and using Tomcat</li>
               <li><b><a href="mailto:dev@tomcat.apache.org">dev@tomcat.apache.org</a></b> for developers working on Tomcat</li>
           </ul>

            <p>Thanks for using Tomcat!</p>

            <p id="footer"><img src="tomcat-power.gif" width="77" height="80" alt="Powered by Tomcat"/><br/>
	    &nbsp;

	    Copyright &copy; 1999-2012 Apache Software Foundation<br/>
            All Rights Reserved
            </p>
        </td>

    </tr>
</table>

</body>
</html>
