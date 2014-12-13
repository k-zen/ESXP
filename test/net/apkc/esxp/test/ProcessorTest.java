/*
 * Copyright (c) 2014, Andreas P. Koenzen <akc at apkc.net>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.apkc.esxp.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import net.apkc.esxp.exceptions.ParserNotInitializedException;
import net.apkc.esxp.processor.Processor;
import net.apkc.esxp.walker.DOMWalker;
import net.apkc.esxp.walker.DOMWalkerFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Example and testing class for implementing the processor.
 *
 * @author Andreas P. Koenzen <akc at apkc.net>
 * @version 0.1
 */
class ProcessorTest
{

    static final Logger LOG = Logger.getLogger(ProcessorTest.class.getName());
    final byte WALKER = DOMWalkerFactory.STACK_DOM_WALKER;
    final boolean STRICT_MODE = false;
    Processor processor = Processor.newBuild();
    Document doc;
    NodeList nodes;

    /**
     * Configure this XML processor.
     *
     * @param xml          The XML document to parse.
     * @param schemaString The schema used to validate the XML, if null skip.
     * @param rootNode     The root node of the XML.
     *
     * @return This instance.
     */
    ProcessorTest configure(InputStream xmlStream, InputStream schemaStream, String rootNode)
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            if (schemaStream != null)
            {
                // Validate the XML file againts our default schema.
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(new StreamSource(schemaStream));
                dbFactory.setSchema(schema);
            }

            // Configure to Focus on Content.
            dbFactory.setValidating(false);
            dbFactory.setNamespaceAware(true);
            dbFactory.setCoalescing(true);
            dbFactory.setExpandEntityReferences(true);
            dbFactory.setIgnoringComments(true);
            dbFactory.setIgnoringElementContentWhitespace(true);

            // Create a DOM document.
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler()
            {
                @Override
                public void warning(SAXParseException e) throws SAXException
                {
                    LOG.warn("DOM Warning: " + e.toString(), e);
                }

                @Override
                public void error(SAXParseException e) throws SAXException
                {
                    LOG.error("DOM Error: " + e.toString(), e);
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException
                {
                    LOG.fatal("DOM Fatal: " + e.toString(), e);
                    throw e;
                }
            });

            doc = builder.parse(new InputSource(xmlStream)); // Create document
            doc.getDocumentElement().normalize(); // Configure
            nodes = doc.getElementsByTagName(rootNode);
        }
        catch (ParserConfigurationException | SAXException | IOException ex)
        {
            System.err.println("Error configuring processor. Error: " + ex.toString());
            System.exit(1);
        }

        return this;
    }

    List<WikiPage> getPage() throws ParserNotInitializedException
    {
        if (nodes == null)
        {
            throw new ParserNotInitializedException("Parser was not started!");
        }

        List<WikiPage> pages = new ArrayList<>();
        WikiPage page = WikiPage.newBuild();

        try
        {
            DOMWalker mainParser = DOMWalkerFactory.getWalker(WALKER).configure(nodes.item(0), DOMWalker.ELEMENT_NODES);
            while (mainParser.hasNext())
            {
                Node n1 = mainParser.nextNode();
                switch (n1.getNodeName())
                {
                    case "page":
                        DOMWalker subParser1 = DOMWalkerFactory.getWalker(WALKER).configure(n1, DOMWalker.ELEMENT_NODES);
                        while (subParser1.hasNext())
                        {
                            Node n2 = subParser1.nextNode();
                            switch (n2.getNodeName())
                            {
                                case "title":
                                    page.setTitle(processor.getNodeValue(n2, false));
                                    break;
                                case "ns":
                                    page.setNS(processor.getNodeValue(n2, false));
                                    break;
                                case "id":
                                    switch (n2.getParentNode().getNodeName())
                                    {
                                        case "page":
                                            page.setId(processor.getNodeValue(n2, false));
                                            break;
                                        case "revision":
                                            page.setRevId(processor.getNodeValue(n2, false));
                                            break;
                                        case "contributor":
                                            page.setRevContributorId(processor.getNodeValue(n2, false));
                                            break;
                                    }
                                    break;
                                case "parentid":
                                    page.setRevParentId(processor.getNodeValue(n2, false));
                                    break;
                                case "timestamp":
                                    page.setRevTimestamp(processor.getNodeValue(n2, false));
                                    break;
                                case "username":
                                    page.setRevContributorUsername(processor.getNodeValue(n2, false));
                                    break;
                                case "minor":
                                    page.setRevMinor(processor.getNodeValue(n2, false));
                                    break;
                                case "comment":
                                    page.setRevComment(processor.getNodeValue(n2, false));
                                    break;
                                case "text":
                                    page.setRevTextId(processor.getNodeAttributeValue(n2, "id", false));
                                    page.setRevTextBytes(processor.getNodeAttributeValue(n2, "bytes", false));
                                    page.setRevText(processor.getNodeValue(n2, false));
                                    break;
                                case "sha1":
                                    page.setRevSHA1(processor.getNodeValue(n2, false));
                                    break;
                                case "model":
                                    page.setRevModel(processor.getNodeValue(n2, false));
                                    break;
                                case "format":
                                    page.setRevFormat(processor.getNodeValue(n2, false));
                                    break;
                            }
                        }
                        break;
                }

                if (!page.isEmpty())
                {
                    pages.add(page);
                    page = WikiPage.newBuild();
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Error parsing DOM tree. Error: " + e.toString(), e);
        }

        return pages;
    }

    public static void main(String[] args)
    {
        try
        {
            if (LOG.isInfoEnabled())
            {
                LOG.info("XML processing...");
            }
            long start = System.currentTimeMillis();
            ProcessorTest test = new ProcessorTest().configure(
                    ProcessorTest.class.getResourceAsStream("/resources/test.xml"),
                    ProcessorTest.class.getResourceAsStream("/resources/test.xsd"),
                    "mediawiki");
            List<WikiPage> pages = test.getPage();
            long end = System.currentTimeMillis();
            if (LOG.isInfoEnabled())
            {
                LOG.info("Time: " + (end - start) + " milliseconds, " + ((float) (end - start) / 1000) + " seconds, " + ((float) (end - start) / 1000 / 60) + " minutes");
            }

            // Show pages.
            for (WikiPage p : pages)
            {
                System.out.println(p.toString());
            }

            Scanner s = new Scanner(System.in);
            String input = "n";
            while (!input.equals("y"))
            {
                System.out.print("Exit test? [y/n] ");
                input = s.nextLine();
            }
        }
        catch (ParserNotInitializedException ex)
        {
            System.err.println("Problem reading file. Error: " + ex.toString());
        }
    }
}
