/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.apkc.esxp.utils;

import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * This class comes with the project Apache Xalan. (Comment by Andreas P. Koenzen).
 *
 * @author G. Todd Miller
 */
public class SAX2DOM implements ContentHandler, LexicalHandler
{

    private Node _root = null;
    private Document _document = null;
    private Node _nextSibling = null;
    private Stack<Node> _nodeStk = new Stack<>();
    private Vector<String> _namespaceDecls = null;
    private Node _lastSibling = null;

    public SAX2DOM() throws ParserConfigurationException
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        _document = factory.newDocumentBuilder().newDocument();
        _root = _document;
    }

    public SAX2DOM(Node root, Node nextSibling) throws ParserConfigurationException
    {
        _root = root;
        if (root instanceof Document)
        {
            _document = (Document) root;
        }
        else if (root != null)
        {
            _document = root.getOwnerDocument();
        }
        else
        {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            _document = factory.newDocumentBuilder().newDocument();
            _root = _document;
        }

        _nextSibling = nextSibling;
    }

    public SAX2DOM(Node root) throws ParserConfigurationException
    {
        this(root, null);
    }

    public Node getDOM()
    {
        return _root;
    }

    @Override
    public void characters(char[] ch, int start, int length)
    {
        final Node last = (Node) _nodeStk.peek();

        // No text nodes can be children of root (DOM006 exception)
        if (last != _document)
        {
            final String text = new String(ch, start, length);
            if (_lastSibling != null && _lastSibling.getNodeType() == Node.TEXT_NODE)
            {
                ((Text) _lastSibling).appendData(text);
            }
            else if (last == _root && _nextSibling != null)
            {
                _lastSibling = last.insertBefore(_document.createTextNode(text), _nextSibling);
            }
            else
            {
                _lastSibling = last.appendChild(_document.createTextNode(text));
            }

        }
    }

    @Override
    public void startDocument()
    {
        _nodeStk.push(_root);
    }

    @Override
    public void endDocument()
    {
        _nodeStk.pop();
    }

    @Override
    public void startElement(String namespace, String localName, String qName, Attributes attrs)
    {
        final Element tmp = (Element) _document.createElementNS(namespace, qName);

        // Add namespace declarations first
        if (_namespaceDecls != null)
        {
            final int nDecls = _namespaceDecls.size();
            for (int i = 0; i < nDecls; i++)
            {
                final String prefix = (String) _namespaceDecls.elementAt(i++);

                if (prefix == null || prefix.equals(Constants.EMPTYSTRING))
                {
                    tmp.setAttributeNS(Constants.XMLNS_URI, Constants.XMLNS_PREFIX, (String) _namespaceDecls.elementAt(i));
                }
                else
                {
                    tmp.setAttributeNS(Constants.XMLNS_URI, Constants.XMLNS_STRING + prefix, (String) _namespaceDecls.elementAt(i));
                }
            }
            _namespaceDecls.clear();
        }

        // Add attributes to element
        final int nattrs = attrs.getLength();
        for (int i = 0; i < nattrs; i++)
        {
            if (attrs.getLocalName(i) == null)
            {
                tmp.setAttribute(attrs.getQName(i), attrs.getValue(i));
            }
            else
            {
                tmp.setAttributeNS(attrs.getURI(i), attrs.getQName(i), attrs.getValue(i));
            }
        }

        // Append this new node onto current stack node
        Node last = (Node) _nodeStk.peek();

        // If the SAX2DOM is created with a non-null next sibling node,
        // insert the result nodes before the next sibling under the root.
        if (last == _root && _nextSibling != null)
        {
            last.insertBefore(tmp, _nextSibling);
        }
        else
        {
            last.appendChild(tmp);
        }

        // Push this node onto stack
        _nodeStk.push(tmp);
        _lastSibling = null;
    }

    @Override
    public void endElement(String namespace, String localName, String qName)
    {
        _nodeStk.pop();
        _lastSibling = null;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
    {
        if (_namespaceDecls == null)
        {
            _namespaceDecls = new Vector<>(2);
        }
        _namespaceDecls.addElement(prefix);
        _namespaceDecls.addElement(uri);
    }

    @Override
    public void endPrefixMapping(String prefix)
    {
        // do nothing
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
    {
    }

    @Override
    public void processingInstruction(String target, String data)
    {
        final Node last = (Node) _nodeStk.peek();
        ProcessingInstruction pi = _document.createProcessingInstruction(target, data);
        if (pi != null)
        {
            if (last == _root && _nextSibling != null)
            {
                last.insertBefore(pi, _nextSibling);
            }
            else
            {
                last.appendChild(pi);
            }

            _lastSibling = pi;
        }
    }

    @Override
    public void setDocumentLocator(Locator locator)
    {
    }

    @Override
    public void skippedEntity(String name)
    {
    }

    @Override
    public void comment(char[] ch, int start, int length)
    {
        final Node last = (Node) _nodeStk.peek();
        Comment comment = _document.createComment(new String(ch, start, length));
        if (comment != null)
        {
            if (last == _root && _nextSibling != null)
            {
                last.insertBefore(comment, _nextSibling);
            }
            else
            {
                last.appendChild(comment);
            }

            _lastSibling = comment;
        }
    }

    @Override
    public void startCDATA()
    {
    }

    @Override
    public void endCDATA()
    {
    }

    @Override
    public void startEntity(java.lang.String name)
    {
    }

    @Override
    public void endDTD()
    {
    }

    @Override
    public void endEntity(String name)
    {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException
    {
    }

    /**
     * This class defines constants used by both the compiler and the
     * runtime system.
     *
     * @author Jacek Ambroziak
     * @author Santiago Pericas-Geertsen
     */
    interface Constants
    {

        static final String XSLT_URI = "http://www.w3.org/1999/XSL/Transform";
        static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
        static final String EMPTYSTRING = "";
        static final String XML_PREFIX = "xml";
        static final String XMLNS_PREFIX = "xmlns";
        static final String XMLNS_STRING = "xmlns:";
        static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    }
}
