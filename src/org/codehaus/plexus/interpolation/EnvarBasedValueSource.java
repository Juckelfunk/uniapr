// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import org.codehaus.plexus.interpolation.os.OperatingSystemUtils;
import java.io.IOException;
import java.util.Properties;

public class EnvarBasedValueSource extends AbstractValueSource
{
    private static Properties envarsCaseSensitive;
    private static Properties envarsCaseInsensitive;
    private final Properties envars;
    private final boolean caseSensitive;
    
    public EnvarBasedValueSource() throws IOException {
        this(true);
    }
    
    public EnvarBasedValueSource(final boolean caseSensitive) throws IOException {
        super(false);
        this.caseSensitive = caseSensitive;
        this.envars = getEnvars(caseSensitive);
    }
    
    private static synchronized Properties getEnvars(final boolean caseSensitive) throws IOException {
        if (caseSensitive) {
            if (EnvarBasedValueSource.envarsCaseSensitive == null) {
                EnvarBasedValueSource.envarsCaseSensitive = OperatingSystemUtils.getSystemEnvVars(caseSensitive);
            }
            return EnvarBasedValueSource.envarsCaseSensitive;
        }
        if (EnvarBasedValueSource.envarsCaseInsensitive == null) {
            EnvarBasedValueSource.envarsCaseInsensitive = OperatingSystemUtils.getSystemEnvVars(caseSensitive);
        }
        return EnvarBasedValueSource.envarsCaseInsensitive;
    }
    
    public Object getValue(final String expression) {
        String expr = expression;
        if (expr.startsWith("env.")) {
            expr = expr.substring("env.".length());
        }
        if (!this.caseSensitive) {
            expr = expr.toUpperCase();
        }
        return this.envars.getProperty(expr);
    }
}
