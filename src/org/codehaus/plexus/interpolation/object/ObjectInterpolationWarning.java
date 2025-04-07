// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.object;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ObjectInterpolationWarning
{
    private final String message;
    private Throwable cause;
    private final String path;
    
    public ObjectInterpolationWarning(final String path, final String message) {
        this.path = path;
        this.message = message;
    }
    
    public ObjectInterpolationWarning(final String path, final String message, final Throwable cause) {
        this.path = path;
        this.message = message;
        this.cause = cause;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public String toString() {
        if (this.cause == null) {
            return this.path + ": " + this.message;
        }
        final StringWriter w = new StringWriter();
        final PrintWriter pw = new PrintWriter(w);
        pw.print(this.path);
        pw.print(": ");
        pw.println(this.message);
        pw.println("Cause: ");
        this.cause.printStackTrace(pw);
        return w.toString();
    }
}
