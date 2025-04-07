// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.logging;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SystemStreamLog implements Log
{
    public void debug(final CharSequence content) {
        this.print("debug", content);
    }
    
    public void debug(final CharSequence content, final Throwable error) {
        this.print("debug", content, error);
    }
    
    public void debug(final Throwable error) {
        this.print("debug", error);
    }
    
    public void info(final CharSequence content) {
        this.print("info", content);
    }
    
    public void info(final CharSequence content, final Throwable error) {
        this.print("info", content, error);
    }
    
    public void info(final Throwable error) {
        this.print("info", error);
    }
    
    public void warn(final CharSequence content) {
        this.print("warn", content);
    }
    
    public void warn(final CharSequence content, final Throwable error) {
        this.print("warn", content, error);
    }
    
    public void warn(final Throwable error) {
        this.print("warn", error);
    }
    
    public void error(final CharSequence content) {
        System.err.println("[error] " + content.toString());
    }
    
    public void error(final CharSequence content, final Throwable error) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(sWriter);
        error.printStackTrace(pWriter);
        System.err.println("[error] " + content.toString() + "\n\n" + sWriter.toString());
    }
    
    public void error(final Throwable error) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(sWriter);
        error.printStackTrace(pWriter);
        System.err.println("[error] " + sWriter.toString());
    }
    
    public boolean isDebugEnabled() {
        return false;
    }
    
    public boolean isInfoEnabled() {
        return true;
    }
    
    public boolean isWarnEnabled() {
        return true;
    }
    
    public boolean isErrorEnabled() {
        return true;
    }
    
    private void print(final String prefix, final CharSequence content) {
        System.out.println("[" + prefix + "] " + content.toString());
    }
    
    private void print(final String prefix, final Throwable error) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(sWriter);
        error.printStackTrace(pWriter);
        System.out.println("[" + prefix + "] " + sWriter.toString());
    }
    
    private void print(final String prefix, final CharSequence content, final Throwable error) {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(sWriter);
        error.printStackTrace(pWriter);
        System.out.println("[" + prefix + "] " + content.toString() + "\n\n" + sWriter.toString());
    }
}
