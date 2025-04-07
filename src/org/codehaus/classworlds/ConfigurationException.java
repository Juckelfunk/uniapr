// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

public class ConfigurationException extends Exception
{
    public ConfigurationException(final String msg) {
        super(msg);
    }
    
    public ConfigurationException(final String msg, final int lineNo, final String line) {
        super(msg + " (" + lineNo + "): " + line);
    }
}
