// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.Writer;
import java.io.PrintWriter;

public class CompactXMLWriter extends PrettyPrintXMLWriter
{
    public CompactXMLWriter(final PrintWriter writer) {
        super(writer);
    }
    
    public CompactXMLWriter(final Writer writer) {
        super(writer);
    }
    
    protected void endOfLine() {
    }
}
