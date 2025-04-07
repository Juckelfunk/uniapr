// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.InputStream;
import java.io.IOException;

public class XmlReaderException extends IOException
{
    private String _bomEncoding;
    private String _xmlGuessEncoding;
    private String _xmlEncoding;
    private String _contentTypeMime;
    private String _contentTypeEncoding;
    private InputStream _is;
    
    public XmlReaderException(final String msg, final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final InputStream is) {
        this(msg, null, null, bomEnc, xmlGuessEnc, xmlEnc, is);
    }
    
    public XmlReaderException(final String msg, final String ctMime, final String ctEnc, final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final InputStream is) {
        super(msg);
        this._contentTypeMime = ctMime;
        this._contentTypeEncoding = ctEnc;
        this._bomEncoding = bomEnc;
        this._xmlGuessEncoding = xmlGuessEnc;
        this._xmlEncoding = xmlEnc;
        this._is = is;
    }
    
    public String getBomEncoding() {
        return this._bomEncoding;
    }
    
    public String getXmlGuessEncoding() {
        return this._xmlGuessEncoding;
    }
    
    public String getXmlEncoding() {
        return this._xmlEncoding;
    }
    
    public String getContentTypeMime() {
        return this._contentTypeMime;
    }
    
    public String getContentTypeEncoding() {
        return this._contentTypeEncoding;
    }
    
    public InputStream getInputStream() {
        return this._is;
    }
}
