// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.io.IOException;
import java.io.Reader;
import java.io.FilterReader;

public class InterpolatorFilterReader extends FilterReader
{
    private Interpolator interpolator;
    private String replaceData;
    private int replaceIndex;
    private int previousIndex;
    public static final String DEFAULT_BEGIN_TOKEN = "${";
    public static final String DEFAULT_END_TOKEN = "}";
    private String beginToken;
    private String orginalBeginToken;
    private String endToken;
    private boolean interpolateWithPrefixPattern;
    private String escapeString;
    private boolean useEscape;
    private boolean preserveEscapeString;
    
    public InterpolatorFilterReader(final Reader in, final Interpolator interpolator) {
        this(in, interpolator, "${", "}");
    }
    
    public InterpolatorFilterReader(final Reader in, final Interpolator interpolator, final String beginToken, final String endToken) {
        super(in);
        this.replaceData = null;
        this.replaceIndex = -1;
        this.previousIndex = -1;
        this.interpolateWithPrefixPattern = true;
        this.useEscape = false;
        this.preserveEscapeString = false;
        this.interpolator = interpolator;
        this.beginToken = beginToken;
        this.endToken = endToken;
        this.orginalBeginToken = this.beginToken;
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
        if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
            ch = this.endToken.charAt(this.previousIndex++);
        }
        else {
            ch = this.in.read();
        }
        if (ch != this.beginToken.charAt(0) && (!this.useEscape || ch != this.orginalBeginToken.charAt(0))) {
            return ch;
        }
        final StringBuffer key = new StringBuffer();
        key.append((char)ch);
        int beginTokenMatchPos = 1;
        do {
            if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
                ch = this.endToken.charAt(this.previousIndex++);
            }
            else {
                ch = this.in.read();
            }
            if (ch == -1) {
                break;
            }
            key.append((char)ch);
            if (beginTokenMatchPos < this.beginToken.length() && ch != this.beginToken.charAt(beginTokenMatchPos++) && this.useEscape && this.orginalBeginToken.length() > beginTokenMatchPos - 1 && ch != this.orginalBeginToken.charAt(beginTokenMatchPos - 1)) {
                ch = -1;
                break;
            }
            if (!this.useEscape || this.orginalBeginToken != this.endToken || !key.toString().startsWith(this.beginToken)) {
                continue;
            }
            ch = this.in.read();
            key.append((char)ch);
        } while (ch != this.endToken.charAt(0));
        if (ch != -1 && this.endToken.length() > 1) {
            int endTokenMatchPos = 1;
            do {
                if (this.previousIndex != -1 && this.previousIndex < this.endToken.length()) {
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
            } while (endTokenMatchPos < this.endToken.length());
        }
        if (ch == -1) {
            this.replaceData = key.toString();
            this.replaceIndex = 1;
            return this.replaceData.charAt(0);
        }
        String value = null;
        try {
            boolean escapeFound = false;
            if (this.useEscape && key.toString().startsWith(this.escapeString + this.orginalBeginToken)) {
                final String keyStr = key.toString();
                if (!this.preserveEscapeString) {
                    value = keyStr.substring(this.escapeString.length(), keyStr.length());
                }
                else {
                    value = keyStr;
                }
                escapeFound = true;
            }
            if (!escapeFound) {
                if (this.interpolateWithPrefixPattern) {
                    value = this.interpolator.interpolate(key.toString(), "");
                }
                else {
                    value = this.interpolator.interpolate(key.toString());
                }
            }
        }
        catch (InterpolationException e) {
            final IllegalArgumentException error = new IllegalArgumentException(e.getMessage());
            error.initCause(e);
            throw error;
        }
        if (value != null) {
            if (value.length() != 0) {
                this.replaceData = value;
                this.replaceIndex = 0;
            }
            return this.read();
        }
        this.previousIndex = 0;
        this.replaceData = key.substring(0, key.length() - this.endToken.length());
        this.replaceIndex = 0;
        return this.beginToken.charAt(0);
    }
    
    public boolean isInterpolateWithPrefixPattern() {
        return this.interpolateWithPrefixPattern;
    }
    
    public void setInterpolateWithPrefixPattern(final boolean interpolateWithPrefixPattern) {
        this.interpolateWithPrefixPattern = interpolateWithPrefixPattern;
    }
    
    public String getEscapeString() {
        return this.escapeString;
    }
    
    public void setEscapeString(final String escapeString) {
        if (escapeString != null && escapeString.length() >= 1) {
            this.escapeString = escapeString;
            this.orginalBeginToken = this.beginToken;
            this.beginToken = escapeString + this.beginToken;
            this.useEscape = (escapeString != null && escapeString.length() >= 1);
        }
    }
    
    public boolean isPreserveEscapeString() {
        return this.preserveEscapeString;
    }
    
    public void setPreserveEscapeString(final boolean preserveEscapeString) {
        this.preserveEscapeString = preserveEscapeString;
    }
}
