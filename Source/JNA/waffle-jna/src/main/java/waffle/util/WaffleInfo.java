/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.util;

import com.sun.jna.Platform;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.LMJoin;
import com.sun.jna.platform.win32.Netapi32Util;
import com.sun.jna.platform.win32.Win32Exception;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
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
 * A Utility class to read system info to help troubleshoot WAFFLE system configuration.
 *
 * <pre>
 * This utility class collects system information and returns it as an XML document.
 * </pre>
 *
 * From the command line, you can write the info to stdout using:
 *
 * <pre>
 * <code>
 *   java -cp "jna.jar;waffle-core.jar;waffle-api.jar;jna-platform.jar;guava-21.0.jar" waffle.util.WaffleInfo
 * </code>
 * </pre>
 *
 * To show this information in a browser, run:
 *
 * <pre>
 * <code>
 *   java -cp "..." waffle.util.WaffleInfo -show
 * </code>
 * </pre>
 *
 * To lookup account names and return any listed info, run:
 *
 * <pre>
 * <code>
 *   java -cp "..." waffle.util.WaffleInfo -lookup AccountName
 * </code>
 * </pre>
 *
 */
public class WaffleInfo {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WaffleInfo.class);

    /**
     * Get a Document with basic system information
     *
     * This uses the builtin javax.xml package even though the API is quite verbose
     *
     * @return Document with waffle info.
     *
     * @throws ParserConfigurationException
     *             when getting new document builder.
     */
    public Document getWaffleInfo() throws ParserConfigurationException {
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        // create the root element and add it to the document
        final Element root = doc.createElement("waffle");

        // Add Version Information as attributes
        String version = WaffleInfo.class.getPackage().getImplementationVersion();
        if (version != null) {
            root.setAttribute("version", version);
        }
        version = Platform.class.getPackage().getImplementationVersion();
        if (version != null) {
            root.setAttribute("jna", version);
        }
        version = WindowUtils.class.getPackage().getImplementationVersion();
        if (version != null) {
            root.setAttribute("jna-platform", version);
        }

        doc.appendChild(root);
        root.appendChild(this.getAuthProviderInfo(doc));

        return doc;
    }

    /**
     * Gets the auth provider info.
     *
     * @param doc
     *            the doc
     * @return the auth provider info
     */
    protected Element getAuthProviderInfo(final Document doc) {
        final IWindowsAuthProvider auth = new WindowsAuthProviderImpl();

        final Element node = doc.createElement("auth");
        node.setAttribute("class", auth.getClass().getName());

        // Current User
        Element child = doc.createElement("currentUser");
        node.appendChild(child);

        final String currentUsername = WindowsAccountImpl.getCurrentUsername();
        this.addAccountInfo(doc, child, new WindowsAccountImpl(currentUsername));

        // Computer
        child = doc.createElement("computer");
        node.appendChild(child);

        final IWindowsComputer c = auth.getCurrentComputer();
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
        Element g;
        for (final String s : c.getGroups()) {
            g = doc.createElement("group");
            g.setTextContent(s);
            value.appendChild(g);
        }
        child.appendChild(value);

        // Only Show Domains if we are in a Domain
        if (Netapi32Util.getJoinStatus() == LMJoin.NETSETUP_JOIN_STATUS.NetSetupDomainName) {
            child = doc.createElement("domains");
            node.appendChild(child);

            Element d;
            for (final IWindowsDomain domain : auth.getDomains()) {
                d = doc.createElement("domain");
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

    /**
     * Adds the account info.
     *
     * @param doc
     *            the doc
     * @param node
     *            the node
     * @param account
     *            the account
     */
    protected void addAccountInfo(final Document doc, final Element node, final IWindowsAccount account) {
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

    /**
     * Gets the lookup info.
     *
     * @param doc
     *            the doc
     * @param lookup
     *            the lookup
     * @return the lookup info
     */
    public Element getLookupInfo(final Document doc, final String lookup) {
        final IWindowsAuthProvider auth = new WindowsAuthProviderImpl();
        final Element node = doc.createElement("lookup");
        node.setAttribute("name", lookup);
        try {
            this.addAccountInfo(doc, node, auth.lookupAccount(lookup));
        } catch (final Win32Exception e) {
            node.appendChild(WaffleInfo.getException(doc, e));
        }
        return node;
    }

    /**
     * Gets the exception.
     *
     * @param doc
     *            the doc
     * @param t
     *            the t
     * @return the exception
     */
    public static Element getException(final Document doc, final Exception t) {
        final Element node = doc.createElement("exception");
        node.setAttribute("class", t.getClass().getName());

        Element value = doc.createElement("message");
        if (t.getMessage() != null) {
            value.setTextContent(t.getMessage());
            node.appendChild(value);
        }

        value = doc.createElement("trace");
        value.setTextContent(Arrays.toString(t.getStackTrace()));
        node.appendChild(value);
        return node;
    }

    /**
     * To pretty xml.
     *
     * @param doc
     *            the doc
     * @return the string
     * @throws TransformerException
     *             the transformer exception
     */
    public static String toPrettyXML(final Document doc) throws TransformerException {
        // set up a transformer
        final TransformerFactory transfac = TransformerFactory.newInstance();
        final Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        // create string from xml tree
        final StringWriter sw = new StringWriter();
        final StreamResult result = new StreamResult(sw);
        final DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        return sw.toString();
    }

    /**
     * Print system information.
     *
     * @param args
     *            variable arguments to pass to main. Valid values are "-show" and "-lookup".
     */
    public static void main(final String[] args) {
        boolean show = false;
        final List<String> lookup = new ArrayList<>();
        if (args != null) {
            String arg;
            for (int i = 0; i < args.length; i++) {
                arg = args[i];
                if (null != arg) {
                    switch (arg) {
                        case "-show":
                            show = true;
                            break;
                        case "-lookup":
                            lookup.add(args[++i]);
                            break;
                        default:
                            WaffleInfo.LOGGER.error("Unknown Argument: {}", arg);
                            throw new RuntimeException("Unknown Argument: " + arg);
                    }
                }
            }
        }

        final WaffleInfo helper = new WaffleInfo();
        try {
            final Document info = helper.getWaffleInfo();
            for (final String name : lookup) {
                info.getDocumentElement().appendChild(helper.getLookupInfo(info, name));
            }

            final String xml = WaffleInfo.toPrettyXML(info);
            final File f;
            if (show) {
                f = File.createTempFile("waffle-info-", ".xml");
                Files.write(f.toPath(), xml.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                Desktop.getDesktop().open(f);
            } else {
                WaffleInfo.LOGGER.info(xml);
            }
        } catch (final IOException | TransformerException | ParserConfigurationException e) {
            WaffleInfo.LOGGER.error(e.getMessage());
            WaffleInfo.LOGGER.trace("", e);
        }
    }
}
