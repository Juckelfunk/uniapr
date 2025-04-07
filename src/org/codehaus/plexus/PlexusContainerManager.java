// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

public interface PlexusContainerManager
{
    public static final String ROLE = ((PlexusContainerManager$1.class$org$codehaus$plexus$PlexusContainerManager == null) ? (PlexusContainerManager$1.class$org$codehaus$plexus$PlexusContainerManager = PlexusContainerManager$1.class$("org.codehaus.plexus.PlexusContainerManager")) : PlexusContainerManager$1.class$org$codehaus$plexus$PlexusContainerManager).getName();
    
    PlexusContainer[] getManagedContainers();
}
