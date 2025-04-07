// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

public class XmlStreamReader extends XmlReader
{
    public XmlStreamReader(final File file) throws IOException {
        super(file);
    }
    
    public XmlStreamReader(final InputStream is) throws IOException {
        super(is);
    }
    
    public XmlStreamReader(final InputStream is, final boolean lenient) throws IOException, XmlStreamReaderException {
        super(is, lenient);
    }
    
    public XmlStreamReader(final URL url) throws IOException {
        super(url);
    }
    
    public XmlStreamReader(final URLConnection conn) throws IOException {
        super(conn);
    }
    
    public XmlStreamReader(final InputStream is, final String httpContentType) throws IOException {
        super(is, httpContentType);
    }
    
    public XmlStreamReader(final InputStream is, final String httpContentType, final boolean lenient, final String defaultEncoding) throws IOException, XmlStreamReaderException {
        super(is, httpContentType, lenient, defaultEncoding);
    }
    
    public XmlStreamReader(final InputStream is, final String httpContentType, final boolean lenient) throws IOException, XmlStreamReaderException {
        super(is, httpContentType, lenient);
    }
}
