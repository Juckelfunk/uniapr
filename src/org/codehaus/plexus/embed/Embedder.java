// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.embed;

import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.PlexusContainerException;
import java.util.Iterator;
import java.util.Set;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.PropertyUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.DefaultPlexusContainer;
import java.util.Properties;
import java.io.Reader;

public class Embedder implements PlexusEmbedder
{
    private Reader configurationReader;
    private Properties properties;
    private final DefaultPlexusContainer container;
    private boolean embedderStarted;
    private boolean embedderStopped;
    
    public Embedder() {
        this.embedderStarted = false;
        this.embedderStopped = false;
        this.container = new DefaultPlexusContainer();
    }
    
    public synchronized PlexusContainer getContainer() {
        if (!this.embedderStarted) {
            throw new IllegalStateException("Embedder must be started");
        }
        return this.container;
    }
    
    public Object lookup(final String role) throws ComponentLookupException {
        return this.getContainer().lookup(role);
    }
    
    public Object lookup(final String role, final String id) throws ComponentLookupException {
        return this.getContainer().lookup(role, id);
    }
    
    public boolean hasComponent(final String role) {
        return this.getContainer().hasComponent(role);
    }
    
    public boolean hasComponent(final String role, final String id) {
        return this.getContainer().hasComponent(role, id);
    }
    
    public void release(final Object service) throws ComponentLifecycleException {
        this.getContainer().release(service);
    }
    
    public synchronized void setClassWorld(final ClassWorld classWorld) {
        this.container.setClassWorld(classWorld);
    }
    
    public synchronized void setConfiguration(final URL configuration) throws IOException {
        if (this.embedderStarted || this.embedderStopped) {
            throw new IllegalStateException("Embedder has already been started");
        }
        this.configurationReader = new InputStreamReader(configuration.openStream());
    }
    
    public synchronized void setConfiguration(final Reader configuration) throws IOException {
        if (this.embedderStarted || this.embedderStopped) {
            throw new IllegalStateException("Embedder has already been started");
        }
        this.configurationReader = configuration;
    }
    
    public synchronized void addContextValue(final Object key, final Object value) {
        if (this.embedderStarted || this.embedderStopped) {
            throw new IllegalStateException("Embedder has already been started");
        }
        this.container.addContextValue(key, value);
    }
    
    public synchronized void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public synchronized void setProperties(final File file) {
        this.properties = PropertyUtils.loadProperties(file);
    }
    
    public void setLoggerManager(final LoggerManager loggerManager) {
        this.container.setLoggerManager(loggerManager);
    }
    
    protected synchronized void initializeContext() {
        final Set keys = this.properties.keySet();
        for (final String key : keys) {
            final String value = this.properties.getProperty(key);
            this.container.addContextValue(key, value);
        }
    }
    
    public synchronized void start(final ClassWorld classWorld) throws PlexusContainerException {
        this.container.setClassWorld(classWorld);
        this.start();
    }
    
    public synchronized void start() throws PlexusContainerException {
        if (this.embedderStarted) {
            throw new IllegalStateException("Embedder already started");
        }
        if (this.embedderStopped) {
            throw new IllegalStateException("Embedder cannot be restarted");
        }
        if (this.configurationReader != null) {
            try {
                this.container.setConfigurationResource(this.configurationReader);
            }
            catch (PlexusConfigurationResourceException e) {
                throw new PlexusContainerException("Error loading from configuration reader", e);
            }
        }
        if (this.properties != null) {
            this.initializeContext();
        }
        this.container.initialize();
        this.embedderStarted = true;
        this.container.start();
    }
    
    public synchronized void stop() {
        if (this.embedderStopped) {
            throw new IllegalStateException("Embedder already stopped");
        }
        if (!this.embedderStarted) {
            throw new IllegalStateException("Embedder not started");
        }
        this.container.dispose();
        this.embedderStarted = false;
        this.embedderStopped = true;
    }
}
