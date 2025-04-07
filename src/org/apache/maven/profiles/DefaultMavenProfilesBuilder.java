// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import java.io.Writer;
import org.codehaus.plexus.util.IOUtil;
import java.io.StringWriter;
import org.codehaus.plexus.util.ReaderFactory;
import org.apache.maven.profiles.io.xpp3.ProfilesXpp3Reader;
import java.io.File;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultMavenProfilesBuilder extends AbstractLogEnabled implements MavenProfilesBuilder
{
    private static final String PROFILES_XML_FILE = "profiles.xml";
    
    public ProfilesRoot buildProfiles(final File basedir) throws IOException, XmlPullParserException {
        final File profilesXml = new File(basedir, "profiles.xml");
        ProfilesRoot profilesRoot = null;
        if (profilesXml.exists()) {
            final ProfilesXpp3Reader reader = new ProfilesXpp3Reader();
            Reader profileReader = null;
            try {
                profileReader = ReaderFactory.newXmlReader(profilesXml);
                final StringWriter sWriter = new StringWriter();
                IOUtil.copy(profileReader, sWriter);
                String rawInput = sWriter.toString();
                try {
                    final RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
                    interpolator.addValueSource(new EnvarBasedValueSource());
                    rawInput = interpolator.interpolate(rawInput, "settings");
                }
                catch (Exception e) {
                    this.getLogger().warn("Failed to initialize environment variable resolver. Skipping environment substitution in profiles.xml.");
                    this.getLogger().debug("Failed to initialize envar resolver. Skipping resolution.", e);
                }
                final StringReader sReader = new StringReader(rawInput);
                profilesRoot = reader.read(sReader);
            }
            finally {
                IOUtil.close(profileReader);
            }
        }
        return profilesRoot;
    }
}
