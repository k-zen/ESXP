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

import org.w3c.dom.Node;

/**
 * Abstract class representing a DOM walker.
 *
 * @author Andreas P. Koenzen <akc at apkc.net>
 * @version 0.1
 */
public abstract class DOMWalker
{

    public static final byte ELEMENT_NODES = 0x0;
    public static final byte TEXT_NODES = 0x1;

    /**
     * This method initializes the walker by passing the root node.
     *
     * @param rootNode       The root node of the DOM tree.
     * @param nodesToProcess Flag to point which type of nodes we should walk.
     *
     * @return This instance.
     *
     * @throws Exception If the root node is NULL.
     */
    public abstract DOMWalker configure(Node rootNode, byte nodesToProcess) throws Exception;

    /**
     * Returns the next node in the collection.
     *
     * @return The next node in the collection.
     */
    public abstract Node nextNode();

    /**
     * This method will remove all children nodes from the collection.
     * <p>
     * This method should be called after the call to nextNode().
     * </p>
     */
    public abstract void skipChildren();

    /**
     * Checks if the collection is not empty.
     *
     * @return TRUE if there are more nodes, FALSE otherwise.
     */
    public abstract boolean hasNext();
}
