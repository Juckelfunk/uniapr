// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

import org.apache.maven.wagon.repository.RepositoryPermissions;
import org.codehaus.plexus.PlexusContainer;
import java.util.Collection;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import java.io.File;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;

public interface WagonManager
{
    public static final String ROLE = WagonManager.class.getName();
    
    @Deprecated
    Wagon getWagon(final String p0) throws UnsupportedProtocolException;
    
    Wagon getWagon(final Repository p0) throws UnsupportedProtocolException, WagonConfigurationException;
    
    void getArtifact(final Artifact p0, final List p1) throws TransferFailedException, ResourceDoesNotExistException;
    
    void getArtifact(final Artifact p0, final ArtifactRepository p1) throws TransferFailedException, ResourceDoesNotExistException;
    
    void putArtifact(final File p0, final Artifact p1, final ArtifactRepository p2) throws TransferFailedException;
    
    void putArtifactMetadata(final File p0, final ArtifactMetadata p1, final ArtifactRepository p2) throws TransferFailedException;
    
    void getArtifactMetadata(final ArtifactMetadata p0, final ArtifactRepository p1, final File p2, final String p3) throws TransferFailedException, ResourceDoesNotExistException;
    
    void getArtifactMetadataFromDeploymentRepository(final ArtifactMetadata p0, final ArtifactRepository p1, final File p2, final String p3) throws TransferFailedException, ResourceDoesNotExistException;
    
    void setOnline(final boolean p0);
    
    boolean isOnline();
    
    void addProxy(final String p0, final String p1, final int p2, final String p3, final String p4, final String p5);
    
    void addAuthenticationInfo(final String p0, final String p1, final String p2, final String p3, final String p4);
    
    void addMirror(final String p0, final String p1, final String p2);
    
    void setDownloadMonitor(final TransferListener p0);
    
    void addPermissionInfo(final String p0, final String p1, final String p2);
    
    ProxyInfo getProxy(final String p0);
    
    AuthenticationInfo getAuthenticationInfo(final String p0);
    
    void addConfiguration(final String p0, final Xpp3Dom p1);
    
    void setInteractive(final boolean p0);
    
    void registerWagons(final Collection p0, final PlexusContainer p1);
    
    void setDefaultRepositoryPermissions(final RepositoryPermissions p0);
    
    ArtifactRepository getMirrorRepository(final ArtifactRepository p0);
}
