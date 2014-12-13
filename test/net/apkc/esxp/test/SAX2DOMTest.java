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
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.apkc.esxp.utils.DOMEcho;
import net.apkc.esxp.utils.SAX2DOM;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Class to test the conversion from a SAX parser to a DOM document.
 *
 * @author Andreas P. Koenzen <akc at apkc.net>
 * @version 0.1
 */
class SAX2DOMTest
{

    static final Logger LOG = Logger.getLogger(SAX2DOMTest.class.getName());

    public static void main(String args[])
    {
        try
        {
            if (LOG.isInfoEnabled())
            {
                LOG.info("SAX to DOM conversion...");
            }
            long start = System.currentTimeMillis();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            SAX2DOM converter = new SAX2DOM();

            SAXParser parser = factory.newSAXParser();
            XMLReader xr = parser.getXMLReader();
            xr.setContentHandler(converter);
            xr.parse(new InputSource(SAX2DOMTest.class.getResourceAsStream("/resources/test.xml")));
            long end = System.currentTimeMillis();
            if (LOG.isInfoEnabled())
            {
                LOG.info("Time: " + (end - start) + " milliseconds, " + ((float) (end - start) / 1000) + " seconds, " + ((float) (end - start) / 1000 / 60) + " minutes");
            }

            // Show DOM document
            Scanner s = new Scanner(System.in);
            System.out.print("Show DOM document? [y/n] ");
            if (s.nextLine().equalsIgnoreCase("y"))
            {
                new DOMEcho(System.out).echo(converter.getDOM());
            }
        }
        catch (ParserConfigurationException | SAXException | IOException ex)
        {
            System.err.println("Error executing converter. Error: " + ex.toString());
            System.exit(1);
        }
    }
}
