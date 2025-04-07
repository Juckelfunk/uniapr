// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.net.URL;

public class PropertyUtils
{
    public static Properties loadProperties(final URL url) {
        try {
            return loadProperties(url.openStream());
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Properties loadProperties(final File file) {
        try {
            return loadProperties(new FileInputStream(file));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Properties loadProperties(final InputStream is) {
        try {
            final Properties properties = new Properties();
            if (is != null) {
                properties.load(is);
            }
            return properties;
        }
        catch (IOException e) {}
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException ex) {}
        }
        return null;
    }
}
