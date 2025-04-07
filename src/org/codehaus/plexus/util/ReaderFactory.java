// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import java.io.InputStream;

public class ReaderFactory
{
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String US_ASCII = "US-ASCII";
    public static final String UTF_16 = "UTF-16";
    public static final String UTF_16BE = "UTF-16BE";
    public static final String UTF_16LE = "UTF-16LE";
    public static final String UTF_8 = "UTF-8";
    public static final String FILE_ENCODING;
    
    public static XmlStreamReader newXmlReader(final InputStream in) throws IOException {
        return new XmlStreamReader(in);
    }
    
    public static XmlStreamReader newXmlReader(final File file) throws IOException {
        return new XmlStreamReader(file);
    }
    
    public static XmlStreamReader newXmlReader(final URL url) throws IOException {
        return new XmlStreamReader(url);
    }
    
    public static Reader newPlatformReader(final InputStream in) {
        return new InputStreamReader(in);
    }
    
    public static Reader newPlatformReader(final File file) throws FileNotFoundException {
        return new FileReader(file);
    }
    
    public static Reader newPlatformReader(final URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }
    
    public static Reader newReader(final InputStream in, final String encoding) throws UnsupportedEncodingException {
        return new InputStreamReader(in, encoding);
    }
    
    public static Reader newReader(final File file, final String encoding) throws FileNotFoundException, UnsupportedEncodingException {
        return new InputStreamReader(new FileInputStream(file), encoding);
    }
    
    public static Reader newReader(final URL url, final String encoding) throws IOException {
        return new InputStreamReader(url.openStream(), encoding);
    }
    
    static {
        FILE_ENCODING = System.getProperty("file.encoding");
    }
}
