/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Crimson" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc., 
 * http://www.sun.com.  For more information on the Apache Software 
 * Foundation, please see <http://www.apache.org/>.
 */
package net.apkc.esxp.utils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Program to echo a DOM tree using DOM Level 2 interfaces. Use JAXP to
 * read in and create a DOM tree. DOM currently does not provide a method
 * to do this. (This is planned for Level 3.) See the method main for the
 * three basic steps. Once the application obtains a DOM Document tree, it
 * dumps out the nodes in the tree and associated node attributes for each
 * node.
 *
 * Notes: Program flags may be used to create possibly non-conformant (but
 * useful) DOM trees. Program also shows an example of using an
 * ErrorHandler.
 *
 * @author Edwin Goei <edwingo@apache.org>
 */
public class DOMEcho
{

    /** All output will use this encoding. */
    private final String ENCODING = "UTF-8";
    /** Indentation will be in multiples of basicIndent. */
    private final String INDENT = "  ";
    /** Output goes here. */
    private PrintWriter out;
    /** Indent level. */
    private int indent = 0;

    public DOMEcho(OutputStream out)
    {
        try
        {
            this.out = new PrintWriter(new OutputStreamWriter(out, ENCODING), true);
        }
        catch (UnsupportedEncodingException ex)
        {
        }
    }

    /**
     * Echo common attributes of a DOM node and terminate output with an
     * EOL character.
     *
     * @param n Node to print.
     */
    private void printlnCommon(Node n)
    {
        out.print(" nodeName=\"" + n.getNodeName() + "\"");

        String val = n.getNamespaceURI();
        if (val != null)
        {
            out.print(" uri=\"" + val + "\"");
        }

        val = n.getPrefix();
        if (val != null)
        {
            out.print(" pre=\"" + val + "\"");
        }

        val = n.getLocalName();
        if (val != null)
        {
            out.print(" local=\"" + val + "\"");
        }

        val = n.getNodeValue();
        if (val != null)
        {
            out.print(" nodeValue=");
            if (val.trim().equals(""))
            {
                out.print("[WS]"); // Whitespace
            }
            else
            {
                out.print("\"" + n.getNodeValue() + "\"");
            }
        }

        out.println();
    }

    /**
     * Indent to the current level in multiples of basicIndent.
     */
    private void outputIndentation()
    {
        for (int i = 0; i < indent; i++)
        {
            out.print(INDENT);
        }
    }

    /**
     * Recursive routine to print out DOM tree nodes.
     *
     * @param n Node to print.
     */
    public void echo(Node n)
    {
        outputIndentation(); // Indent to the current level before printing anything

        int type = n.getNodeType();
        switch (type)
        {
            case Node.ATTRIBUTE_NODE:
                out.print("ATTR:");
                printlnCommon(n);
                break;
            case Node.CDATA_SECTION_NODE:
                out.print("CDATA:");
                printlnCommon(n);
                break;
            case Node.COMMENT_NODE:
                out.print("COMM:");
                printlnCommon(n);
                break;
            case Node.DOCUMENT_FRAGMENT_NODE:
                out.print("DOC_FRAG:");
                printlnCommon(n);
                break;
            case Node.DOCUMENT_NODE:
                out.print("DOC:");
                printlnCommon(n);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                out.print("DOC_TYPE:");
                printlnCommon(n);

                // Print entities if any
                NamedNodeMap nodeMap = ((DocumentType) n).getEntities();
                indent += 2;
                for (int i = 0; i < nodeMap.getLength(); i++)
                {
                    Entity entity = (Entity) nodeMap.item(i);
                    echo(entity);
                }
                indent -= 2;
                break;
            case Node.ELEMENT_NODE:
                out.print("ELEM:");
                printlnCommon(n);

                // Print attributes if any.  Note: element attributes are not
                // children of ELEMENT_NODEs but are properties of their
                // associated ELEMENT_NODE.  For this reason, they are printed
                // with 2x the indent level to indicate this.
                NamedNodeMap atts = n.getAttributes();
                indent += 2;
                for (int i = 0; i < atts.getLength(); i++)
                {
                    Node att = atts.item(i);
                    echo(att);
                }
                indent -= 2;
                break;
            case Node.ENTITY_NODE:
                out.print("ENT:");
                printlnCommon(n);
                break;
            case Node.ENTITY_REFERENCE_NODE:
                out.print("ENT_REF:");
                printlnCommon(n);
                break;
            case Node.NOTATION_NODE:
                out.print("NOTATION:");
                printlnCommon(n);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                out.print("PROC_INST:");
                printlnCommon(n);
                break;
            case Node.TEXT_NODE:
                out.print("TEXT:");
                printlnCommon(n);
                break;
            default:
                out.print("UNSUPPORTED NODE: " + type);
                printlnCommon(n);
                break;
        }

        // Print children if any
        indent++;
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling())
        {
            echo(child);
        }
        indent--;
    }
}
