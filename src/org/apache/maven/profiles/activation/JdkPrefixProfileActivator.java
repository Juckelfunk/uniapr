// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Activation;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.model.Profile;

public class JdkPrefixProfileActivator extends DetectedProfileActivator
{
    private static final String JDK_VERSION;
    
    public boolean isActive(final Profile profile) throws ProfileActivationException {
        final Activation activation = profile.getActivation();
        String jdk = activation.getJdk();
        Label_0085: {
            if (!jdk.startsWith("[")) {
                if (!jdk.startsWith("(")) {
                    break Label_0085;
                }
            }
            try {
                return this.matchJdkVersionRange(jdk);
            }
            catch (InvalidVersionSpecificationException e) {
                throw new ProfileActivationException("Invalid JDK version in profile '" + profile.getId() + "': " + e.getMessage());
            }
        }
        boolean reverse = false;
        if (jdk.startsWith("!")) {
            reverse = true;
            jdk = jdk.substring(1);
        }
        if (this.getJdkVersion().startsWith(jdk)) {
            return !reverse;
        }
        return reverse;
    }
    
    private boolean matchJdkVersionRange(final String jdk) throws InvalidVersionSpecificationException {
        final VersionRange jdkVersionRange = VersionRange.createFromVersionSpec(this.convertJdkToMavenVersion(jdk));
        final DefaultArtifactVersion jdkVersion = new DefaultArtifactVersion(this.convertJdkToMavenVersion(this.getJdkVersion()));
        return jdkVersionRange.containsVersion(jdkVersion);
    }
    
    private String convertJdkToMavenVersion(final String jdk) {
        return jdk.replaceAll("_", "-");
    }
    
    protected String getJdkVersion() {
        return JdkPrefixProfileActivator.JDK_VERSION;
    }
    
    @Override
    protected boolean canDetectActivation(final Profile profile) {
        return profile.getActivation() != null && StringUtils.isNotEmpty(profile.getActivation().getJdk());
    }
    
    static {
        JDK_VERSION = System.getProperty("java.version");
    }
}
