// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;

public class Xpp3DomWriter
{
    public static void write(final Writer writer, final Xpp3Dom dom) {
        write(new PrettyPrintXMLWriter(writer), dom);
    }
    
    public static void write(final PrintWriter writer, final Xpp3Dom dom) {
        write(new PrettyPrintXMLWriter(writer), dom);
    }
    
    public static void write(final XMLWriter xmlWriter, final Xpp3Dom dom) {
        write(xmlWriter, dom, true);
    }
    
    public static void write(final XMLWriter xmlWriter, final Xpp3Dom dom, final boolean escape) {
        xmlWriter.startElement(dom.getName());
        final String[] attributeNames = dom.getAttributeNames();
        for (int i = 0; i < attributeNames.length; ++i) {
            final String attributeName = attributeNames[i];
            xmlWriter.addAttribute(attributeName, dom.getAttribute(attributeName));
        }
        final Xpp3Dom[] children = dom.getChildren();
        for (int j = 0; j < children.length; ++j) {
            write(xmlWriter, children[j], escape);
        }
        final String value = dom.getValue();
        if (value != null) {
            if (escape) {
                xmlWriter.writeText(value);
            }
            else {
                xmlWriter.writeMarkup(value);
            }
        }
        xmlWriter.endElement();
    }
}
