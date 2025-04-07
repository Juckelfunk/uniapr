// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.net.URL;

public interface ClassRealm
{
    String getId();
    
    ClassWorld getWorld();
    
    void importFrom(final String p0, final String p1) throws NoSuchRealmException;
    
    void addConstituent(final URL p0);
    
    ClassRealm locateSourceRealm(final String p0);
    
    void setParent(final ClassRealm p0);
    
    ClassRealm createChildRealm(final String p0) throws DuplicateRealmException;
    
    ClassLoader getClassLoader();
    
    ClassRealm getParent();
    
    URL[] getConstituents();
    
    Class loadClass(final String p0) throws ClassNotFoundException;
    
    URL getResource(final String p0);
    
    Enumeration findResources(final String p0) throws IOException;
    
    InputStream getResourceAsStream(final String p0);
    
    void display();
}
