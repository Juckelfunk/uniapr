// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.Option;
import java.util.Collection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public interface ClassPathRoot
{
    URL getResource(final String p0) throws MalformedURLException;
    
    InputStream getData(final String p0) throws IOException;
    
    Collection<String> classNames();
    
    Option<String> cacheLocation();
}
