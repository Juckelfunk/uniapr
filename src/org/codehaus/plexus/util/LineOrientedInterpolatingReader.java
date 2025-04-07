// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.HashSet;
import org.codehaus.plexus.util.reflection.ReflectorException;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.util.Collections;
import java.io.Reader;
import org.codehaus.plexus.util.reflection.Reflector;
import java.util.Map;
import java.io.PushbackReader;
import java.io.FilterReader;

public class LineOrientedInterpolatingReader extends FilterReader
{
    public static final String DEFAULT_START_DELIM = "${";
    public static final String DEFAULT_END_DELIM = "}";
    public static final String DEFAULT_ESCAPE_SEQ = "\\";
    private static final char CARRIAGE_RETURN_CHAR = '\r';
    private static final char NEWLINE_CHAR = '\n';
    private final PushbackReader pushbackReader;
    private final Map context;
    private final String startDelim;
    private final String endDelim;
    private final String escapeSeq;
    private final int minExpressionSize;
    private final Reflector reflector;
    private int lineIdx;
    private String line;
    
    public LineOrientedInterpolatingReader(final Reader reader, final Map context, final String startDelim, final String endDelim, final String escapeSeq) {
        super(reader);
        this.lineIdx = -1;
        this.startDelim = startDelim;
        this.endDelim = endDelim;
        this.escapeSeq = escapeSeq;
        this.minExpressionSize = startDelim.length() + endDelim.length() + 1;
        this.context = Collections.unmodifiableMap((Map<?, ?>)context);
        this.reflector = new Reflector();
        if (reader instanceof PushbackReader) {
            this.pushbackReader = (PushbackReader)reader;
        }
        else {
            this.pushbackReader = new PushbackReader(reader, 1);
        }
    }
    
    public LineOrientedInterpolatingReader(final Reader reader, final Map context, final String startDelim, final String endDelim) {
        this(reader, context, startDelim, endDelim, "\\");
    }
    
    public LineOrientedInterpolatingReader(final Reader reader, final Map context) {
        this(reader, context, "${", "}", "\\");
    }
    
    public int read() throws IOException {
        if (this.line == null || this.lineIdx >= this.line.length()) {
            this.readAndInterpolateLine();
        }
        int next = -1;
        if (this.line != null && this.lineIdx < this.line.length()) {
            next = this.line.charAt(this.lineIdx++);
        }
        return next;
    }
    
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        int fillCount = 0;
        for (int i = off; i < off + len; ++i) {
            final int next = this.read();
            if (next <= -1) {
                break;
            }
            cbuf[i] = (char)next;
            ++fillCount;
        }
        if (fillCount == 0) {
            fillCount = -1;
        }
        return fillCount;
    }
    
    public long skip(final long n) throws IOException {
        long skipCount = 0L;
        for (long i = 0L; i < n; ++i) {
            final int next = this.read();
            if (next < 0) {
                break;
            }
            ++skipCount;
        }
        return skipCount;
    }
    
    private void readAndInterpolateLine() throws IOException {
        final String rawLine = this.readLine();
        if (rawLine != null) {
            final Set expressions = this.parseForExpressions(rawLine);
            final Map evaluatedExpressions = this.evaluateExpressions(expressions);
            final String interpolated = this.replaceWithInterpolatedValues(rawLine, evaluatedExpressions);
            if (interpolated != null && interpolated.length() > 0) {
                this.line = interpolated;
                this.lineIdx = 0;
            }
        }
        else {
            this.line = null;
            this.lineIdx = -1;
        }
    }
    
    private String readLine() throws IOException {
        final StringBuffer lineBuffer = new StringBuffer(40);
        int next = -1;
        boolean lastWasCR = false;
        while ((next = this.pushbackReader.read()) > -1) {
            final char c = (char)next;
            if (c == '\r') {
                lastWasCR = true;
                lineBuffer.append(c);
            }
            else {
                if (c == '\n') {
                    lineBuffer.append(c);
                    break;
                }
                if (lastWasCR) {
                    this.pushbackReader.unread(c);
                    break;
                }
                lineBuffer.append(c);
            }
        }
        if (lineBuffer.length() < 1) {
            return null;
        }
        return lineBuffer.toString();
    }
    
    private String replaceWithInterpolatedValues(final String rawLine, final Map evaluatedExpressions) {
        String result = rawLine;
        for (final Map.Entry entry : evaluatedExpressions.entrySet()) {
            final String expression = entry.getKey();
            final String value = String.valueOf(entry.getValue());
            result = this.findAndReplaceUnlessEscaped(result, expression, value);
        }
        return result;
    }
    
    private Map evaluateExpressions(final Set expressions) {
        final Map evaluated = new TreeMap();
        for (final String rawExpression : expressions) {
            final String realExpression = rawExpression.substring(this.startDelim.length(), rawExpression.length() - this.endDelim.length());
            final String[] parts = realExpression.split("\\.");
            if (parts.length > 0) {
                Object value = this.context.get(parts[0]);
                if (value == null) {
                    continue;
                }
                for (int i = 1; i < parts.length; ++i) {
                    try {
                        value = this.reflector.getObjectProperty(value, parts[i]);
                        if (value == null) {
                            break;
                        }
                    }
                    catch (ReflectorException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                evaluated.put(rawExpression, value);
            }
        }
        return evaluated;
    }
    
    private Set parseForExpressions(final String rawLine) {
        final Set expressions = new HashSet();
        if (rawLine != null) {
            int placeholder = -1;
            do {
                final int start = this.findDelimiter(rawLine, this.startDelim, placeholder);
                if (start < 0) {
                    break;
                }
                final int end = this.findDelimiter(rawLine, this.endDelim, start + 1);
                if (end < 0) {
                    break;
                }
                expressions.add(rawLine.substring(start, end + this.endDelim.length()));
                placeholder = end + 1;
            } while (placeholder < rawLine.length() - this.minExpressionSize);
        }
        return expressions;
    }
    
    private int findDelimiter(final String rawLine, final String delimiter, final int lastPos) {
        int placeholder = lastPos;
        int position = -1;
        do {
            position = rawLine.indexOf(delimiter, placeholder);
            if (position < 0) {
                break;
            }
            final int escEndIdx = rawLine.indexOf(this.escapeSeq, placeholder) + this.escapeSeq.length();
            if (escEndIdx <= this.escapeSeq.length() - 1 || escEndIdx != position) {
                continue;
            }
            placeholder = position + 1;
            position = -1;
        } while (position < 0 && placeholder < rawLine.length() - this.endDelim.length());
        return position;
    }
    
    private String findAndReplaceUnlessEscaped(final String rawLine, final String search, final String replace) {
        final StringBuffer lineBuffer = new StringBuffer((int)(rawLine.length() * 1.5));
        int lastReplacement = -1;
        do {
            final int nextReplacement = rawLine.indexOf(search, lastReplacement + 1);
            if (nextReplacement <= -1) {
                break;
            }
            if (lastReplacement < 0) {
                lastReplacement = 0;
            }
            lineBuffer.append(rawLine.substring(lastReplacement, nextReplacement));
            final int escIdx = rawLine.indexOf(this.escapeSeq, lastReplacement + 1);
            if (escIdx > -1 && escIdx + this.escapeSeq.length() == nextReplacement) {
                lineBuffer.setLength(lineBuffer.length() - this.escapeSeq.length());
                lineBuffer.append(search);
            }
            else {
                lineBuffer.append(replace);
            }
            lastReplacement = nextReplacement + search.length();
        } while (lastReplacement > -1);
        if (lastReplacement < rawLine.length()) {
            lineBuffer.append(rawLine.substring(lastReplacement));
        }
        return lineBuffer.toString();
    }
}
