/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
* 
* Copyright (c) 2010 Application Security, Inc.
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Application Security, Inc.
*******************************************************************************/
package waffle.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Utility class to read system info to help troubleshoot WAFFLE system configuration
 */
public class WaffleInfo {
  static final Logger log = LoggerFactory.getLogger(WaffleInfo.class);

  /**
   * @return The WAFFLE Implementation Version
   */
  public static String getVersion() {
    return WaffleInfo.class.getPackage().getImplementationVersion();
  }
  
  /**
   * Get a Document with basic system information
   * 
   * This uses the builtin javax.xml package even though the API is quite verbose
   */
  public Document getSystemInfo() throws Exception {
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
    Document doc = docBuilder.newDocument();

    //create the root element and add it to the document
    Element root = doc.createElement("waffle");
    root.setAttribute("version", getVersion());
    doc.appendChild(root);
    
    root.appendChild( getAuthProviderInfo(doc) );
    
    return doc;
  }
  
  
  private Element getAuthProviderInfo(Document doc) {
    IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
    
    Element node = doc.createElement("auth");
    node.setAttribute("class", auth.getClass().getName() );
    Element wrap = node;

    // Current User
    try {
      Element child = wrap = doc.createElement("currentUser");
      node.appendChild(child);

      String currentUsername = WindowsAccountImpl.getCurrentUsername();
      IWindowsAccount account = new WindowsAccountImpl(currentUsername);

      Element value = doc.createElement("Name");
      value.setTextContent(account.getName());
      child.appendChild(value);
      
      value = doc.createElement("FQN");
      value.setTextContent(account.getFqn());
      child.appendChild(value);

      value = doc.createElement("Domain");
      value.setTextContent(account.getDomain());
      child.appendChild(value);

      value = doc.createElement("SID");
      value.setTextContent(account.getSidString());
      child.appendChild(value);
    }
    catch(Throwable ex) {      
      wrap.appendChild(getException(doc, ex));
    }
    
    // Computer
    try {
      Element child = wrap = doc.createElement("computer");
      node.appendChild(child);
      
      IWindowsComputer c = auth.getCurrentComputer();
      Element value = doc.createElement("computerName");
      value.setTextContent(c.getComputerName());
      child.appendChild(value);
      
      value = doc.createElement("memberOf");
      value.setTextContent(c.getMemberOf());
      child.appendChild(value);

      value = doc.createElement("joinStatus");
      value.setTextContent(c.getJoinStatus());
      child.appendChild(value);

      value = doc.createElement("groups");
      for(String s : c.getGroups()) {
        Element g = doc.createElement("group");
        g.setTextContent(s);
        value.appendChild(g);
      }
      child.appendChild(value);
    }
    catch(Throwable ex) {
      wrap.appendChild(getException(doc, ex));
    }
    
    // Domains
    // TODO? skip if the computer is in a workgroup?
    try {
      Element child = wrap = doc.createElement("domains");
      node.appendChild(child);
      
      for(IWindowsDomain domain : auth.getDomains()) {
        Element d = doc.createElement("domain");
        node.appendChild(d);
        
        Element value = doc.createElement("FQN");
        value.setTextContent(domain.getFqn());
        child.appendChild(value);

        value = doc.createElement("TrustTypeString");
        value.setTextContent(domain.getTrustTypeString());
        child.appendChild(value);

        value = doc.createElement("TrustDirectionString");
        value.setTextContent(domain.getTrustDirectionString());
        child.appendChild(value);
      }
    }
    catch(Throwable ex) {      
      wrap.appendChild(getException(doc, ex));
    }
    return node;
  }
  
  public static Element getException(Document doc, Throwable t) {
    Element node = doc.createElement("exception");
    node.setAttribute("class", t.getClass().getName() );
    
    Element value = doc.createElement("message");
    if(t.getMessage()!=null) {
      value.setTextContent(t.getMessage());
      node.appendChild(value);
    }
    
    value = doc.createElement("trace");
    Writer result = new StringWriter();
    PrintWriter printWriter = new PrintWriter(result);
    t.printStackTrace(printWriter);
    value.setTextContent(result.toString());
    node.appendChild(value);
    return node;
  }
  
  public static String toPrettyXML(Document doc) throws Exception {
    //set up a transformer
    TransformerFactory transfac = TransformerFactory.newInstance();
    Transformer trans = transfac.newTransformer();
    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    trans.setOutputProperty(OutputKeys.INDENT, "yes");

    //create string from xml tree
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    DOMSource source = new DOMSource(doc);
    trans.transform(source, result);
    return sw.toString();
  }
  
  /**
   * Print system information
   */
  public static void main(String[] args) {
    WaffleInfo helper = new WaffleInfo();
    try {
      Document info = helper.getSystemInfo();
      System.out.println(toPrettyXML(info));
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
