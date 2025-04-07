// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.help;

public class PitHelpError extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public PitHelpError(final Help message, final Object... params) {
        super(message.format(params));
    }
}
