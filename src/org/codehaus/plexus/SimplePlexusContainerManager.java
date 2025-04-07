// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

import java.util.Hashtable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import java.util.Iterator;
import java.io.Reader;
import java.io.InputStream;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import java.io.InputStreamReader;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;
import java.util.Properties;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

public class SimplePlexusContainerManager implements PlexusContainerManager, Contextualizable, Initializable, Startable
{
    private PlexusContainer parentPlexus;
    private DefaultPlexusContainer myPlexus;
    private String plexusConfig;
    private Properties contextValues;
    
    public void contextualize(final Context context) throws ContextException {
        this.parentPlexus = (PlexusContainer)context.get("plexus");
    }
    
    public void initialize() throws InitializationException {
        (this.myPlexus = new DefaultPlexusContainer()).setParentPlexusContainer(this.parentPlexus);
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final InputStream stream = loader.getResourceAsStream(this.plexusConfig);
        final Reader r = new InputStreamReader(stream);
        try {
            this.myPlexus.setConfigurationResource(r);
        }
        catch (PlexusConfigurationResourceException e) {
            throw new InitializationException("Unable to initialize container configuration", e);
        }
        if (this.contextValues != null) {
            for (final String name : ((Hashtable<Object, V>)this.contextValues).keySet()) {
                this.myPlexus.addContextValue(name, this.contextValues.getProperty(name));
            }
        }
        try {
            this.myPlexus.initialize();
        }
        catch (PlexusContainerException e2) {
            throw new InitializationException("Error initializing container", e2);
        }
    }
    
    public void start() throws StartingException {
        try {
            this.myPlexus.start();
        }
        catch (PlexusContainerException e) {
            throw new StartingException("Error starting container", e);
        }
    }
    
    public void stop() {
        this.myPlexus.dispose();
    }
    
    public PlexusContainer[] getManagedContainers() {
        return new PlexusContainer[] { this.myPlexus };
    }
}
