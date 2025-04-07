// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import org.pitest.functional.F2;
import org.pitest.functional.FCollection;
import java.util.List;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;

public final class PitError extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public PitError(final String message, final Throwable cause) {
        super(message + info(), cause);
    }
    
    public PitError(final String message) {
        super(message + info());
    }
    
    private static String info() {
        final RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        return "\n\nPlease copy and paste the information and the complete stacktrace below when reporting an issue\nVM : " + rt.getVmName() + "\nVendor : " + rt.getVmVendor() + "\nVersion : " + rt.getVmVersion() + "\nUptime : " + rt.getUptime() + "\nInput -> " + createInputString(rt.getInputArguments()) + "\nBootClassPathSupported : " + rt.isBootClassPathSupported() + "\n";
    }
    
    private static String createInputString(final List<String> inputArguments) {
        final StringBuilder sb = new StringBuilder();
        FCollection.fold(append(), sb, inputArguments);
        return sb.toString();
    }
    
    private static F2<StringBuilder, String, StringBuilder> append() {
        return new F2<StringBuilder, String, StringBuilder>() {
            private int position = 0;
            
            @Override
            public StringBuilder apply(final StringBuilder a, final String b) {
                ++this.position;
                return a.append("\n " + this.position + " : " + b);
            }
        };
    }
}
