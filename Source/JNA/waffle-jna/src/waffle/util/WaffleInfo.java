/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
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

import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.jna.Platform;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Win32Exception;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsComputer;
import waffle.windows.auth.IWindowsDomain;
import waffle.windows.auth.impl.WindowsAccountImpl;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

/**
 * A Utility class to read system info to help troubleshoot WAFFLE system configuration.
 * 
 * This utility class collects system information and returns it as an XML document.
 * 
 * From the command line, you can write the info to stdout using:
 * <code>
 *   java -cp "jna.jar;waffle-core.jar;waffle-api.jar;platform.jar;guava-13.0.jar" waffle.util.WaffleInfo
 * </code>
 * 
 * To show this information in a browser, run:
 * <code>
 *   java -cp "..." waffle.util.WaffleInfo -show
 * </code>
 * 
 * To lookup account names and return any listed info, run:
 * <code>
 *   java -cp "..." waffle.util.WaffleInfo -lookup AccountName
 * </code>
 * 
 */
public class WaffleInfo {
  
  /**
   * Get a Document with basic system information
   * 
   * This uses the builtin javax.xml package even though the API is quite verbose
   * @throws ParserConfigurationException 
   */
  public Document getWaffleInfo() throws ParserConfigurationException {
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
    Document doc = docBuilder.newDocument();

    //create the root element and add it to the document
    Element root = doc.createElement("waffle");
    
    // Add Version Information as attributes
    String version = WaffleInfo.class.getPackage().getImplementationVersion();
    if (version!=null) {
      root.setAttribute("version", version);
    }
    version = Platform.class.getPackage().getImplementationVersion();
    if (version!=null) {
      root.setAttribute("jna", version);
    }
    version = WindowUtils.class.getPackage().getImplementationVersion();
    if (version!=null) {
      root.setAttribute("jna-platform", version);
    }
    
    doc.appendChild(root);
    root.appendChild(getAuthProviderInfo(doc));
    
    return doc;
  }
  
  protected Element getAuthProviderInfo(Document doc) {
    IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
    
    Element node = doc.createElement("auth");
    node.setAttribute("class", auth.getClass().getName() );

    // Current User
    Element child = doc.createElement("currentUser");
    node.appendChild(child);

    String currentUsername = WindowsAccountImpl.getCurrentUsername();
    addAccountInfo(doc,child,new WindowsAccountImpl(currentUsername));
      
    // Computer
    child = doc.createElement("computer");
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
    for (String s : c.getGroups()) {
      Element g = doc.createElement("group");
      g.setTextContent(s);
      value.appendChild(g);
    }
    child.appendChild(value);
      
    
    // Only Show Domains if we are in a Domain
    if (Netapi32Util.getJoinStatus() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName ) {
      child = doc.createElement("domains");
      node.appendChild(child);
      
      for (IWindowsDomain domain : auth.getDomains()) {
        Element d = doc.createElement("domain");
        node.appendChild(d);
        
        value = doc.createElement("FQN");
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
    return node;
  }
  
  protected void addAccountInfo(Document doc, Element node, IWindowsAccount account) {
    Element value = doc.createElement("Name");
    value.setTextContent(account.getName());
    node.appendChild(value);
    
    value = doc.createElement("FQN");
    value.setTextContent(account.getFqn());
    node.appendChild(value);

    value = doc.createElement("Domain");
    value.setTextContent(account.getDomain());
    node.appendChild(value);

    value = doc.createElement("SID");
    value.setTextContent(account.getSidString());
    node.appendChild(value);
  }
  
  public Element getLookupInfo(Document doc, String lookup) {
    IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
    Element node = doc.createElement("lookup");
    node.setAttribute("name", lookup );
    try {
      addAccountInfo(doc,node,auth.lookupAccount(lookup));
    }
    catch(Win32Exception ex) {      
      node.appendChild(getException(doc, ex));
    }
    return node;
  }
  
  public static Element getException(Document doc, Exception t) {
    Element node = doc.createElement("exception");
    node.setAttribute("class", t.getClass().getName() );
    
    Element value = doc.createElement("message");
    if (t.getMessage()!=null) {
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
  
  public static String toPrettyXML(Document doc) throws TransformerException {
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
    boolean show = false;
    List<String> lookup = new ArrayList<String>();
    if (args!=null) {
      for (int i=0; i<args.length; i++) {
        String arg = args[i];
        if ("-show".equals(arg)) {
          show = true;
        }
        else if (arg.equals("-lookup")) {
          lookup.add( args[++i] );
        }
        else {
          System.err.println("Unknown Argument: "+arg);
          System.exit(1);
        }
      }
    }
      
    WaffleInfo helper = new WaffleInfo();
    try {
      Document info = helper.getWaffleInfo();
      for (String name : lookup) {
        info.getDocumentElement().appendChild(helper.getLookupInfo(info, name));
      }
      
      String xml = toPrettyXML(info);
      if (show) {
	      File f = File.createTempFile("waffle-info-", ".xml");
	      Files.write(xml, f, Charsets.UTF_8);
	      Desktop.getDesktop().open(f);
      }
      else {
        System.out.println(xml);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
