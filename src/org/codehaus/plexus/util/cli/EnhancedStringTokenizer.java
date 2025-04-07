// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.util.StringTokenizer;

public final class EnhancedStringTokenizer
{
    private StringTokenizer cst;
    String cdelim;
    final boolean cdelimSingleChar;
    final char cdelimChar;
    boolean creturnDelims;
    String lastToken;
    boolean delimLast;
    
    public EnhancedStringTokenizer(final String str) {
        this(str, " \t\n\r\f", false);
    }
    
    public EnhancedStringTokenizer(final String str, final String delim) {
        this(str, delim, false);
    }
    
    public EnhancedStringTokenizer(final String str, final String delim, final boolean returnDelims) {
        this.cst = null;
        this.lastToken = null;
        this.delimLast = true;
        this.cst = new StringTokenizer(str, delim, true);
        this.cdelim = delim;
        this.creturnDelims = returnDelims;
        this.cdelimSingleChar = (delim.length() == 1);
        this.cdelimChar = delim.charAt(0);
    }
    
    public boolean hasMoreTokens() {
        return this.cst.hasMoreTokens();
    }
    
    private String internalNextToken() {
        if (this.lastToken != null) {
            final String last = this.lastToken;
            this.lastToken = null;
            return last;
        }
        final String token = this.cst.nextToken();
        if (!this.isDelim(token)) {
            this.delimLast = false;
            return token;
        }
        if (this.delimLast) {
            this.lastToken = token;
            return "";
        }
        this.delimLast = true;
        return token;
    }
    
    public String nextToken() {
        final String token = this.internalNextToken();
        if (this.creturnDelims) {
            return token;
        }
        if (this.isDelim(token)) {
            return this.hasMoreTokens() ? this.internalNextToken() : "";
        }
        return token;
    }
    
    private boolean isDelim(final String str) {
        if (str.length() == 1) {
            final char ch = str.charAt(0);
            if (this.cdelimSingleChar) {
                if (this.cdelimChar == ch) {
                    return true;
                }
            }
            else if (this.cdelim.indexOf(ch) >= 0) {
                return true;
            }
        }
        return false;
    }
}
