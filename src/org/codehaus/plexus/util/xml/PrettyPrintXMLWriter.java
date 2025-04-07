// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;
import java.io.Writer;
import java.util.LinkedList;
import java.io.PrintWriter;

public class PrettyPrintXMLWriter implements XMLWriter
{
    protected static final String LS;
    private PrintWriter writer;
    private LinkedList elementStack;
    private boolean tagInProgress;
    private int depth;
    private String lineIndenter;
    private String lineSeparator;
    private String encoding;
    private String docType;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    
    public PrettyPrintXMLWriter(final PrintWriter writer, final String lineIndenter) {
        this(writer, lineIndenter, null, null);
    }
    
    public PrettyPrintXMLWriter(final Writer writer, final String lineIndenter) {
        this(new PrintWriter(writer), lineIndenter);
    }
    
    public PrettyPrintXMLWriter(final PrintWriter writer) {
        this(writer, null, null);
    }
    
    public PrettyPrintXMLWriter(final Writer writer) {
        this(new PrintWriter(writer));
    }
    
    public PrettyPrintXMLWriter(final PrintWriter writer, final String lineIndenter, final String encoding, final String doctype) {
        this(writer, lineIndenter, PrettyPrintXMLWriter.LS, encoding, doctype);
    }
    
    public PrettyPrintXMLWriter(final Writer writer, final String lineIndenter, final String encoding, final String doctype) {
        this(new PrintWriter(writer), lineIndenter, encoding, doctype);
    }
    
    public PrettyPrintXMLWriter(final PrintWriter writer, final String encoding, final String doctype) {
        this(writer, "  ", encoding, doctype);
    }
    
    public PrettyPrintXMLWriter(final Writer writer, final String encoding, final String doctype) {
        this(new PrintWriter(writer), encoding, doctype);
    }
    
    public PrettyPrintXMLWriter(final PrintWriter writer, final String lineIndenter, final String lineSeparator, final String encoding, final String doctype) {
        this.elementStack = new LinkedList();
        this.setWriter(writer);
        this.setLineIndenter(lineIndenter);
        this.setLineSeparator(lineSeparator);
        this.setEncoding(encoding);
        this.setDocType(doctype);
        if (doctype != null || encoding != null) {
            this.writeDocumentHeaders();
        }
    }
    
    public void startElement(final String name) {
        this.tagIsEmpty = false;
        this.finishTag();
        this.write("<");
        this.write(name);
        this.elementStack.addLast(name);
        this.tagInProgress = true;
        this.setDepth(this.getDepth() + 1);
        this.readyForNewLine = true;
        this.tagIsEmpty = true;
    }
    
    public void writeText(final String text) {
        this.writeText(text, true);
    }
    
    public void writeMarkup(final String text) {
        this.writeText(text, false);
    }
    
    private void writeText(String text, final boolean escapeXml) {
        this.readyForNewLine = false;
        this.tagIsEmpty = false;
        this.finishTag();
        if (escapeXml) {
            text = escapeXml(text);
        }
        this.write(StringUtils.unifyLineSeparators(text, this.lineSeparator));
    }
    
    private static String escapeXml(String text) {
        text = text.replaceAll("&", "&amp;");
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        text = text.replaceAll("\"", "&quot;");
        text = text.replaceAll("'", "&apos;");
        return text;
    }
    
    private static String escapeXmlAttribute(String text) {
        text = escapeXml(text);
        text = text.replaceAll("\r\n", "&#10;");
        final Pattern pattern = Pattern.compile("([\u0000-\u001f])");
        Matcher m;
        StringBuffer b;
        for (m = pattern.matcher(text), b = new StringBuffer(); m.find(); m = m.appendReplacement(b, "&#" + Integer.toString(m.group(1).charAt(0)) + ";")) {}
        m.appendTail(b);
        return b.toString();
    }
    
    public void addAttribute(final String key, final String value) {
        this.write(" ");
        this.write(key);
        this.write("=\"");
        this.write(escapeXmlAttribute(value));
        this.write("\"");
    }
    
    public void endElement() {
        this.setDepth(this.getDepth() - 1);
        if (this.tagIsEmpty) {
            this.write("/");
            this.readyForNewLine = false;
            this.finishTag();
            this.elementStack.removeLast();
        }
        else {
            this.finishTag();
            this.write("</" + this.elementStack.removeLast() + ">");
        }
        this.readyForNewLine = true;
    }
    
    private void write(final String str) {
        this.getWriter().write(str);
    }
    
    private void finishTag() {
        if (this.tagInProgress) {
            this.write(">");
        }
        this.tagInProgress = false;
        if (this.readyForNewLine) {
            this.endOfLine();
        }
        this.readyForNewLine = false;
        this.tagIsEmpty = false;
    }
    
    protected String getLineIndenter() {
        return this.lineIndenter;
    }
    
    protected void setLineIndenter(final String lineIndenter) {
        this.lineIndenter = lineIndenter;
    }
    
    protected String getLineSeparator() {
        return this.lineSeparator;
    }
    
    protected void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    protected void endOfLine() {
        this.write(this.getLineSeparator());
        for (int i = 0; i < this.getDepth(); ++i) {
            this.write(this.getLineIndenter());
        }
    }
    
    private void writeDocumentHeaders() {
        this.write("<?xml version=\"1.0\"");
        if (this.getEncoding() != null) {
            this.write(" encoding=\"" + this.getEncoding() + "\"");
        }
        this.write("?>");
        this.endOfLine();
        if (this.getDocType() != null) {
            this.write("<!DOCTYPE ");
            this.write(this.getDocType());
            this.write(">");
            this.endOfLine();
        }
    }
    
    protected void setWriter(final PrintWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer could not be null");
        }
        this.writer = writer;
    }
    
    protected PrintWriter getWriter() {
        return this.writer;
    }
    
    protected void setDepth(final int depth) {
        this.depth = depth;
    }
    
    protected int getDepth() {
        return this.depth;
    }
    
    protected void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    protected String getEncoding() {
        return this.encoding;
    }
    
    protected void setDocType(final String docType) {
        this.docType = docType;
    }
    
    protected String getDocType() {
        return this.docType;
    }
    
    protected LinkedList getElementStack() {
        return this.elementStack;
    }
    
    static {
        LS = System.getProperty("line.separator");
    }
}
