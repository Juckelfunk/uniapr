// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.os;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Properties;

public final class OperatingSystemUtils
{
    private OperatingSystemUtils() {
    }
    
    public static Properties getSystemEnvVars() throws IOException {
        return getSystemEnvVars(true);
    }
    
    public static Properties getSystemEnvVars(final boolean caseSensitive) throws IOException {
        Process p = null;
        try {
            final Properties envVars = new Properties();
            final Runtime r = Runtime.getRuntime();
            if (Os.isFamily("windows")) {
                if (Os.isFamily("win9x")) {
                    p = r.exec("command.com /c set");
                }
                else {
                    p = r.exec("cmd.exe /c set");
                }
            }
            else {
                p = r.exec("env");
            }
            final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lastKey = null;
            String lastVal = null;
            String line;
            while ((line = br.readLine()) != null) {
                final int idx = line.indexOf(61);
                if (idx > 0) {
                    lastKey = line.substring(0, idx);
                    if (!caseSensitive) {
                        lastKey = lastKey.toUpperCase();
                    }
                    lastVal = line.substring(idx + 1);
                    envVars.setProperty(lastKey, lastVal);
                }
                else {
                    if (lastKey == null) {
                        continue;
                    }
                    lastVal = lastVal + "\n" + line;
                    envVars.setProperty(lastKey, lastVal);
                }
            }
            return envVars;
        }
        finally {
            if (p != null) {
                p.destroy();
            }
        }
    }
}
