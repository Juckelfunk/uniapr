// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;
import java.io.File;

public interface MavenProfilesBuilder
{
    public static final String ROLE = MavenProfilesBuilder.class.getName();
    
    ProfilesRoot buildProfiles(final File p0) throws IOException, XmlPullParserException;
}
