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
package net.apkc.esxp.processor;

import net.apkc.esxp.exceptions.AttributeNotFoundException;
import net.apkc.esxp.exceptions.InvalidNodeException;
import net.apkc.esxp.exceptions.NodeNotFoundException;
import net.apkc.esxp.exceptions.TagNotFoundException;
import net.apkc.esxp.exceptions.TextNotFoundException;
import net.apkc.esxp.walker.DOMWalker;
import net.apkc.esxp.walker.DOMWalkerFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML parser.
 *
 * @author Andreas P. Koenzen <akc@apkc.net>
 * @version 0.1
 * @see <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder Pattern</a>
 */
public class Processor
{

    private static final Logger LOG = Logger.getLogger(Processor.class.getName());
    private final byte WALKER = DOMWalkerFactory.STACK_DOM_WALKER;

    private Processor()
    {
    }

    public static Processor newBuild()
    {
        return new Processor();
    }

    /**
     * Walks the DOM tree in search of a given tag and when found retrieves the tag's value.
     *
     * @param doc      The XML document to parse.
     * @param rootNode The root node of the XML.
     * @param tag      The tag's name
     * @param strict   If TRUE this method will raise an exception if the tag to search was not found. If
     *                 FALSE will return an empty string.
     *
     * @return The tag's value
     *
     * @throws TagNotFoundException If the required tag was not found.
     */
    public String searchTagValue(Document doc, String rootNode, String tag, boolean strict)
            throws TagNotFoundException
    {
        NodeList nodes = doc.getElementsByTagName(rootNode);

        try
        {
            DOMWalker parser = DOMWalkerFactory.getParser(WALKER).configure(nodes.item(0), DOMWalker.ELEMENT_NODES);

            if (LOG.isTraceEnabled())
            {
                LOG.trace(">>> OBTAINING TAG: " + tag);
            }

            while (parser.hasNext())
            {
                Node node = parser.nextNode();

                if (LOG.isTraceEnabled())
                {
                    LOG.trace("\t> " + node.getNodeName());
                }
                if (node.getNodeName().equalsIgnoreCase(tag))
                {
                    return getNodeValue(node, strict);
                }
            }

            if (LOG.isTraceEnabled())
            {
                LOG.trace(">>> END OBTAININ TAG: " + tag);
            }
        }
        catch (Exception e)
        {
            LOG.error("Error parsing DOM tree. Error: " + e.toString(), e);
        }

        if (strict)
        {
            throw new TagNotFoundException("The tag \"" + tag + "\" was not found in the XML.");
        }
        else
        {
            return "";
        }
    }

    /**
     * Walks the DOM tree in search of a given tag and when found retrieves the tag's attribute value.
     *
     * @param doc           The XML document to parse.
     * @param rootNode      The root node of the XML.
     * @param tag           The tag's name
     * @param attributeName The attribute name
     * @param strict        If TRUE this method will raise an exception if the tag or attribute to search were not found. If
     *                      FALSE will return an empty string.
     *
     * @return The attribute value
     *
     * @throws TagNotFoundException       If the required tag was not found.
     * @throws AttributeNotFoundException If the required attribute was not found.
     */
    public String searchTagAttributeValue(Document doc, String rootNode, String tag, String attributeName, boolean strict)
            throws TagNotFoundException,
                   AttributeNotFoundException
    {
        NodeList nodes = doc.getElementsByTagName(rootNode);

        try
        {
            DOMWalker parser = DOMWalkerFactory.getParser(WALKER).configure(nodes.item(0), DOMWalker.ELEMENT_NODES);
            while (parser.hasNext())
            {
                Node node = parser.nextNode();
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    if (node.getNodeName().equalsIgnoreCase(tag))
                    {
                        NamedNodeMap attributes = node.getAttributes();
                        if (attributes == null)
                        {
                            if (strict)
                            {
                                throw new AttributeNotFoundException("The tag \"" + tag + "\" does not contain attributes.");
                            }
                            else
                            {
                                return "";
                            }
                        }
                        else
                        {
                            Node attribute = attributes.getNamedItem(attributeName);
                            if (attribute == null)
                            {
                                if (strict)
                                {
                                    throw new AttributeNotFoundException("The attribute \"" + attribute + "\" does not exists.");
                                }
                                else
                                {
                                    return "";
                                }
                            }
                            else
                            {
                                return attribute.getNodeValue();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Error parsing DOM tree. Error: " + e.toString(), e);
        }

        if (strict)
        {
            throw new TagNotFoundException("The tag \"" + tag + "\" was not found in the XML.");
        }
        else
        {
            return "";
        }
    }

    /**
     * Retrieves a specific attribute value from a given element node.
     *
     * @param node          The node from which to extract the attribute.
     * @param attributeName The name of the attribute.
     * @param strict        If TRUE this method will raise an exception if the attribute to search was not found. If
     *                      FALSE will return an empty string.
     *
     * @return The value of the given attribute.
     *
     * @throws AttributeNotFoundException If the required attribute was not found.
     * @throws InvalidNodeException       If the node is not an element node.
     */
    public String getNodeAttributeValue(Node node, String attributeName, boolean strict)
            throws AttributeNotFoundException,
                   InvalidNodeException
    {
        if (node.getNodeType() != Node.ELEMENT_NODE)
        {
            throw new InvalidNodeException("The node is not an element node.");
        }

        NamedNodeMap attributeList = node.getAttributes();
        if (attributeList == null)
        {
            if (strict)
            {
                throw new AttributeNotFoundException("The node does not contain attributes.");
            }
            else
            {
                return "";
            }
        }
        else
        {

            Node attribute = attributeList.getNamedItem(attributeName);
            if (attribute == null)
            {
                if (strict)
                {
                    throw new AttributeNotFoundException("The attribute does not exists.");
                }
                else
                {
                    return "";
                }
            }
            else
            {
                String text = attribute.getNodeValue();
                text = text.replaceAll("\\s+", " ");
                text = text.trim();

                return text;
            }
        }
    }

    /**
     * Retrieves TEXT data from a given element node.
     *
     * @param node   The element node from where to extract TEXT data if available.
     * @param strict If TRUE this method will raise an exception if the value to retrieve was not found. If
     *               FALSE will return an empty string.
     *
     * @return A String representing the TEXT.
     *
     * @throws TextNotFoundException If no text was found.
     */
    public String getNodeValue(Node node, boolean strict)
            throws TextNotFoundException
    {
        try
        {
            DOMWalker parser = DOMWalkerFactory.getParser(WALKER).configure(node, DOMWalker.TEXT_NODES);
            while (parser.hasNext())
            {
                Node currentNode = parser.nextNode();
                if (currentNode.getNodeType() == Node.COMMENT_NODE)
                {
                    parser.skipChildren();
                }

                if (currentNode.getNodeType() == Node.TEXT_NODE)
                {
                    String text = currentNode.getNodeValue();
                    text = text.replaceAll("\\s+", " ");
                    text = text.trim();

                    if (text.length() > 0)
                    {
                        return text;
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Error parsing DOM tree. Error: " + e.toString(), e);
        }

        if (strict)
        {
            throw new TextNotFoundException("This node contains no text.");
        }
        else
        {
            return "";
        }
    }

    /**
     * Find the named node in a node's sublist.
     *
     * <ul>
     * <li>Ignores comments and processing instructions.</li>
     * <li>Ignores TEXT nodes (likely to exist and contain ignorable whitespace, if not validating.</li>
     * <li>Ignores CDATA nodes and EntityRef nodes.</li>
     * <li>Examines element nodes to find one with the specified name.</li>
     * </ul>
     *
     * @param name The tag name for the element to find.
     * @param node The element node to start searching from.
     *
     * @return The sub node found.
     *
     * @throws NodeNotFoundException If no node was found.
     */
    public Node retrieveSubNode(String name, Node node)
            throws NodeNotFoundException
    {
        if (node.getNodeType() != Node.ELEMENT_NODE || !node.hasChildNodes())
        {
            return null;
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node subnode = list.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE)
            {
                if (subnode.getNodeName().equals(name))
                {
                    return subnode;
                }
            }
        }

        throw new NodeNotFoundException("A sub node named \"" + name + "\" was not found.");
    }

    /**
     * Walks the DOM tree in search of a given node and when found retrieves the node.
     *
     * @param doc      The XML document to parse.
     * @param rootNode The root node of the XML.
     * @param tag      The tag's name
     *
     * @return The node
     *
     * @throws NodeNotFoundException If the required node was not found.
     */
    public Node searchNode(Document doc, String rootNode, String tag)
            throws NodeNotFoundException
    {
        NodeList nodes = doc.getElementsByTagName(rootNode);

        try
        {
            DOMWalker parser = DOMWalkerFactory.getParser(WALKER).configure(nodes.item(0), DOMWalker.ELEMENT_NODES);

            if (LOG.isTraceEnabled())
            {
                LOG.trace(">>> OBTAINING TAG: " + tag);
            }

            while (parser.hasNext())
            {
                Node node = parser.nextNode();

                if (LOG.isTraceEnabled())
                {
                    LOG.trace("\t> " + node.getNodeName());
                }
                if (node.getNodeName().equalsIgnoreCase(tag))
                {
                    return node;
                }
            }

            if (LOG.isTraceEnabled())
            {
                LOG.trace(">>> END OBTAININ TAG: " + tag);
            }
        }
        catch (Exception e)
        {
            LOG.error("Error parsing DOM tree. Error: " + e.toString(), e);
        }

        throw new NodeNotFoundException("The node \"" + tag + "\" was not found in the XML.");
    }
}
