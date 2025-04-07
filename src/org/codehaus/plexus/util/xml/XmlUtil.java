// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.InputStream;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.util.StringUtils;
import java.io.IOException;
import java.io.Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import java.io.Reader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.ReaderFactory;
import java.io.File;

public class XmlUtil
{
    public static final int DEFAULT_INDENTATION_SIZE = 2;
    public static final String DEFAULT_LINE_SEPARATOR;
    
    public static boolean isXml(final File f) {
        if (f == null) {
            throw new IllegalArgumentException("f could not be null.");
        }
        if (!f.isFile()) {
            throw new IllegalArgumentException("The file '" + f.getAbsolutePath() + "' is not a file.");
        }
        Reader reader = null;
        try {
            reader = ReaderFactory.newXmlReader(f);
            final XmlPullParser parser = new MXParser();
            parser.setInput(reader);
            parser.nextToken();
            return true;
        }
        catch (Exception e) {
            return false;
        }
        finally {
            IOUtil.close(reader);
        }
    }
    
    public static void prettyFormat(final Reader reader, final Writer writer) throws IOException {
        prettyFormat(reader, writer, 2, XmlUtil.DEFAULT_LINE_SEPARATOR);
    }
    
    public static void prettyFormat(final Reader reader, final Writer writer, int indentSize, final String lineSeparator) throws IOException {
        if (reader == null) {
            throw new IllegalArgumentException("The reader is null");
        }
        if (writer == null) {
            throw new IllegalArgumentException("The writer is null");
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        final PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(writer);
        xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
        xmlWriter.setLineSeparator(lineSeparator);
        final XmlPullParser parser = new MXParser();
        try {
            parser.setInput(reader);
            prettyFormatInternal(parser, xmlWriter);
        }
        catch (XmlPullParserException e) {
            throw new IOException("Unable to parse the XML: " + e.getMessage());
        }
    }
    
    public static void prettyFormat(final InputStream is, final OutputStream os) throws IOException {
        prettyFormat(is, os, 2, XmlUtil.DEFAULT_LINE_SEPARATOR);
    }
    
    public static void prettyFormat(final InputStream is, final OutputStream os, int indentSize, final String lineSeparator) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("The is is null");
        }
        if (os == null) {
            throw new IllegalArgumentException("The os is null");
        }
        if (indentSize < 0) {
            indentSize = 0;
        }
        Reader reader = null;
        final Writer out = new OutputStreamWriter(os);
        final PrettyPrintXMLWriter xmlWriter = new PrettyPrintXMLWriter(out);
        xmlWriter.setLineIndenter(StringUtils.repeat(" ", indentSize));
        xmlWriter.setLineSeparator(lineSeparator);
        final XmlPullParser parser = new MXParser();
        try {
            reader = ReaderFactory.newXmlReader(is);
            parser.setInput(reader);
            prettyFormatInternal(parser, xmlWriter);
        }
        catch (XmlPullParserException e) {
            throw new IOException("Unable to parse the XML: " + e.getMessage());
        }
        finally {
            IOUtil.close(reader);
            IOUtil.close(out);
        }
    }
    
    private static void prettyFormatInternal(final XmlPullParser parser, final PrettyPrintXMLWriter writer) throws XmlPullParserException, IOException {
        boolean hasTag = false;
        boolean hasComment = false;
        for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.nextToken()) {
            if (eventType == 2) {
                hasTag = true;
                if (hasComment) {
                    writer.writeText(writer.getLineIndenter());
                    hasComment = false;
                }
                writer.startElement(parser.getName());
                for (int i = 0; i < parser.getAttributeCount(); ++i) {
                    final String key = parser.getAttributeName(i);
                    final String value = parser.getAttributeValue(i);
                    writer.addAttribute(key, value);
                }
            }
            else if (eventType == 4) {
                String text = parser.getText();
                if (!text.trim().equals("")) {
                    text = StringUtils.removeDuplicateWhitespace(text);
                    writer.writeText(text);
                }
            }
            else if (eventType == 3) {
                hasTag = false;
                writer.endElement();
            }
            else if (eventType == 9) {
                hasComment = true;
                if (!hasTag) {
                    writer.writeMarkup(writer.getLineSeparator());
                    for (int i = 0; i < writer.getDepth(); ++i) {
                        writer.writeMarkup(writer.getLineIndenter());
                    }
                }
                writer.writeMarkup("<!--" + parser.getText().trim() + " -->");
                if (!hasTag) {
                    writer.writeMarkup(writer.getLineSeparator());
                    for (int i = 0; i < writer.getDepth() - 1; ++i) {
                        writer.writeMarkup(writer.getLineIndenter());
                    }
                }
            }
            else if (eventType == 10) {
                writer.writeMarkup("<!DOCTYPE" + parser.getText() + ">");
                writer.endOfLine();
            }
            else if (eventType == 8) {
                writer.writeMarkup("<?" + parser.getText() + "?>");
                writer.endOfLine();
            }
            else if (eventType == 5) {
                writer.writeMarkup("<![CDATA[" + parser.getText() + "]]>");
            }
            else if (eventType == 6) {
                writer.writeMarkup("&" + parser.getName() + ";");
            }
        }
    }
    
    static {
        DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
