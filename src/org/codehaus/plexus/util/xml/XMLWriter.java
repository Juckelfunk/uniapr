// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

public interface XMLWriter
{
    void startElement(final String p0);
    
    void addAttribute(final String p0, final String p1);
    
    void writeText(final String p0);
    
    void writeMarkup(final String p0);
    
    void endElement();
}
