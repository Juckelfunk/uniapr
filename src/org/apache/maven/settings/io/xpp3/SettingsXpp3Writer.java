// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings.io.xpp3;

import org.apache.maven.settings.TrackableBase;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.RepositoryBase;
import org.apache.maven.settings.Proxy;
import java.util.Iterator;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.IdentifiableBase;
import org.apache.maven.settings.ActivationProperty;
import org.apache.maven.settings.ActivationOS;
import org.apache.maven.settings.ActivationFile;
import org.apache.maven.settings.Activation;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.apache.maven.settings.Settings;
import java.io.Writer;

public class SettingsXpp3Writer
{
    private static final String NAMESPACE;
    
    public void write(final Writer writer, final Settings settings) throws IOException {
        final XmlSerializer serializer = new MXSerializer();
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
        serializer.setOutput(writer);
        serializer.startDocument(settings.getModelEncoding(), null);
        this.writeSettings(settings, "settings", serializer);
        serializer.endDocument();
    }
    
    private void writeActivation(final Activation activation, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activation != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (activation.isActiveByDefault()) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "activeByDefault").text(String.valueOf(activation.isActiveByDefault())).endTag(SettingsXpp3Writer.NAMESPACE, "activeByDefault");
            }
            if (activation.getJdk() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "jdk").text(activation.getJdk()).endTag(SettingsXpp3Writer.NAMESPACE, "jdk");
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
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationFile(final ActivationFile activationFile, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationFile != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (activationFile.getMissing() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "missing").text(activationFile.getMissing()).endTag(SettingsXpp3Writer.NAMESPACE, "missing");
            }
            if (activationFile.getExists() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "exists").text(activationFile.getExists()).endTag(SettingsXpp3Writer.NAMESPACE, "exists");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationOS(final ActivationOS activationOS, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationOS != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (activationOS.getName() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "name").text(activationOS.getName()).endTag(SettingsXpp3Writer.NAMESPACE, "name");
            }
            if (activationOS.getFamily() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "family").text(activationOS.getFamily()).endTag(SettingsXpp3Writer.NAMESPACE, "family");
            }
            if (activationOS.getArch() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "arch").text(activationOS.getArch()).endTag(SettingsXpp3Writer.NAMESPACE, "arch");
            }
            if (activationOS.getVersion() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "version").text(activationOS.getVersion()).endTag(SettingsXpp3Writer.NAMESPACE, "version");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationProperty(final ActivationProperty activationProperty, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationProperty != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (activationProperty.getName() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "name").text(activationProperty.getName()).endTag(SettingsXpp3Writer.NAMESPACE, "name");
            }
            if (activationProperty.getValue() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "value").text(activationProperty.getValue()).endTag(SettingsXpp3Writer.NAMESPACE, "value");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeIdentifiableBase(final IdentifiableBase identifiableBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (identifiableBase != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (identifiableBase.getId() != null && !identifiableBase.getId().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(identifiableBase.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeMirror(final Mirror mirror, final String tagName, final XmlSerializer serializer) throws IOException {
        if (mirror != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (mirror.getMirrorOf() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "mirrorOf").text(mirror.getMirrorOf()).endTag(SettingsXpp3Writer.NAMESPACE, "mirrorOf");
            }
            if (mirror.getName() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "name").text(mirror.getName()).endTag(SettingsXpp3Writer.NAMESPACE, "name");
            }
            if (mirror.getUrl() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "url").text(mirror.getUrl()).endTag(SettingsXpp3Writer.NAMESPACE, "url");
            }
            if (mirror.getId() != null && !mirror.getId().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(mirror.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeProfile(final Profile profile, final String tagName, final XmlSerializer serializer) throws IOException {
        if (profile != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (profile.getActivation() != null) {
                this.writeActivation(profile.getActivation(), "activation", serializer);
            }
            if (profile.getProperties() != null && profile.getProperties().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "properties");
                for (final String key : profile.getProperties().keySet()) {
                    final String value = (String)profile.getProperties().get(key);
                    serializer.startTag(SettingsXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(SettingsXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "properties");
            }
            if (profile.getRepositories() != null && profile.getRepositories().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "repositories");
                for (final Repository o : profile.getRepositories()) {
                    this.writeRepository(o, "repository", serializer);
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "repositories");
            }
            if (profile.getPluginRepositories() != null && profile.getPluginRepositories().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "pluginRepositories");
                for (final Repository o : profile.getPluginRepositories()) {
                    this.writeRepository(o, "pluginRepository", serializer);
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "pluginRepositories");
            }
            if (profile.getId() != null && !profile.getId().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(profile.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeProxy(final Proxy proxy, final String tagName, final XmlSerializer serializer) throws IOException {
        if (proxy != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (!proxy.isActive()) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "active").text(String.valueOf(proxy.isActive())).endTag(SettingsXpp3Writer.NAMESPACE, "active");
            }
            if (proxy.getProtocol() != null && !proxy.getProtocol().equals("http")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "protocol").text(proxy.getProtocol()).endTag(SettingsXpp3Writer.NAMESPACE, "protocol");
            }
            if (proxy.getUsername() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "username").text(proxy.getUsername()).endTag(SettingsXpp3Writer.NAMESPACE, "username");
            }
            if (proxy.getPassword() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "password").text(proxy.getPassword()).endTag(SettingsXpp3Writer.NAMESPACE, "password");
            }
            if (proxy.getPort() != 8080) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "port").text(String.valueOf(proxy.getPort())).endTag(SettingsXpp3Writer.NAMESPACE, "port");
            }
            if (proxy.getHost() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "host").text(proxy.getHost()).endTag(SettingsXpp3Writer.NAMESPACE, "host");
            }
            if (proxy.getNonProxyHosts() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "nonProxyHosts").text(proxy.getNonProxyHosts()).endTag(SettingsXpp3Writer.NAMESPACE, "nonProxyHosts");
            }
            if (proxy.getId() != null && !proxy.getId().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(proxy.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepository(final Repository repository, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repository != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (repository.getReleases() != null) {
                this.writeRepositoryPolicy(repository.getReleases(), "releases", serializer);
            }
            if (repository.getSnapshots() != null) {
                this.writeRepositoryPolicy(repository.getSnapshots(), "snapshots", serializer);
            }
            if (repository.getId() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(repository.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            if (repository.getName() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "name").text(repository.getName()).endTag(SettingsXpp3Writer.NAMESPACE, "name");
            }
            if (repository.getUrl() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "url").text(repository.getUrl()).endTag(SettingsXpp3Writer.NAMESPACE, "url");
            }
            if (repository.getLayout() != null && !repository.getLayout().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "layout").text(repository.getLayout()).endTag(SettingsXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepositoryBase(final RepositoryBase repositoryBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repositoryBase != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (repositoryBase.getId() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(repositoryBase.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            if (repositoryBase.getName() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "name").text(repositoryBase.getName()).endTag(SettingsXpp3Writer.NAMESPACE, "name");
            }
            if (repositoryBase.getUrl() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "url").text(repositoryBase.getUrl()).endTag(SettingsXpp3Writer.NAMESPACE, "url");
            }
            if (repositoryBase.getLayout() != null && !repositoryBase.getLayout().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "layout").text(repositoryBase.getLayout()).endTag(SettingsXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepositoryPolicy(final RepositoryPolicy repositoryPolicy, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repositoryPolicy != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (!repositoryPolicy.isEnabled()) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "enabled").text(String.valueOf(repositoryPolicy.isEnabled())).endTag(SettingsXpp3Writer.NAMESPACE, "enabled");
            }
            if (repositoryPolicy.getUpdatePolicy() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "updatePolicy").text(repositoryPolicy.getUpdatePolicy()).endTag(SettingsXpp3Writer.NAMESPACE, "updatePolicy");
            }
            if (repositoryPolicy.getChecksumPolicy() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "checksumPolicy").text(repositoryPolicy.getChecksumPolicy()).endTag(SettingsXpp3Writer.NAMESPACE, "checksumPolicy");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeServer(final Server server, final String tagName, final XmlSerializer serializer) throws IOException {
        if (server != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            if (server.getUsername() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "username").text(server.getUsername()).endTag(SettingsXpp3Writer.NAMESPACE, "username");
            }
            if (server.getPassword() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "password").text(server.getPassword()).endTag(SettingsXpp3Writer.NAMESPACE, "password");
            }
            if (server.getPrivateKey() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "privateKey").text(server.getPrivateKey()).endTag(SettingsXpp3Writer.NAMESPACE, "privateKey");
            }
            if (server.getPassphrase() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "passphrase").text(server.getPassphrase()).endTag(SettingsXpp3Writer.NAMESPACE, "passphrase");
            }
            if (server.getFilePermissions() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "filePermissions").text(server.getFilePermissions()).endTag(SettingsXpp3Writer.NAMESPACE, "filePermissions");
            }
            if (server.getDirectoryPermissions() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "directoryPermissions").text(server.getDirectoryPermissions()).endTag(SettingsXpp3Writer.NAMESPACE, "directoryPermissions");
            }
            if (server.getConfiguration() != null) {
                ((Xpp3Dom)server.getConfiguration()).writeToSerializer(SettingsXpp3Writer.NAMESPACE, serializer);
            }
            if (server.getId() != null && !server.getId().equals("default")) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "id").text(server.getId()).endTag(SettingsXpp3Writer.NAMESPACE, "id");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeSettings(final Settings settings, final String tagName, final XmlSerializer serializer) throws IOException {
        if (settings != null) {
            serializer.setPrefix("", "http://maven.apache.org/SETTINGS/1.0.0");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd");
            if (settings.getLocalRepository() != null) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "localRepository").text(settings.getLocalRepository()).endTag(SettingsXpp3Writer.NAMESPACE, "localRepository");
            }
            if (!settings.isInteractiveMode()) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "interactiveMode").text(String.valueOf(settings.isInteractiveMode())).endTag(SettingsXpp3Writer.NAMESPACE, "interactiveMode");
            }
            if (settings.isUsePluginRegistry()) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "usePluginRegistry").text(String.valueOf(settings.isUsePluginRegistry())).endTag(SettingsXpp3Writer.NAMESPACE, "usePluginRegistry");
            }
            if (settings.isOffline()) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "offline").text(String.valueOf(settings.isOffline())).endTag(SettingsXpp3Writer.NAMESPACE, "offline");
            }
            if (settings.getProxies() != null && settings.getProxies().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "proxies");
                for (final Proxy o : settings.getProxies()) {
                    this.writeProxy(o, "proxy", serializer);
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "proxies");
            }
            if (settings.getServers() != null && settings.getServers().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "servers");
                for (final Server o2 : settings.getServers()) {
                    this.writeServer(o2, "server", serializer);
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "servers");
            }
            if (settings.getMirrors() != null && settings.getMirrors().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "mirrors");
                for (final Mirror o3 : settings.getMirrors()) {
                    this.writeMirror(o3, "mirror", serializer);
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "mirrors");
            }
            if (settings.getProfiles() != null && settings.getProfiles().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "profiles");
                for (final Profile o4 : settings.getProfiles()) {
                    this.writeProfile(o4, "profile", serializer);
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "profiles");
            }
            if (settings.getActiveProfiles() != null && settings.getActiveProfiles().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "activeProfiles");
                for (final String activeProfile : settings.getActiveProfiles()) {
                    serializer.startTag(SettingsXpp3Writer.NAMESPACE, "activeProfile").text(activeProfile).endTag(SettingsXpp3Writer.NAMESPACE, "activeProfile");
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "activeProfiles");
            }
            if (settings.getPluginGroups() != null && settings.getPluginGroups().size() > 0) {
                serializer.startTag(SettingsXpp3Writer.NAMESPACE, "pluginGroups");
                for (final String pluginGroup : settings.getPluginGroups()) {
                    serializer.startTag(SettingsXpp3Writer.NAMESPACE, "pluginGroup").text(pluginGroup).endTag(SettingsXpp3Writer.NAMESPACE, "pluginGroup");
                }
                serializer.endTag(SettingsXpp3Writer.NAMESPACE, "pluginGroups");
            }
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeTrackableBase(final TrackableBase trackableBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (trackableBase != null) {
            serializer.startTag(SettingsXpp3Writer.NAMESPACE, tagName);
            serializer.endTag(SettingsXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    static {
        NAMESPACE = null;
    }
}
