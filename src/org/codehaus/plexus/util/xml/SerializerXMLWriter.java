// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.util.Collections;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class SerializerXMLWriter implements XMLWriter
{
    private final XmlSerializer serializer;
    private final String namespace;
    private final Stack elements;
    private List exceptions;
    
    public SerializerXMLWriter(final String namespace, final XmlSerializer serializer) {
        this.elements = new Stack();
        this.serializer = serializer;
        this.namespace = namespace;
    }
    
    public void startElement(final String name) {
        try {
            this.serializer.startTag(this.namespace, name);
            this.elements.push(name);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }
    
    public void addAttribute(final String key, final String value) {
        try {
            this.serializer.attribute(this.namespace, key, value);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }
    
    public void writeText(final String text) {
        try {
            this.serializer.text(text);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }
    
    public void writeMarkup(final String text) {
        try {
            this.serializer.cdsect(text);
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }
    
    public void endElement() {
        try {
            this.serializer.endTag(this.namespace, this.elements.pop());
        }
        catch (IOException e) {
            this.storeException(e);
        }
    }
    
    private void storeException(final IOException e) {
        if (this.exceptions == null) {
            this.exceptions = new ArrayList();
        }
        this.exceptions.add(e);
    }
    
    public List getExceptions() {
        return (this.exceptions == null) ? Collections.EMPTY_LIST : this.exceptions;
    }
}
