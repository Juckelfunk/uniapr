// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.io.xpp3;

import org.apache.maven.profiles.RepositoryPolicy;
import org.apache.maven.profiles.RepositoryBase;
import java.util.Iterator;
import org.apache.maven.profiles.Repository;
import org.apache.maven.profiles.Profile;
import org.apache.maven.profiles.ActivationProperty;
import org.apache.maven.profiles.ActivationOS;
import org.apache.maven.profiles.ActivationFile;
import org.apache.maven.profiles.Activation;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.apache.maven.profiles.ProfilesRoot;
import java.io.Writer;

public class ProfilesXpp3Writer
{
    private static final String NAMESPACE;
    
    public void write(final Writer writer, final ProfilesRoot profilesRoot) throws IOException {
        final XmlSerializer serializer = new MXSerializer();
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
        serializer.setOutput(writer);
        serializer.startDocument(profilesRoot.getModelEncoding(), null);
        this.writeProfilesRoot(profilesRoot, "profilesXml", serializer);
        serializer.endDocument();
    }
    
    private void writeActivation(final Activation activation, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activation != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (activation.isActiveByDefault()) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "activeByDefault").text(String.valueOf(activation.isActiveByDefault())).endTag(ProfilesXpp3Writer.NAMESPACE, "activeByDefault");
            }
            if (activation.getJdk() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "jdk").text(activation.getJdk()).endTag(ProfilesXpp3Writer.NAMESPACE, "jdk");
            }
            if (activation.getOs() != null) {
                this.writeActivationOS(activation.getOs(), "os", serializer);
            }
            if (activation.getProperty() != null) {
                this.writeActivationProperty(activation.getProperty(), "property", serializer);
            }
            if (activation.getFile() != null) {
                this.writeActivationFile(activation.getFile(), "file", serializer);
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationFile(final ActivationFile activationFile, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationFile != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (activationFile.getMissing() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "missing").text(activationFile.getMissing()).endTag(ProfilesXpp3Writer.NAMESPACE, "missing");
            }
            if (activationFile.getExists() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "exists").text(activationFile.getExists()).endTag(ProfilesXpp3Writer.NAMESPACE, "exists");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationOS(final ActivationOS activationOS, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationOS != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (activationOS.getName() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "name").text(activationOS.getName()).endTag(ProfilesXpp3Writer.NAMESPACE, "name");
            }
            if (activationOS.getFamily() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "family").text(activationOS.getFamily()).endTag(ProfilesXpp3Writer.NAMESPACE, "family");
            }
            if (activationOS.getArch() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "arch").text(activationOS.getArch()).endTag(ProfilesXpp3Writer.NAMESPACE, "arch");
            }
            if (activationOS.getVersion() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "version").text(activationOS.getVersion()).endTag(ProfilesXpp3Writer.NAMESPACE, "version");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationProperty(final ActivationProperty activationProperty, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationProperty != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (activationProperty.getName() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "name").text(activationProperty.getName()).endTag(ProfilesXpp3Writer.NAMESPACE, "name");
            }
            if (activationProperty.getValue() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "value").text(activationProperty.getValue()).endTag(ProfilesXpp3Writer.NAMESPACE, "value");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeProfile(final Profile profile, final String tagName, final XmlSerializer serializer) throws IOException {
        if (profile != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (profile.getId() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "id").text(profile.getId()).endTag(ProfilesXpp3Writer.NAMESPACE, "id");
            }
            if (profile.getActivation() != null) {
                this.writeActivation(profile.getActivation(), "activation", serializer);
            }
            if (profile.getProperties() != null && profile.getProperties().size() > 0) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "properties");
                for (final String key : profile.getProperties().keySet()) {
                    final String value = (String)profile.getProperties().get(key);
                    serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(ProfilesXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(ProfilesXpp3Writer.NAMESPACE, "properties");
            }
            if (profile.getRepositories() != null && profile.getRepositories().size() > 0) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "repositories");
                for (final Repository o : profile.getRepositories()) {
                    this.writeRepository(o, "repository", serializer);
                }
                serializer.endTag(ProfilesXpp3Writer.NAMESPACE, "repositories");
            }
            if (profile.getPluginRepositories() != null && profile.getPluginRepositories().size() > 0) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "pluginRepositories");
                for (final Repository o : profile.getPluginRepositories()) {
                    this.writeRepository(o, "pluginRepository", serializer);
                }
                serializer.endTag(ProfilesXpp3Writer.NAMESPACE, "pluginRepositories");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeProfilesRoot(final ProfilesRoot profilesRoot, final String tagName, final XmlSerializer serializer) throws IOException {
        if (profilesRoot != null) {
            serializer.setPrefix("", "http://maven.apache.org/PROFILES/1.0.0");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/PROFILES/1.0.0 http://maven.apache.org/xsd/profiles-1.0.0.xsd");
            if (profilesRoot.getProfiles() != null && profilesRoot.getProfiles().size() > 0) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "profiles");
                for (final Profile o : profilesRoot.getProfiles()) {
                    this.writeProfile(o, "profile", serializer);
                }
                serializer.endTag(ProfilesXpp3Writer.NAMESPACE, "profiles");
            }
            if (profilesRoot.getActiveProfiles() != null && profilesRoot.getActiveProfiles().size() > 0) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "activeProfiles");
                for (final String activeProfile : profilesRoot.getActiveProfiles()) {
                    serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "activeProfile").text(activeProfile).endTag(ProfilesXpp3Writer.NAMESPACE, "activeProfile");
                }
                serializer.endTag(ProfilesXpp3Writer.NAMESPACE, "activeProfiles");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepository(final Repository repository, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repository != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (repository.getReleases() != null) {
                this.writeRepositoryPolicy(repository.getReleases(), "releases", serializer);
            }
            if (repository.getSnapshots() != null) {
                this.writeRepositoryPolicy(repository.getSnapshots(), "snapshots", serializer);
            }
            if (repository.getId() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "id").text(repository.getId()).endTag(ProfilesXpp3Writer.NAMESPACE, "id");
            }
            if (repository.getName() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "name").text(repository.getName()).endTag(ProfilesXpp3Writer.NAMESPACE, "name");
            }
            if (repository.getUrl() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "url").text(repository.getUrl()).endTag(ProfilesXpp3Writer.NAMESPACE, "url");
            }
            if (repository.getLayout() != null && !repository.getLayout().equals("default")) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "layout").text(repository.getLayout()).endTag(ProfilesXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepositoryBase(final RepositoryBase repositoryBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repositoryBase != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (repositoryBase.getId() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "id").text(repositoryBase.getId()).endTag(ProfilesXpp3Writer.NAMESPACE, "id");
            }
            if (repositoryBase.getName() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "name").text(repositoryBase.getName()).endTag(ProfilesXpp3Writer.NAMESPACE, "name");
            }
            if (repositoryBase.getUrl() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "url").text(repositoryBase.getUrl()).endTag(ProfilesXpp3Writer.NAMESPACE, "url");
            }
            if (repositoryBase.getLayout() != null && !repositoryBase.getLayout().equals("default")) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "layout").text(repositoryBase.getLayout()).endTag(ProfilesXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepositoryPolicy(final RepositoryPolicy repositoryPolicy, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repositoryPolicy != null) {
            serializer.startTag(ProfilesXpp3Writer.NAMESPACE, tagName);
            if (!repositoryPolicy.isEnabled()) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "enabled").text(String.valueOf(repositoryPolicy.isEnabled())).endTag(ProfilesXpp3Writer.NAMESPACE, "enabled");
            }
            if (repositoryPolicy.getUpdatePolicy() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "updatePolicy").text(repositoryPolicy.getUpdatePolicy()).endTag(ProfilesXpp3Writer.NAMESPACE, "updatePolicy");
            }
            if (repositoryPolicy.getChecksumPolicy() != null) {
                serializer.startTag(ProfilesXpp3Writer.NAMESPACE, "checksumPolicy").text(repositoryPolicy.getChecksumPolicy()).endTag(ProfilesXpp3Writer.NAMESPACE, "checksumPolicy");
            }
            serializer.endTag(ProfilesXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    static {
        NAMESPACE = null;
    }
}
