// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.IOException;
import java.util.HashMap;
import java.io.Reader;
import java.util.Map;
import java.io.FilterReader;

public class InterpolationFilterReader extends FilterReader
{
    private String replaceData;
    private int replaceIndex;
    private int previousIndex;
    private Map variables;
    private String beginToken;
    private String endToken;
    private int beginTokenLength;
    private int endTokenLength;
    private static String DEFAULT_BEGIN_TOKEN;
    private static String DEFAULT_END_TOKEN;
    
    public InterpolationFilterReader(final Reader in, final Map variables, final String beginToken, final String endToken) {
        super(in);
        this.replaceData = null;
        this.replaceIndex = -1;
        this.previousIndex = -1;
        this.variables = new HashMap();
        this.variables = variables;
        this.beginToken = beginToken;
        this.endToken = endToken;
        this.beginTokenLength = beginToken.length();
        this.endTokenLength = endToken.length();
    }
    
    public InterpolationFilterReader(final Reader in, final Map variables) {
        this(in, variables, InterpolationFilterReader.DEFAULT_BEGIN_TOKEN, InterpolationFilterReader.DEFAULT_END_TOKEN);
    }
    
    public long skip(final long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("skip value is negative");
        }
        for (long i = 0L; i < n; ++i) {
            if (this.read() == -1) {
                return i;
            }
        }
        return n;
    }
    
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        int i = 0;
        while (i < len) {
            final int ch = this.read();
            if (ch == -1) {
                if (i == 0) {
                    return -1;
                }
                return i;
            }
            else {
                cbuf[off + i] = (char)ch;
                ++i;
            }
        }
        return len;
    }
    
    public int read() throws IOException {
        if (this.replaceIndex != -1 && this.replaceIndex < this.replaceData.length()) {
            final int ch = this.replaceData.charAt(this.replaceIndex++);
            if (this.replaceIndex >= this.replaceData.length()) {
                this.replaceIndex = -1;
            }
            return ch;
        }
        int ch = -1;
        if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
            ch = this.endToken.charAt(this.previousIndex++);
        }
        else {
            ch = this.in.read();
        }
        if (ch != this.beginToken.charAt(0)) {
            return ch;
        }
        final StringBuffer key = new StringBuffer();
        int beginTokenMatchPos = 1;
        do {
            if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
                ch = this.endToken.charAt(this.previousIndex++);
            }
            else {
                ch = this.in.read();
            }
            if (ch == -1) {
                break;
            }
            key.append((char)ch);
            if (beginTokenMatchPos < this.beginTokenLength && ch != this.beginToken.charAt(beginTokenMatchPos++)) {
                ch = -1;
                break;
            }
        } while (ch != this.endToken.charAt(0));
        if (ch != -1 && this.endTokenLength > 1) {
            int endTokenMatchPos = 1;
            do {
                if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
                    ch = this.endToken.charAt(this.previousIndex++);
                }
                else {
                    ch = this.in.read();
                }
                if (ch == -1) {
                    break;
                }
                key.append((char)ch);
                if (ch != this.endToken.charAt(endTokenMatchPos++)) {
                    ch = -1;
                    break;
                }
            } while (endTokenMatchPos < this.endTokenLength);
        }
        if (ch == -1) {
            this.replaceData = key.toString();
            this.replaceIndex = 0;
            return this.beginToken.charAt(0);
        }
        final String variableKey = key.substring(this.beginTokenLength - 1, key.length() - this.endTokenLength);
        final Object o = this.variables.get(variableKey);
        if (o != null) {
            final String value = o.toString();
            if (value.length() != 0) {
                this.replaceData = value;
                this.replaceIndex = 0;
            }
            return this.read();
        }
        this.previousIndex = 0;
        this.replaceData = key.substring(0, key.length() - this.endTokenLength);
        this.replaceIndex = 0;
        return this.beginToken.charAt(0);
    }
    
    static {
        InterpolationFilterReader.DEFAULT_BEGIN_TOKEN = "${";
        InterpolationFilterReader.DEFAULT_END_TOKEN = "}";
    }
}
