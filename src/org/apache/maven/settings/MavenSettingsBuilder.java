// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.io.File;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;

public interface MavenSettingsBuilder
{
    public static final String ROLE = MavenSettingsBuilder.class.getName();
    public static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";
    public static final String ALT_GLOBAL_SETTINGS_XML_LOCATION = "org.apache.maven.global-settings";
    public static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";
    
    Settings buildSettings() throws IOException, XmlPullParserException;
    
    Settings buildSettings(final boolean p0) throws IOException, XmlPullParserException;
    
    Settings buildSettings(final File p0) throws IOException, XmlPullParserException;
    
    Settings buildSettings(final File p0, final boolean p1) throws IOException, XmlPullParserException;
}
