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
package net.apkc.esxp.walker;

import java.util.Stack;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Traverses a DOM tree using a stack as a buffer for nodes.
 *
 * <pre>
 *              |--DOC--NRO
 *     |---CLI--|
 *     |        |--CTA
 *     |
 *     |        |--PIN
 *     |---AUTH-|
 * TRX-|        |--EST      |-NCTA
 *     |                    |-SALDOC
 *     |---CTAS--------CTA--|-SALDOD       |-DETALLE
 *     |                    |-MOVS----MOV--|
 *     |                                   |-MONTO
 *     |              |-NCTA
 *     |       |-CTA--|
 *     |---TXT-|      |-DENOM
 *     |       |
 *     |       |-SOBRE
 *     |       |-MONTO
 *     |       |-ESTADO
 *     |       |-RAZON
 *     |
 *     |---INFO--IPADDR
 * </pre>
 *
 * <p>
 * The algorithm starts by adding the root node into the stack. Then on the
 * first call to the method nextNode() it removes the root node from the stack
 * but adds all its children into it, obeying the order last to first. This last
 * step makes it possible to visit the nodes from left to right.<br/>
 * This step is repeated until there are no more nodes in the pile.
 * </p>
 *
 * <p>
 * Sample behavior of the stack while loading the tree:
 *
 * <pre>
 *               DOC   NRO
 *         CLI   CTA   CTA   CTA         PIN
 *         AUTH  AUTH  AUTH  AUTH  AUTH  EST
 *         CTAS  CTAS  CTAS  CTAS  CTAS  CTAS
 *         TXT   TXT   TXT   TXT   TXT   TXT
 * TRX     INFO  INFO  INFO  INFO  INFO  INFO
 *
 * init()  It.1  It.2  It.3  It.4  It.5  It.6
 * </pre>
 * </p>
 *
 * @author Andreas P. Koenzen <akc@apkc.net>
 * @version 0.1
 */
public class StackDOMWalker extends DOMWalker
{

    /** The last node being analyzed. */
    private Node currentNode;
    /** Child nodes of the current node. */
    private NodeList currentChildren;
    /** Stack of nodes. */
    private Stack<Node> nodes;
    /**
     * If this parser should process ELEMENT nodes or TEXT nodes.
     * 0x0 = Process only ELEMENT nodes.
     * 0x1 = Process only TEXT nodes.
     */
    private byte nodesToProcess = 0x0;

    private StackDOMWalker()
    {
    }

    public static StackDOMWalker newBuild()
    {
        return new StackDOMWalker();
    }

    @Override
    public StackDOMWalker configure(Node rootNode, byte nodesToProcess) throws Exception
    {
        if (rootNode == null)
        {
            throw new Exception("Root node in a DOM tree can't be NULL!");
        }

        nodes = new Stack<>();
        nodes.add(rootNode);
        this.nodesToProcess = nodesToProcess;

        return this;
    }

    @Override
    public Node nextNode()
    {
        if (!hasNext())
        {
            return null;
        }

        currentNode = nodes.pop();
        currentChildren = currentNode.getChildNodes();

        int childLen = (currentChildren != null) ? currentChildren.getLength() : 0;
        for (int i = childLen - 1; i >= 0; i--)
        {
            switch (nodesToProcess)
            {
                case DOMWalker.ELEMENT_NODES:
                    if (currentChildren.item(i).getNodeType() == Node.ELEMENT_NODE)
                    {
                        nodes.add(currentChildren.item(i));
                    }
                    break;
                case DOMWalker.TEXT_NODES:
                    if (currentChildren.item(i).getNodeType() == Node.TEXT_NODE)
                    {
                        nodes.add(currentChildren.item(i));
                    }
                    break;
            }
        }

        return currentNode;
    }

    @Override
    public void skipChildren()
    {
        int childLen = (currentChildren != null) ? currentChildren.getLength() : 0;
        for (int i = 0; i < childLen; i++)
        {
            Node child = nodes.peek();
            if (child.equals(currentChildren.item(i)))
            {
                nodes.pop();
            }
        }
    }

    @Override
    public boolean hasNext()
    {
        return nodes.size() > 0;
    }

    @Override
    public String toString()
    {
        return "Current Node: " + currentNode.getNodeName();
    }
}
