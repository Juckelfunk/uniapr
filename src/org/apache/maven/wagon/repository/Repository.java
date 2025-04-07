// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.repository;

import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.wagon.PathUtils;
import java.util.Properties;
import java.io.Serializable;

public class Repository implements Serializable
{
    private static final long serialVersionUID = 1312227676322136247L;
    private String id;
    private String name;
    private String host;
    private int port;
    private String basedir;
    private String protocol;
    private String url;
    private RepositoryPermissions permissions;
    private Properties parameters;
    private String username;
    private String password;
    
    public Repository() {
        this.port = -1;
        this.parameters = new Properties();
        this.username = null;
        this.password = null;
    }
    
    public Repository(final String id, final String url) {
        this.port = -1;
        this.parameters = new Properties();
        this.username = null;
        this.password = null;
        if (id == null) {
            throw new NullPointerException("id can not be null");
        }
        this.setId(id);
        if (url == null) {
            throw new NullPointerException("url can not be null");
        }
        this.setUrl(url);
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getBasedir() {
        return this.basedir;
    }
    
    public void setBasedir(final String basedir) {
        this.basedir = basedir;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setUrl(final String url) {
        this.url = url;
        this.protocol = PathUtils.protocol(url);
        this.host = PathUtils.host(url);
        this.port = PathUtils.port(url);
        this.basedir = PathUtils.basedir(url);
        String username = PathUtils.user(url);
        this.username = username;
        if (username != null) {
            final String password = PathUtils.password(url);
            if (password != null) {
                this.password = password;
                username = username + ":" + password;
            }
            username += "@";
            final int index = url.indexOf(username);
            this.url = url.substring(0, index) + url.substring(index + username.length());
        }
    }
    
    public String getUrl() {
        if (this.url != null) {
            return this.url;
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(this.protocol);
        sb.append("://");
        sb.append(this.host);
        if (this.port != -1) {
            sb.append(":");
            sb.append(this.port);
        }
        sb.append(this.basedir);
        return sb.toString();
    }
    
    public String getHost() {
        if (this.host == null) {
            return "localhost";
        }
        return this.host;
    }
    
    public String getName() {
        if (this.name == null) {
            return this.getId();
        }
        return this.name;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Repository[");
        if (StringUtils.isNotEmpty(this.getName())) {
            sb.append(this.getName()).append("|");
        }
        sb.append(this.getUrl());
        sb.append("]");
        return sb.toString();
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public RepositoryPermissions getPermissions() {
        return this.permissions;
    }
    
    public void setPermissions(final RepositoryPermissions permissions) {
        this.permissions = permissions;
    }
    
    public String getParameter(final String key) {
        return this.parameters.getProperty(key);
    }
    
    public void setParameters(final Properties parameters) {
        this.parameters = parameters;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Repository other = (Repository)obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        }
        else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
}
