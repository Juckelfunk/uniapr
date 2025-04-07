// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.Locale;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.io.Reader;

public class XmlReader extends Reader
{
    private static final int BUFFER_SIZE = 4096;
    private static final String UTF_8 = "UTF-8";
    private static final String US_ASCII = "US-ASCII";
    private static final String UTF_16BE = "UTF-16BE";
    private static final String UTF_16LE = "UTF-16LE";
    private static final String UTF_16 = "UTF-16";
    private static final String EBCDIC = "CP1047";
    private static String _staticDefaultEncoding;
    private Reader _reader;
    private String _encoding;
    private String _defaultEncoding;
    private static final Pattern CHARSET_PATTERN;
    static final Pattern ENCODING_PATTERN;
    private static final MessageFormat RAW_EX_1;
    private static final MessageFormat RAW_EX_2;
    private static final MessageFormat HTTP_EX_1;
    private static final MessageFormat HTTP_EX_2;
    private static final MessageFormat HTTP_EX_3;
    
    public static void setDefaultEncoding(final String encoding) {
        XmlReader._staticDefaultEncoding = encoding;
    }
    
    public static String getDefaultEncoding() {
        return XmlReader._staticDefaultEncoding;
    }
    
    public XmlReader(final File file) throws IOException {
        this(new FileInputStream(file));
    }
    
    public XmlReader(final InputStream is) throws IOException {
        this(is, true);
    }
    
    public XmlReader(final InputStream is, final boolean lenient) throws IOException, XmlStreamReaderException {
        this._defaultEncoding = XmlReader._staticDefaultEncoding;
        try {
            this.doRawStream(is, lenient);
        }
        catch (XmlStreamReaderException ex) {
            if (!lenient) {
                throw ex;
            }
            this.doLenientDetection(null, ex);
        }
    }
    
    public XmlReader(final URL url) throws IOException {
        this(url.openConnection());
    }
    
    public XmlReader(final URLConnection conn) throws IOException {
        this._defaultEncoding = XmlReader._staticDefaultEncoding;
        final boolean lenient = true;
        if (conn instanceof HttpURLConnection) {
            try {
                this.doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
            }
            catch (XmlStreamReaderException ex) {
                this.doLenientDetection(conn.getContentType(), ex);
            }
        }
        else if (conn.getContentType() != null) {
            try {
                this.doHttpStream(conn.getInputStream(), conn.getContentType(), lenient);
            }
            catch (XmlStreamReaderException ex) {
                this.doLenientDetection(conn.getContentType(), ex);
            }
        }
        else {
            try {
                this.doRawStream(conn.getInputStream(), lenient);
            }
            catch (XmlStreamReaderException ex) {
                this.doLenientDetection(null, ex);
            }
        }
    }
    
    public XmlReader(final InputStream is, final String httpContentType) throws IOException {
        this(is, httpContentType, true);
    }
    
    public XmlReader(final InputStream is, final String httpContentType, final boolean lenient, final String defaultEncoding) throws IOException, XmlStreamReaderException {
        this._defaultEncoding = ((defaultEncoding == null) ? XmlReader._staticDefaultEncoding : defaultEncoding);
        try {
            this.doHttpStream(is, httpContentType, lenient);
        }
        catch (XmlStreamReaderException ex) {
            if (!lenient) {
                throw ex;
            }
            this.doLenientDetection(httpContentType, ex);
        }
    }
    
    public XmlReader(final InputStream is, final String httpContentType, final boolean lenient) throws IOException, XmlStreamReaderException {
        this(is, httpContentType, lenient, null);
    }
    
    private void doLenientDetection(String httpContentType, XmlStreamReaderException ex) throws IOException {
        if (httpContentType != null && httpContentType.startsWith("text/html")) {
            httpContentType = httpContentType.substring("text/html".length());
            httpContentType = "text/xml" + httpContentType;
            try {
                this.doHttpStream(ex.getInputStream(), httpContentType, true);
                ex = null;
            }
            catch (XmlStreamReaderException ex2) {
                ex = ex2;
            }
        }
        if (ex != null) {
            String encoding = ex.getXmlEncoding();
            if (encoding == null) {
                encoding = ex.getContentTypeEncoding();
            }
            if (encoding == null) {
                encoding = ((this._defaultEncoding == null) ? "UTF-8" : this._defaultEncoding);
            }
            this.prepareReader(ex.getInputStream(), encoding);
        }
    }
    
    public String getEncoding() {
        return this._encoding;
    }
    
    public int read(final char[] buf, final int offset, final int len) throws IOException {
        return this._reader.read(buf, offset, len);
    }
    
    public void close() throws IOException {
        this._reader.close();
    }
    
    private void doRawStream(final InputStream is, final boolean lenient) throws IOException {
        final BufferedInputStream pis = new BufferedInputStream(is, 4096);
        final String bomEnc = getBOMEncoding(pis);
        final String xmlGuessEnc = getXMLGuessEncoding(pis);
        final String xmlEnc = getXmlProlog(pis, xmlGuessEnc);
        final String encoding = this.calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, pis);
        this.prepareReader(pis, encoding);
    }
    
    private void doHttpStream(final InputStream is, final String httpContentType, final boolean lenient) throws IOException {
        final BufferedInputStream pis = new BufferedInputStream(is, 4096);
        final String cTMime = getContentTypeMime(httpContentType);
        final String cTEnc = getContentTypeEncoding(httpContentType);
        final String bomEnc = getBOMEncoding(pis);
        final String xmlGuessEnc = getXMLGuessEncoding(pis);
        final String xmlEnc = getXmlProlog(pis, xmlGuessEnc);
        final String encoding = this.calculateHttpEncoding(cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, pis, lenient);
        this.prepareReader(pis, encoding);
    }
    
    private void prepareReader(final InputStream is, final String encoding) throws IOException {
        this._reader = new InputStreamReader(is, encoding);
        this._encoding = encoding;
    }
    
    private String calculateRawEncoding(final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final InputStream is) throws IOException {
        String encoding;
        if (bomEnc == null) {
            if (xmlGuessEnc == null || xmlEnc == null) {
                encoding = ((this._defaultEncoding == null) ? "UTF-8" : this._defaultEncoding);
            }
            else if (xmlEnc.equals("UTF-16") && (xmlGuessEnc.equals("UTF-16BE") || xmlGuessEnc.equals("UTF-16LE"))) {
                encoding = xmlGuessEnc;
            }
            else {
                encoding = xmlEnc;
            }
        }
        else if (bomEnc.equals("UTF-8")) {
            if (xmlGuessEnc != null && !xmlGuessEnc.equals("UTF-8")) {
                throw new XmlStreamReaderException(XmlReader.RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            if (xmlEnc != null && !xmlEnc.equals("UTF-8")) {
                throw new XmlStreamReaderException(XmlReader.RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            encoding = "UTF-8";
        }
        else {
            if (!bomEnc.equals("UTF-16BE") && !bomEnc.equals("UTF-16LE")) {
                throw new XmlStreamReaderException(XmlReader.RAW_EX_2.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc)) {
                throw new IOException(XmlReader.RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }));
            }
            if (xmlEnc != null && !xmlEnc.equals("UTF-16") && !xmlEnc.equals(bomEnc)) {
                throw new XmlStreamReaderException(XmlReader.RAW_EX_1.format(new Object[] { bomEnc, xmlGuessEnc, xmlEnc }), bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            encoding = bomEnc;
        }
        return encoding;
    }
    
    private String calculateHttpEncoding(final String cTMime, final String cTEnc, final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final InputStream is, final boolean lenient) throws IOException {
        String encoding;
        if (lenient & xmlEnc != null) {
            encoding = xmlEnc;
        }
        else {
            final boolean appXml = isAppXml(cTMime);
            final boolean textXml = isTextXml(cTMime);
            if (!appXml && !textXml) {
                throw new XmlStreamReaderException(XmlReader.HTTP_EX_3.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
            }
            if (cTEnc == null) {
                if (appXml) {
                    encoding = this.calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc, is);
                }
                else {
                    encoding = ((this._defaultEncoding == null) ? "US-ASCII" : this._defaultEncoding);
                }
            }
            else {
                if (bomEnc != null && (cTEnc.equals("UTF-16BE") || cTEnc.equals("UTF-16LE"))) {
                    throw new XmlStreamReaderException(XmlReader.HTTP_EX_1.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
                }
                if (cTEnc.equals("UTF-16")) {
                    if (bomEnc == null || !bomEnc.startsWith("UTF-16")) {
                        throw new XmlStreamReaderException(XmlReader.HTTP_EX_2.format(new Object[] { cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc }), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc, is);
                    }
                    encoding = bomEnc;
                }
                else {
                    encoding = cTEnc;
                }
            }
        }
        return encoding;
    }
    
    private static String getContentTypeMime(final String httpContentType) {
        String mime = null;
        if (httpContentType != null) {
            final int i = httpContentType.indexOf(";");
            mime = ((i == -1) ? httpContentType : httpContentType.substring(0, i)).trim();
        }
        return mime;
    }
    
    private static String getContentTypeEncoding(final String httpContentType) {
        String encoding = null;
        if (httpContentType != null) {
            final int i = httpContentType.indexOf(";");
            if (i > -1) {
                final String postMime = httpContentType.substring(i + 1);
                final Matcher m = XmlReader.CHARSET_PATTERN.matcher(postMime);
                encoding = (m.find() ? m.group(1) : null);
                encoding = ((encoding != null) ? encoding.toUpperCase(Locale.ENGLISH) : null);
            }
        }
        return encoding;
    }
    
    private static String getBOMEncoding(final BufferedInputStream is) throws IOException {
        String encoding = null;
        final int[] bytes = new int[3];
        is.mark(3);
        bytes[0] = is.read();
        bytes[1] = is.read();
        bytes[2] = is.read();
        if (bytes[0] == 254 && bytes[1] == 255) {
            encoding = "UTF-16BE";
            is.reset();
            is.read();
            is.read();
        }
        else if (bytes[0] == 255 && bytes[1] == 254) {
            encoding = "UTF-16LE";
            is.reset();
            is.read();
            is.read();
        }
        else if (bytes[0] == 239 && bytes[1] == 187 && bytes[2] == 191) {
            encoding = "UTF-8";
        }
        else {
            is.reset();
        }
        return encoding;
    }
    
    private static String getXMLGuessEncoding(final BufferedInputStream is) throws IOException {
        String encoding = null;
        final int[] bytes = new int[4];
        is.mark(4);
        bytes[0] = is.read();
        bytes[1] = is.read();
        bytes[2] = is.read();
        bytes[3] = is.read();
        is.reset();
        if (bytes[0] == 0 && bytes[1] == 60 && bytes[2] == 0 && bytes[3] == 63) {
            encoding = "UTF-16BE";
        }
        else if (bytes[0] == 60 && bytes[1] == 0 && bytes[2] == 63 && bytes[3] == 0) {
            encoding = "UTF-16LE";
        }
        else if (bytes[0] == 60 && bytes[1] == 63 && bytes[2] == 120 && bytes[3] == 109) {
            encoding = "UTF-8";
        }
        else if (bytes[0] == 76 && bytes[1] == 111 && bytes[2] == 167 && bytes[3] == 148) {
            encoding = "CP1047";
        }
        return encoding;
    }
    
    private static String getXmlProlog(final BufferedInputStream is, final String guessedEnc) throws IOException {
        String encoding = null;
        if (guessedEnc != null) {
            final byte[] bytes = new byte[4096];
            is.mark(4096);
            int offset;
            int max;
            int c;
            int firstGT;
            String xmlProlog;
            for (offset = 0, max = 4096, c = is.read(bytes, offset, max), firstGT = -1, xmlProlog = null; c != -1 && firstGT == -1 && offset < 4096; offset += c, max -= c, c = is.read(bytes, offset, max), xmlProlog = new String(bytes, 0, offset, guessedEnc), firstGT = xmlProlog.indexOf(62)) {}
            if (firstGT == -1) {
                if (c == -1) {
                    throw new IOException("Unexpected end of XML stream");
                }
                throw new IOException("XML prolog or ROOT element not found on first " + offset + " bytes");
            }
            else {
                final int bytesRead = offset;
                if (bytesRead > 0) {
                    is.reset();
                    final BufferedReader bReader = new BufferedReader(new StringReader(xmlProlog.substring(0, firstGT + 1)));
                    final StringBuffer prolog = new StringBuffer();
                    for (String line = bReader.readLine(); line != null; line = bReader.readLine()) {
                        prolog.append(line);
                    }
                    final Matcher m = XmlReader.ENCODING_PATTERN.matcher(prolog);
                    if (m.find()) {
                        encoding = m.group(1).toUpperCase(Locale.ENGLISH);
                        encoding = encoding.substring(1, encoding.length() - 1);
                    }
                }
            }
        }
        return encoding;
    }
    
    private static boolean isAppXml(final String mime) {
        return mime != null && (mime.equals("application/xml") || mime.equals("application/xml-dtd") || mime.equals("application/xml-external-parsed-entity") || (mime.startsWith("application/") && mime.endsWith("+xml")));
    }
    
    private static boolean isTextXml(final String mime) {
        return mime != null && (mime.equals("text/xml") || mime.equals("text/xml-external-parsed-entity") || (mime.startsWith("text/") && mime.endsWith("+xml")));
    }
    
    static {
        XmlReader._staticDefaultEncoding = null;
        CHARSET_PATTERN = Pattern.compile("charset=([.[^; ]]*)");
        ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
        RAW_EX_1 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch");
        RAW_EX_2 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM");
        HTTP_EX_1 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL");
        HTTP_EX_2 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch");
        HTTP_EX_3 = new MessageFormat("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME");
    }
}
