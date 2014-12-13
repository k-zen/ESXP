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
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.apkc.esxp.processor.UnmarshalledObject;

/**
 * Example class for how to implement an {@link UnmarshalledObject}.
 *
 * @author Andreas P. Koenzen <akc at apkc.net>
 * @version 0.1
 * @see Builder Pattern
 */
class WikiPage extends UnmarshalledObject
{

    private String title = "";
    private String ns = "";
    private String id = "";
    private String revId = "";
    private String revParentId = "";
    private String revTimestamp = "";
    private String revContributorUsername = "";
    private String revContributorId = "";
    private String revMinor = "";
    private String revComment = "";
    private String revText = "";
    private String revTextId = "";
    private String revTextBytes = "";
    private String revSHA1 = "";
    private String revModel = "";
    private String revFormat = "";

    static WikiPage newBuild()
    {
        return new WikiPage();
    }

    String getTitle()
    {
        return title;
    }

    String getNS()
    {
        return ns;
    }

    String getId()
    {
        return id;
    }

    String getRevId()
    {
        return revId;
    }

    String getRevParentId()
    {
        return revParentId;
    }

    String getRevTimestamp()
    {
        return revTimestamp;
    }

    String getRevContributorUsername()
    {
        return revContributorUsername;
    }

    String getRevContributorId()
    {
        return revContributorId;
    }

    String getRevMinor()
    {
        return revMinor;
    }

    String getRevComment()
    {
        return revComment;
    }

    String getRevText()
    {
        return revText;
    }

    String getRevTextId()
    {
        return revTextId;
    }

    String getRevTextBytes()
    {
        return revTextBytes;
    }

    String getRevSHA1()
    {
        return revSHA1;
    }

    String getRevModel()
    {
        return revModel;
    }

    String getRevFormat()
    {
        return revFormat;
    }

    WikiPage setTitle(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        title = p;
        return this;
    }

    WikiPage setNS(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        ns = p;
        return this;
    }

    WikiPage setId(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        id = p;
        return this;
    }

    WikiPage setRevId(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revId = p;
        return this;
    }

    WikiPage setRevParentId(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revParentId = p;
        return this;
    }

    WikiPage setRevTimestamp(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revTimestamp = p;
        return this;
    }

    WikiPage setRevContributorUsername(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revContributorUsername = p;
        return this;
    }

    WikiPage setRevContributorId(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revContributorId = p;
        return this;
    }

    WikiPage setRevMinor(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revMinor = p;
        return this;
    }

    WikiPage setRevComment(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revComment = p;
        return this;
    }

    WikiPage setRevText(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revText = p;
        return this;
    }

    WikiPage setRevTextId(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revTextId = p;
        return this;
    }

    WikiPage setRevTextBytes(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revTextBytes = p;
        return this;
    }

    WikiPage setRevSHA1(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revSHA1 = p;
        return this;
    }

    WikiPage setRevModel(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revModel = p;
        return this;
    }

    WikiPage setRevFormat(String p)
    {
        // Don't allow null values!
        if (p == null)
        {
            return this;
        }

        revFormat = p;
        return this;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public boolean isEmpty()
    {
        return title.isEmpty(); // Title can't be empty.
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof WikiPage))
        {
            return false;
        }

        WikiPage td = (WikiPage) obj;
        return hashCode() == td.hashCode();
    }

    @Override
    public int hashCode()
    {
        int hash = 0x54;
        hash ^= (title != null) ? title.hashCode() : 0x0;
        hash ^= (revText != null) ? revText.hashCode() : 0x0;

        return hash;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        try
        {
            return ((WikiPage) super.clone())
                    .setTitle(title)
                    .setNS(ns)
                    .setId(id)
                    .setRevId(revId)
                    .setRevParentId(revParentId)
                    .setRevTimestamp(revTimestamp)
                    .setRevContributorUsername(revContributorUsername)
                    .setRevContributorId(revContributorId)
                    .setRevMinor(revMinor)
                    .setRevComment(revComment)
                    .setRevText(revText)
                    .setRevTextId(revTextId)
                    .setRevTextBytes(revTextBytes)
                    .setRevSHA1(revSHA1)
                    .setRevModel(revModel)
                    .setRevFormat(revFormat);
        }
        catch (CloneNotSupportedException e)
        {
            throw new CloneNotSupportedException("This object can't be cloned!");
        }
    }

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        return b
                .append("[Title]:").append(title.trim()).append(", ")
                .append("[NS]:").append(ns.trim()).append(", ")
                .append("[Id]:").append(id.trim()).append(", ")
                .append("[Revision Id]:").append(revId.trim()).append(", ")
                .append("[Revision Parent Id]:").append(revParentId.trim()).append(", ")
                .append("[Revision Timestamp]:").append(revTimestamp.trim()).append(", ")
                .append("[Revision Contributor Username]:").append(revContributorUsername.trim()).append(", ")
                .append("[Revision Contributor Id]:").append(revContributorId.trim()).append(", ")
                .append("[Revision Minor]:").append(revMinor.trim()).append(", ")
                .append("[Revision Comment]:").append(revComment.trim()).append(", ")
                .append("[Revision Text]:").append(revText.trim().length() > 15 ? revText.trim().substring(0, 15) : revText.trim()).append(", ") // Abreviated text!
                .append("[Revision Text Id]:").append(revTextId.trim()).append(", ")
                .append("[Revision Text Bytes]:").append(revTextBytes.trim()).append(", ")
                .append("[Revision SHA1]:").append(revSHA1.trim()).append(", ")
                .append("[Revision Model]:").append(revModel.trim()).append(", ")
                .append("[Revision Format]:").append(revFormat.trim())
                .toString();
    }

    @Override
    public int compareTo(Object o)
    {
        return title.compareTo(((WikiPage) o).title); // Order elements by title in natural order.
    }
}
