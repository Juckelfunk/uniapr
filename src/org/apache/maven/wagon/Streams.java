// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public class Streams
{
    private String out;
    private String err;
    
    public Streams() {
        this.out = "";
        this.err = "";
    }
    
    public String getOut() {
        return this.out;
    }
    
    public void setOut(final String out) {
        this.out = out;
    }
    
    public String getErr() {
        return this.err;
    }
    
    public void setErr(final String err) {
        this.err = err;
    }
}
