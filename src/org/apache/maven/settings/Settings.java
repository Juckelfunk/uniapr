// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class Settings extends TrackableBase implements Serializable
{
    private String localRepository;
    private boolean interactiveMode;
    private boolean usePluginRegistry;
    private boolean offline;
    private List<Proxy> proxies;
    private List<Server> servers;
    private List<Mirror> mirrors;
    private List<Profile> profiles;
    private List<String> activeProfiles;
    private List<String> pluginGroups;
    private String modelEncoding;
    private Proxy activeProxy;
    private Map profileMap;
    private RuntimeInfo runtimeInfo;
    
    public Settings() {
        this.interactiveMode = true;
        this.usePluginRegistry = false;
        this.offline = false;
        this.modelEncoding = "UTF-8";
    }
    
    public void addActiveProfile(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Settings.addActiveProfiles(string) parameter must be instanceof " + String.class.getName());
        }
        this.getActiveProfiles().add(string);
    }
    
    public void addMirror(final Mirror mirror) {
        if (!(mirror instanceof Mirror)) {
            throw new ClassCastException("Settings.addMirrors(mirror) parameter must be instanceof " + Mirror.class.getName());
        }
        this.getMirrors().add(mirror);
    }
    
    public void addPluginGroup(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Settings.addPluginGroups(string) parameter must be instanceof " + String.class.getName());
        }
        this.getPluginGroups().add(string);
    }
    
    public void addProfile(final Profile profile) {
        if (!(profile instanceof Profile)) {
            throw new ClassCastException("Settings.addProfiles(profile) parameter must be instanceof " + Profile.class.getName());
        }
        this.getProfiles().add(profile);
    }
    
    public void addProxy(final Proxy proxy) {
        if (!(proxy instanceof Proxy)) {
            throw new ClassCastException("Settings.addProxies(proxy) parameter must be instanceof " + Proxy.class.getName());
        }
        this.getProxies().add(proxy);
    }
    
    public void addServer(final Server server) {
        if (!(server instanceof Server)) {
            throw new ClassCastException("Settings.addServers(server) parameter must be instanceof " + Server.class.getName());
        }
        this.getServers().add(server);
    }
    
    public List<String> getActiveProfiles() {
        if (this.activeProfiles == null) {
            this.activeProfiles = new ArrayList<String>();
        }
        return this.activeProfiles;
    }
    
    public String getLocalRepository() {
        return this.localRepository;
    }
    
    public List<Mirror> getMirrors() {
        if (this.mirrors == null) {
            this.mirrors = new ArrayList<Mirror>();
        }
        return this.mirrors;
    }
    
    public String getModelEncoding() {
        return this.modelEncoding;
    }
    
    public List<String> getPluginGroups() {
        if (this.pluginGroups == null) {
            this.pluginGroups = new ArrayList<String>();
        }
        return this.pluginGroups;
    }
    
    public List<Profile> getProfiles() {
        if (this.profiles == null) {
            this.profiles = new ArrayList<Profile>();
        }
        return this.profiles;
    }
    
    public List<Proxy> getProxies() {
        if (this.proxies == null) {
            this.proxies = new ArrayList<Proxy>();
        }
        return this.proxies;
    }
    
    public List<Server> getServers() {
        if (this.servers == null) {
            this.servers = new ArrayList<Server>();
        }
        return this.servers;
    }
    
    public boolean isInteractiveMode() {
        return this.interactiveMode;
    }
    
    public boolean isOffline() {
        return this.offline;
    }
    
    public boolean isUsePluginRegistry() {
        return this.usePluginRegistry;
    }
    
    public void removeActiveProfile(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Settings.removeActiveProfiles(string) parameter must be instanceof " + String.class.getName());
        }
        this.getActiveProfiles().remove(string);
    }
    
    public void removeMirror(final Mirror mirror) {
        if (!(mirror instanceof Mirror)) {
            throw new ClassCastException("Settings.removeMirrors(mirror) parameter must be instanceof " + Mirror.class.getName());
        }
        this.getMirrors().remove(mirror);
    }
    
    public void removePluginGroup(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Settings.removePluginGroups(string) parameter must be instanceof " + String.class.getName());
        }
        this.getPluginGroups().remove(string);
    }
    
    public void removeProfile(final Profile profile) {
        if (!(profile instanceof Profile)) {
            throw new ClassCastException("Settings.removeProfiles(profile) parameter must be instanceof " + Profile.class.getName());
        }
        this.getProfiles().remove(profile);
    }
    
    public void removeProxy(final Proxy proxy) {
        if (!(proxy instanceof Proxy)) {
            throw new ClassCastException("Settings.removeProxies(proxy) parameter must be instanceof " + Proxy.class.getName());
        }
        this.getProxies().remove(proxy);
    }
    
    public void removeServer(final Server server) {
        if (!(server instanceof Server)) {
            throw new ClassCastException("Settings.removeServers(server) parameter must be instanceof " + Server.class.getName());
        }
        this.getServers().remove(server);
    }
    
    public void setActiveProfiles(final List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }
    
    public void setInteractiveMode(final boolean interactiveMode) {
        this.interactiveMode = interactiveMode;
    }
    
    public void setLocalRepository(final String localRepository) {
        this.localRepository = localRepository;
    }
    
    public void setMirrors(final List<Mirror> mirrors) {
        this.mirrors = mirrors;
    }
    
    public void setModelEncoding(final String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }
    
    public void setOffline(final boolean offline) {
        this.offline = offline;
    }
    
    public void setPluginGroups(final List<String> pluginGroups) {
        this.pluginGroups = pluginGroups;
    }
    
    public void setProfiles(final List<Profile> profiles) {
        this.profiles = profiles;
    }
    
    public void setProxies(final List<Proxy> proxies) {
        this.proxies = proxies;
    }
    
    public void setServers(final List<Server> servers) {
        this.servers = servers;
    }
    
    public void setUsePluginRegistry(final boolean usePluginRegistry) {
        this.usePluginRegistry = usePluginRegistry;
    }
    
    public Boolean getInteractiveMode() {
        return this.isInteractiveMode();
    }
    
    public void flushActiveProxy() {
        this.activeProxy = null;
    }
    
    public synchronized Proxy getActiveProxy() {
        if (this.activeProxy == null) {
            final List proxies = this.getProxies();
            if (proxies != null && !proxies.isEmpty()) {
                for (final Proxy proxy : proxies) {
                    if (proxy.isActive()) {
                        this.activeProxy = proxy;
                        break;
                    }
                }
            }
        }
        return this.activeProxy;
    }
    
    public Server getServer(final String serverId) {
        Server match = null;
        final List servers = this.getServers();
        if (servers != null && serverId != null) {
            for (final Server server : servers) {
                if (serverId.equals(server.getId())) {
                    match = server;
                    break;
                }
            }
        }
        return match;
    }
    
    @Deprecated
    public Mirror getMirrorOf(final String repositoryId) {
        Mirror match = null;
        final List mirrors = this.getMirrors();
        if (mirrors != null && repositoryId != null) {
            for (final Mirror mirror : mirrors) {
                if (repositoryId.equals(mirror.getMirrorOf())) {
                    match = mirror;
                    break;
                }
            }
        }
        return match;
    }
    
    public void flushProfileMap() {
        this.profileMap = null;
    }
    
    public Map getProfilesAsMap() {
        if (this.profileMap == null) {
            this.profileMap = new LinkedHashMap();
            if (this.getProfiles() != null) {
                for (final Profile profile : this.getProfiles()) {
                    this.profileMap.put(profile.getId(), profile);
                }
            }
        }
        return this.profileMap;
    }
    
    public void setRuntimeInfo(final RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }
    
    public RuntimeInfo getRuntimeInfo() {
        return this.runtimeInfo;
    }
}
