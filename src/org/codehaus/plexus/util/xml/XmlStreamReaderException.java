// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.InputStream;

public class XmlStreamReaderException extends XmlReaderException
{
    public XmlStreamReaderException(final String msg, final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final InputStream is) {
        super(msg, bomEnc, xmlGuessEnc, xmlEnc, is);
    }
    
    public XmlStreamReaderException(final String msg, final String ctMime, final String ctEnc, final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final InputStream is) {
        super(msg, ctMime, ctEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
    }
}
