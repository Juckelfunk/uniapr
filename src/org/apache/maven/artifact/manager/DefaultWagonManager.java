// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import java.io.InputStream;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import java.util.Properties;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import java.util.Collection;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.ConnectionException;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import java.security.NoSuchAlgorithmException;
import org.apache.maven.wagon.observers.ChecksumObserver;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import java.io.File;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.repository.Repository;
import java.util.LinkedHashMap;
import java.util.HashMap;
import org.apache.maven.wagon.repository.RepositoryPermissions;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import java.util.Map;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultWagonManager extends AbstractLogEnabled implements WagonManager, Contextualizable, Initializable
{
    private static final String WILDCARD = "*";
    private static final String EXTERNAL_WILDCARD = "external:*";
    private static final String MAVEN_ARTIFACT_PROPERTIES = "META-INF/maven/org.apache.maven/maven-artifact/pom.properties";
    private static final String WAGON_PROVIDER_CONFIGURATION = "wagonProvider";
    private static int anonymousMirrorIdSeed;
    private PlexusContainer container;
    private Map proxies;
    private Map authenticationInfoMap;
    private Map serverPermissionsMap;
    private Map mirrors;
    private Map<String, XmlPlexusConfiguration> serverConfigurationMap;
    private Map<String, String> serverWagonProviderMap;
    private TransferListener downloadMonitor;
    private boolean online;
    private ArtifactRepositoryFactory repositoryFactory;
    private boolean interactive;
    private Map<String, PlexusContainer> availableWagons;
    private RepositoryPermissions defaultRepositoryPermissions;
    private String httpUserAgent;
    private WagonProviderMapping providerMapping;
    
    public DefaultWagonManager() {
        this.proxies = new HashMap();
        this.authenticationInfoMap = new HashMap();
        this.serverPermissionsMap = new HashMap();
        this.mirrors = new LinkedHashMap();
        this.serverConfigurationMap = new HashMap<String, XmlPlexusConfiguration>();
        this.serverWagonProviderMap = new HashMap<String, String>();
        this.online = true;
        this.interactive = true;
        this.availableWagons = new HashMap<String, PlexusContainer>();
        this.providerMapping = new DefaultWagonProviderMapping();
    }
    
    public Wagon getWagon(final Repository repository) throws UnsupportedProtocolException, WagonConfigurationException {
        final String protocol = repository.getProtocol();
        if (protocol == null) {
            throw new UnsupportedProtocolException("The repository " + repository + " does not specify a protocol");
        }
        final Wagon wagon = this.getWagon(protocol, repository.getId());
        this.configureWagon(wagon, repository.getId(), protocol);
        return wagon;
    }
    
    public Wagon getWagon(final String protocol) throws UnsupportedProtocolException {
        return this.getWagon(protocol, null);
    }
    
    private Wagon getWagon(final String protocol, final String repositoryId) throws UnsupportedProtocolException {
        final String hint = this.getWagonHint(protocol, repositoryId);
        final PlexusContainer container = this.getWagonContainer(hint);
        Wagon wagon;
        try {
            wagon = (Wagon)container.lookup(Wagon.ROLE, hint);
        }
        catch (ComponentLookupException e1) {
            throw new UnsupportedProtocolException("Cannot find wagon which supports the requested protocol: " + protocol, e1);
        }
        wagon.setInteractive(this.interactive);
        return wagon;
    }
    
    private String getWagonHint(final String protocol, final String repositoryId) {
        String impl = null;
        if (repositoryId != null && this.serverWagonProviderMap.containsKey(repositoryId)) {
            impl = this.serverWagonProviderMap.get(repositoryId);
            this.getLogger().debug("Using Wagon implementation " + impl + " from settings for server " + repositoryId);
        }
        else {
            impl = this.providerMapping.getWagonProvider(protocol);
            if (impl != null) {
                this.getLogger().debug("Using Wagon implementation " + impl + " from default mapping for protocol " + protocol);
            }
        }
        String hint;
        if (impl != null) {
            hint = protocol + "-" + impl;
            final PlexusContainer container = this.getWagonContainer(hint);
            if (container == null || !container.hasComponent(Wagon.ROLE, hint)) {
                this.getLogger().debug("Cannot find wagon for protocol-provider hint: '" + hint + "', configured for repository: '" + repositoryId + "'. Using protocol hint: '" + protocol + "' instead.");
                hint = protocol;
            }
        }
        else {
            hint = protocol;
        }
        return hint;
    }
    
    private PlexusContainer getWagonContainer(final String hint) {
        PlexusContainer container = this.container;
        if (this.availableWagons.containsKey(hint)) {
            container = this.availableWagons.get(hint);
        }
        return container;
    }
    
    public void putArtifact(final File source, final Artifact artifact, final ArtifactRepository deploymentRepository) throws TransferFailedException {
        this.putRemoteFile(deploymentRepository, source, deploymentRepository.pathOf(artifact), this.downloadMonitor);
    }
    
    public void putArtifactMetadata(final File source, final ArtifactMetadata artifactMetadata, final ArtifactRepository repository) throws TransferFailedException {
        this.getLogger().info("Uploading " + artifactMetadata);
        this.putRemoteFile(repository, source, repository.pathOfRemoteRepositoryMetadata(artifactMetadata), null);
    }
    
    private void putRemoteFile(final ArtifactRepository repository, final File source, final String remotePath, final TransferListener downloadMonitor) throws TransferFailedException {
        this.failIfNotOnline();
        final String protocol = repository.getProtocol();
        Wagon wagon;
        try {
            wagon = this.getWagon(protocol, repository.getId());
            this.configureWagon(wagon, repository);
        }
        catch (UnsupportedProtocolException e) {
            throw new TransferFailedException("Unsupported Protocol: '" + protocol + "': " + e.getMessage(), e);
        }
        if (downloadMonitor != null) {
            wagon.addTransferListener(downloadMonitor);
        }
        final Map checksums = new HashMap(2);
        final Map sums = new HashMap(2);
        try {
            ChecksumObserver checksumObserver = new ChecksumObserver("MD5");
            wagon.addTransferListener(checksumObserver);
            checksums.put("md5", checksumObserver);
            checksumObserver = new ChecksumObserver("SHA-1");
            wagon.addTransferListener(checksumObserver);
            checksums.put("sha1", checksumObserver);
        }
        catch (NoSuchAlgorithmException e2) {
            throw new TransferFailedException("Unable to add checksum methods: " + e2.getMessage(), e2);
        }
        try {
            final Repository artifactRepository = new Repository(repository.getId(), repository.getUrl());
            if (this.serverPermissionsMap.containsKey(repository.getId())) {
                final RepositoryPermissions perms = this.serverPermissionsMap.get(repository.getId());
                this.getLogger().debug("adding permissions to wagon connection: " + perms.getFileMode() + " " + perms.getDirectoryMode());
                artifactRepository.setPermissions(perms);
            }
            else if (this.defaultRepositoryPermissions != null) {
                artifactRepository.setPermissions(this.defaultRepositoryPermissions);
            }
            else {
                this.getLogger().debug("not adding permissions to wagon connection");
            }
            wagon.connect(artifactRepository, this.getAuthenticationInfo(repository.getId()), new ProxyInfoProvider() {
                public ProxyInfo getProxyInfo(final String protocol) {
                    return DefaultWagonManager.this.getProxy(protocol);
                }
            });
            wagon.put(source, remotePath);
            wagon.removeTransferListener(downloadMonitor);
            for (final String extension : checksums.keySet()) {
                final ChecksumObserver observer = checksums.get(extension);
                sums.put(extension, observer.getActualChecksum());
            }
            for (final String extension : checksums.keySet()) {
                final File temp = File.createTempFile("maven-artifact", null);
                temp.deleteOnExit();
                FileUtils.fileWrite(temp.getAbsolutePath(), "UTF-8", sums.get(extension));
                wagon.put(temp, remotePath + "." + extension);
            }
        }
        catch (ConnectionException e3) {
            throw new TransferFailedException("Connection failed: " + e3.getMessage(), e3);
        }
        catch (AuthenticationException e4) {
            throw new TransferFailedException("Authentication failed: " + e4.getMessage(), e4);
        }
        catch (AuthorizationException e5) {
            throw new TransferFailedException("Authorization failed: " + e5.getMessage(), e5);
        }
        catch (ResourceDoesNotExistException e6) {
            throw new TransferFailedException("Resource to deploy not found: " + e6.getMessage(), e6);
        }
        catch (IOException e7) {
            throw new TransferFailedException("Error creating temporary file for deployment: " + e7.getMessage(), e7);
        }
        finally {
            this.disconnectWagon(wagon);
            this.releaseWagon(protocol, wagon, repository.getId());
        }
    }
    
    public void getArtifact(final Artifact artifact, final List remoteRepositories) throws TransferFailedException, ResourceDoesNotExistException {
        boolean successful = false;
        final Iterator iter = remoteRepositories.iterator();
        while (iter.hasNext() && !successful) {
            final ArtifactRepository repository = iter.next();
            try {
                this.getArtifact(artifact, repository);
                successful = artifact.isResolved();
            }
            catch (ResourceDoesNotExistException e2) {
                this.getLogger().info("Unable to find resource '" + artifact.getId() + "' in repository " + repository.getId() + " (" + repository.getUrl() + ")");
            }
            catch (TransferFailedException e) {
                this.getLogger().warn("Unable to get resource '" + artifact.getId() + "' from repository " + repository.getId() + " (" + repository.getUrl() + "): " + e.getMessage());
            }
        }
        if (!successful && !artifact.getFile().exists()) {
            throw new ResourceDoesNotExistException("Unable to download the artifact from any repository");
        }
    }
    
    public void getArtifact(final Artifact artifact, final ArtifactRepository repository) throws TransferFailedException, ResourceDoesNotExistException {
        final String remotePath = repository.pathOf(artifact);
        final ArtifactRepositoryPolicy policy = artifact.isSnapshot() ? repository.getSnapshots() : repository.getReleases();
        if (!policy.isEnabled()) {
            this.getLogger().debug("Skipping disabled repository " + repository.getId());
        }
        else if (repository.isBlacklisted()) {
            this.getLogger().debug("Skipping blacklisted repository " + repository.getId());
        }
        else {
            this.getLogger().debug("Trying repository " + repository.getId());
            this.getRemoteFile(this.getMirrorRepository(repository), artifact.getFile(), remotePath, this.downloadMonitor, policy.getChecksumPolicy(), false);
            this.getLogger().debug("  Artifact resolved");
            artifact.setResolved(true);
        }
    }
    
    public void getArtifactMetadata(final ArtifactMetadata metadata, final ArtifactRepository repository, final File destination, final String checksumPolicy) throws TransferFailedException, ResourceDoesNotExistException {
        final String remotePath = repository.pathOfRemoteRepositoryMetadata(metadata);
        this.getRemoteFile(this.getMirrorRepository(repository), destination, remotePath, null, checksumPolicy, true);
    }
    
    public void getArtifactMetadataFromDeploymentRepository(final ArtifactMetadata metadata, final ArtifactRepository repository, final File destination, final String checksumPolicy) throws TransferFailedException, ResourceDoesNotExistException {
        final String remotePath = repository.pathOfRemoteRepositoryMetadata(metadata);
        this.getRemoteFile(repository, destination, remotePath, null, checksumPolicy, true);
    }
    
    private void getRemoteFile(final ArtifactRepository repository, final File destination, final String remotePath, final TransferListener downloadMonitor, final String checksumPolicy, final boolean force) throws TransferFailedException, ResourceDoesNotExistException {
        this.failIfNotOnline();
        final String protocol = repository.getProtocol();
        Wagon wagon;
        try {
            wagon = this.getWagon(protocol, repository.getId());
            this.configureWagon(wagon, repository);
        }
        catch (UnsupportedProtocolException e) {
            throw new TransferFailedException("Unsupported Protocol: '" + protocol + "': " + e.getMessage(), e);
        }
        if (downloadMonitor != null) {
            wagon.addTransferListener(downloadMonitor);
        }
        final File temp = new File(destination + ".tmp");
        temp.deleteOnExit();
        boolean downloaded = false;
        try {
            this.getLogger().debug("Connecting to repository: '" + repository.getId() + "' with url: '" + repository.getUrl() + "'.");
            wagon.connect(new Repository(repository.getId(), repository.getUrl()), this.getAuthenticationInfo(repository.getId()), new ProxyInfoProvider() {
                public ProxyInfo getProxyInfo(final String protocol) {
                    return DefaultWagonManager.this.getProxy(protocol);
                }
            });
            for (boolean firstRun = true, retry = true; firstRun || retry; firstRun = false) {
                retry = false;
                ChecksumObserver md5ChecksumObserver = null;
                ChecksumObserver sha1ChecksumObserver = null;
                try {
                    md5ChecksumObserver = new ChecksumObserver("MD5");
                    wagon.addTransferListener(md5ChecksumObserver);
                    sha1ChecksumObserver = new ChecksumObserver("SHA-1");
                    wagon.addTransferListener(sha1ChecksumObserver);
                    if (destination.exists() && !force) {
                        try {
                            downloaded = wagon.getIfNewer(remotePath, temp, destination.lastModified());
                            if (!downloaded) {
                                destination.setLastModified(System.currentTimeMillis());
                            }
                        }
                        catch (UnsupportedOperationException e9) {
                            wagon.get(remotePath, temp);
                            downloaded = true;
                        }
                    }
                    else {
                        wagon.get(remotePath, temp);
                        downloaded = true;
                    }
                }
                catch (NoSuchAlgorithmException e2) {
                    throw new TransferFailedException("Unable to add checksum methods: " + e2.getMessage(), e2);
                }
                finally {
                    if (md5ChecksumObserver != null) {
                        wagon.removeTransferListener(md5ChecksumObserver);
                    }
                    if (sha1ChecksumObserver != null) {
                        wagon.removeTransferListener(sha1ChecksumObserver);
                    }
                }
                if (downloaded) {
                    if (downloadMonitor != null) {
                        wagon.removeTransferListener(downloadMonitor);
                    }
                    try {
                        this.verifyChecksum(sha1ChecksumObserver, destination, temp, remotePath, ".sha1", wagon);
                    }
                    catch (ChecksumFailedException e3) {
                        if (firstRun) {
                            this.getLogger().warn("*** CHECKSUM FAILED - " + e3.getMessage() + " - RETRYING");
                            retry = true;
                        }
                        else {
                            this.handleChecksumFailure(checksumPolicy, e3.getMessage(), e3.getCause());
                        }
                    }
                    catch (ResourceDoesNotExistException sha1TryException) {
                        this.getLogger().debug("SHA1 not found, trying MD5", sha1TryException);
                        try {
                            this.verifyChecksum(md5ChecksumObserver, destination, temp, remotePath, ".md5", wagon);
                        }
                        catch (ChecksumFailedException e4) {
                            if (firstRun) {
                                retry = true;
                            }
                            else {
                                this.handleChecksumFailure(checksumPolicy, e4.getMessage(), e4.getCause());
                            }
                        }
                        catch (ResourceDoesNotExistException md5TryException) {
                            this.handleChecksumFailure(checksumPolicy, "Error retrieving checksum file for " + remotePath, md5TryException);
                        }
                    }
                    if (downloadMonitor != null) {
                        wagon.addTransferListener(downloadMonitor);
                    }
                }
            }
        }
        catch (ConnectionException e5) {
            throw new TransferFailedException("Connection failed: " + e5.getMessage(), e5);
        }
        catch (AuthenticationException e6) {
            throw new TransferFailedException("Authentication failed: " + e6.getMessage(), e6);
        }
        catch (AuthorizationException e7) {
            throw new TransferFailedException("Authorization failed: " + e7.getMessage(), e7);
        }
        finally {
            this.disconnectWagon(wagon);
            this.releaseWagon(protocol, wagon, repository.getId());
        }
        if (downloaded) {
            if (!temp.exists()) {
                throw new ResourceDoesNotExistException("Downloaded file does not exist: " + temp);
            }
            if (!temp.renameTo(destination)) {
                try {
                    FileUtils.copyFile(temp, destination);
                    temp.delete();
                }
                catch (IOException e8) {
                    throw new TransferFailedException("Error copying temporary file to the final destination: " + e8.getMessage(), e8);
                }
            }
        }
    }
    
    public ArtifactRepository getMirrorRepository(ArtifactRepository repository) {
        final ArtifactRepository mirror = this.getMirror(repository);
        if (mirror != null) {
            String id = mirror.getId();
            if (id == null) {
                id = repository.getId();
            }
            this.getLogger().debug("Using mirror: " + mirror.getUrl() + " (id: " + id + ")");
            repository = this.repositoryFactory.createArtifactRepository(id, mirror.getUrl(), repository.getLayout(), repository.getSnapshots(), repository.getReleases());
        }
        return repository;
    }
    
    private void failIfNotOnline() throws TransferFailedException {
        if (!this.isOnline()) {
            throw new TransferFailedException("System is offline.");
        }
    }
    
    private void handleChecksumFailure(final String checksumPolicy, final String message, final Throwable cause) throws ChecksumFailedException {
        if ("fail".equals(checksumPolicy)) {
            throw new ChecksumFailedException(message, cause);
        }
        if (!"ignore".equals(checksumPolicy)) {
            this.getLogger().warn("*** CHECKSUM FAILED - " + message + " - IGNORING");
        }
    }
    
    private void verifyChecksum(final ChecksumObserver checksumObserver, final File destination, final File tempDestination, final String remotePath, final String checksumFileExtension, final Wagon wagon) throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {
        try {
            final String actualChecksum = checksumObserver.getActualChecksum();
            final File tempChecksumFile = new File(tempDestination + checksumFileExtension + ".tmp");
            tempChecksumFile.deleteOnExit();
            wagon.get(remotePath + checksumFileExtension, tempChecksumFile);
            String expectedChecksum = FileUtils.fileRead(tempChecksumFile, "UTF-8");
            expectedChecksum = expectedChecksum.trim();
            if (expectedChecksum.regionMatches(true, 0, "MD", 0, 2) || expectedChecksum.regionMatches(true, 0, "SHA", 0, 3)) {
                final int lastSpacePos = expectedChecksum.lastIndexOf(32);
                expectedChecksum = expectedChecksum.substring(lastSpacePos + 1);
            }
            else {
                final int spacePos = expectedChecksum.indexOf(32);
                if (spacePos != -1) {
                    expectedChecksum = expectedChecksum.substring(0, spacePos);
                }
            }
            if (!expectedChecksum.equalsIgnoreCase(actualChecksum)) {
                throw new ChecksumFailedException("Checksum failed on download: local = '" + actualChecksum + "'; remote = '" + expectedChecksum + "'");
            }
            final File checksumFile = new File(destination + checksumFileExtension);
            if (checksumFile.exists()) {
                checksumFile.delete();
            }
            FileUtils.copyFile(tempChecksumFile, checksumFile);
        }
        catch (IOException e) {
            throw new ChecksumFailedException("Invalid checksum file", e);
        }
    }
    
    private void disconnectWagon(final Wagon wagon) {
        try {
            wagon.disconnect();
        }
        catch (ConnectionException e) {
            this.getLogger().error("Problem disconnecting from wagon - ignoring: " + e.getMessage());
        }
    }
    
    private void releaseWagon(final String protocol, final Wagon wagon, final String repositoryId) {
        final String hint = this.getWagonHint(protocol, repositoryId);
        final PlexusContainer container = this.getWagonContainer(hint);
        try {
            container.release(wagon);
        }
        catch (ComponentLifecycleException e) {
            this.getLogger().error("Problem releasing wagon - ignoring: " + e.getMessage());
        }
    }
    
    public ProxyInfo getProxy(final String protocol) {
        final ProxyInfo info = this.proxies.get(protocol);
        if (info != null) {
            this.getLogger().debug("Using Proxy: " + info.getHost());
        }
        return info;
    }
    
    public AuthenticationInfo getAuthenticationInfo(final String id) {
        return this.authenticationInfoMap.get(id);
    }
    
    public ArtifactRepository getMirror(final ArtifactRepository originalRepository) {
        ArtifactRepository selectedMirror = this.mirrors.get(originalRepository.getId());
        if (null == selectedMirror) {
            final Set keySet = this.mirrors.keySet();
            if (keySet != null) {
                for (final String pattern : keySet) {
                    if (this.matchPattern(originalRepository, pattern)) {
                        selectedMirror = this.mirrors.get(pattern);
                        break;
                    }
                }
            }
        }
        return selectedMirror;
    }
    
    public boolean matchPattern(final ArtifactRepository originalRepository, final String pattern) {
        boolean result = false;
        final String originalId = originalRepository.getId();
        if ("*".equals(pattern) || pattern.equals(originalId)) {
            result = true;
        }
        else {
            final String[] repos = pattern.split(",");
            for (int i = 0; i < repos.length; ++i) {
                final String repo = repos[i];
                if (repo.length() > 1 && repo.startsWith("!")) {
                    if (originalId.equals(repo.substring(1))) {
                        result = false;
                        break;
                    }
                }
                else {
                    if (originalId.equals(repo)) {
                        result = true;
                        break;
                    }
                    if ("external:*".equals(repo) && this.isExternalRepo(originalRepository)) {
                        result = true;
                    }
                    else if ("*".equals(repo)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
    
    public boolean isExternalRepo(final ArtifactRepository originalRepository) {
        try {
            final URL url = new URL(originalRepository.getUrl());
            return !url.getHost().equals("localhost") && !url.getHost().equals("127.0.0.1") && !url.getProtocol().equals("file");
        }
        catch (MalformedURLException e) {
            return false;
        }
    }
    
    public void addProxy(final String protocol, final String host, final int port, final String username, final String password, final String nonProxyHosts) {
        final ProxyInfo proxyInfo = new ProxyInfo();
        proxyInfo.setHost(host);
        proxyInfo.setType(protocol);
        proxyInfo.setPort(port);
        proxyInfo.setNonProxyHosts(nonProxyHosts);
        proxyInfo.setUserName(username);
        proxyInfo.setPassword(password);
        this.proxies.put(protocol, proxyInfo);
    }
    
    public void contextualize(final Context context) throws ContextException {
        this.container = (PlexusContainer)context.get("plexus");
    }
    
    public void setDownloadMonitor(final TransferListener downloadMonitor) {
        this.downloadMonitor = downloadMonitor;
    }
    
    public void addAuthenticationInfo(final String repositoryId, final String username, final String password, final String privateKey, final String passphrase) {
        final AuthenticationInfo authInfo = new AuthenticationInfo();
        authInfo.setUserName(username);
        authInfo.setPassword(password);
        authInfo.setPrivateKey(privateKey);
        authInfo.setPassphrase(passphrase);
        this.authenticationInfoMap.put(repositoryId, authInfo);
    }
    
    public void addPermissionInfo(final String repositoryId, final String filePermissions, final String directoryPermissions) {
        final RepositoryPermissions permissions = new RepositoryPermissions();
        boolean addPermissions = false;
        if (filePermissions != null) {
            permissions.setFileMode(filePermissions);
            addPermissions = true;
        }
        if (directoryPermissions != null) {
            permissions.setDirectoryMode(directoryPermissions);
            addPermissions = true;
        }
        if (addPermissions) {
            this.serverPermissionsMap.put(repositoryId, permissions);
        }
    }
    
    public void addMirror(String id, final String mirrorOf, final String url) {
        if (id == null) {
            id = "mirror-" + DefaultWagonManager.anonymousMirrorIdSeed++;
            this.getLogger().warn("You are using a mirror that doesn't declare an <id/> element. Using '" + id + "' instead:\nId: " + id + "\nmirrorOf: " + mirrorOf + "\nurl: " + url + "\n");
        }
        final ArtifactRepository mirror = new DefaultArtifactRepository(id, url, null);
        if (!this.mirrors.containsKey(mirrorOf)) {
            this.mirrors.put(mirrorOf, mirror);
        }
    }
    
    public void setOnline(final boolean online) {
        this.online = online;
    }
    
    public boolean isOnline() {
        return this.online;
    }
    
    public void setInteractive(final boolean interactive) {
        this.interactive = interactive;
    }
    
    public void registerWagons(final Collection wagons, final PlexusContainer extensionContainer) {
        final Iterator<String> i = wagons.iterator();
        while (i.hasNext()) {
            this.availableWagons.put(i.next(), extensionContainer);
        }
    }
    
    private void configureWagon(final Wagon wagon, final ArtifactRepository repository) throws WagonConfigurationException {
        this.configureWagon(wagon, repository.getId(), repository.getProtocol());
    }
    
    private void configureWagon(final Wagon wagon, final String repositoryId, final String protocol) throws WagonConfigurationException {
        PlexusConfiguration config = this.serverConfigurationMap.get(repositoryId);
        if (protocol.startsWith("http") || protocol.startsWith("dav")) {
            config = this.updateUserAgentForHttp(wagon, config);
        }
        if (config != null) {
            ComponentConfigurator componentConfigurator = null;
            try {
                componentConfigurator = (ComponentConfigurator)this.container.lookup(ComponentConfigurator.ROLE, "wagon");
                componentConfigurator.configureComponent(wagon, config, this.container.getContainerRealm());
            }
            catch (ComponentLookupException e) {
                throw new WagonConfigurationException(repositoryId, "Unable to lookup wagon configurator. Wagon configuration cannot be applied.", e);
            }
            catch (ComponentConfigurationException e2) {
                throw new WagonConfigurationException(repositoryId, "Unable to apply wagon configuration.", e2);
            }
            finally {
                if (componentConfigurator != null) {
                    try {
                        this.container.release(componentConfigurator);
                    }
                    catch (ComponentLifecycleException e3) {
                        this.getLogger().error("Problem releasing configurator - ignoring: " + e3.getMessage());
                    }
                }
            }
        }
    }
    
    private PlexusConfiguration updateUserAgentForHttp(final Wagon wagon, PlexusConfiguration config) {
        if (config == null) {
            config = new XmlPlexusConfiguration("configuration");
        }
        if (this.httpUserAgent != null) {
            try {
                wagon.getClass().getMethod("setHttpHeaders", Properties.class);
                final PlexusConfiguration headerConfig = config.getChild("httpHeaders", true);
                final PlexusConfiguration[] children = headerConfig.getChildren("property");
                boolean found = false;
                this.getLogger().debug("Checking for pre-existing User-Agent configuration.");
                for (int i = 0; i < children.length; ++i) {
                    final PlexusConfiguration c = children[i].getChild("name", false);
                    if (c != null && "User-Agent".equals(c.getValue(null))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.getLogger().debug("Adding User-Agent configuration.");
                    final XmlPlexusConfiguration propertyConfig = new XmlPlexusConfiguration("property");
                    headerConfig.addChild(propertyConfig);
                    final XmlPlexusConfiguration nameConfig = new XmlPlexusConfiguration("name");
                    nameConfig.setValue("User-Agent");
                    propertyConfig.addChild(nameConfig);
                    final XmlPlexusConfiguration versionConfig = new XmlPlexusConfiguration("value");
                    versionConfig.setValue(this.httpUserAgent);
                    propertyConfig.addChild(versionConfig);
                }
                else {
                    this.getLogger().debug("User-Agent configuration found.");
                }
            }
            catch (SecurityException e) {
                this.getLogger().debug("setHttpHeaders method not accessible on wagon: " + wagon + "; skipping User-Agent configuration.");
            }
            catch (NoSuchMethodException e2) {
                this.getLogger().debug("setHttpHeaders method not found on wagon: " + wagon + "; skipping User-Agent configuration.");
            }
        }
        return config;
    }
    
    public void addConfiguration(final String repositoryId, final Xpp3Dom configuration) {
        if (repositoryId == null || configuration == null) {
            throw new IllegalArgumentException("arguments can't be null");
        }
        final XmlPlexusConfiguration xmlConf = new XmlPlexusConfiguration(configuration);
        for (int i = 0; i < configuration.getChildCount(); ++i, ++i) {
            final Xpp3Dom domChild = configuration.getChild(i);
            if ("wagonProvider".equals(domChild.getName())) {
                this.serverWagonProviderMap.put(repositoryId, domChild.getValue());
                configuration.removeChild(i);
                break;
            }
        }
        this.serverConfigurationMap.put(repositoryId, xmlConf);
    }
    
    public void setDefaultRepositoryPermissions(final RepositoryPermissions defaultRepositoryPermissions) {
        this.defaultRepositoryPermissions = defaultRepositoryPermissions;
    }
    
    public void initialize() throws InitializationException {
        if (this.httpUserAgent == null) {
            InputStream resourceAsStream = null;
            try {
                final Properties properties = new Properties();
                resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.apache.maven/maven-artifact/pom.properties");
                if (resourceAsStream != null) {
                    try {
                        properties.load(resourceAsStream);
                        this.httpUserAgent = "maven-artifact/" + properties.getProperty("version") + " (Java " + System.getProperty("java.version") + "; " + System.getProperty("os.name") + " " + System.getProperty("os.version") + ")";
                    }
                    catch (IOException e) {
                        this.getLogger().warn("Failed to load Maven artifact properties from:\nMETA-INF/maven/org.apache.maven/maven-artifact/pom.properties\n\nUser-Agent HTTP header may be incorrect for artifact resolution.");
                    }
                }
            }
            finally {
                IOUtil.close(resourceAsStream);
            }
        }
    }
    
    public void setHttpUserAgent(final String userAgent) {
        this.httpUserAgent = userAgent;
    }
    
    public String getHttpUserAgent() {
        return this.httpUserAgent;
    }
    
    static {
        DefaultWagonManager.anonymousMirrorIdSeed = 0;
    }
}
