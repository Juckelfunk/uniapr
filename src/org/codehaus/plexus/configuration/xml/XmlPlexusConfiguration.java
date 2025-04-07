// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.xml;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class XmlPlexusConfiguration implements PlexusConfiguration
{
    private Xpp3Dom dom;
    
    public XmlPlexusConfiguration(final String name) {
        this.dom = new Xpp3Dom(name);
    }
    
    public XmlPlexusConfiguration(final Xpp3Dom dom) {
        this.dom = dom;
    }
    
    public Xpp3Dom getXpp3Dom() {
        return this.dom;
    }
    
    public String getName() {
        return this.dom.getName();
    }
    
    public String getValue() {
        return this.dom.getValue();
    }
    
    public String getValue(final String defaultValue) {
        String value = this.dom.getValue();
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    public void setValue(final String value) {
        this.dom.setValue(value);
    }
    
    public void setAttribute(final String name, final String value) {
        this.dom.setAttribute(name, value);
    }
    
    public String getAttribute(final String name, final String defaultValue) {
        String attribute = this.getAttribute(name);
        if (attribute == null) {
            attribute = defaultValue;
        }
        return attribute;
    }
    
    public String getAttribute(final String name) {
        return this.dom.getAttribute(name);
    }
    
    public String[] getAttributeNames() {
        return this.dom.getAttributeNames();
    }
    
    public PlexusConfiguration getChild(final String name) {
        return this.getChild(name, true);
    }
    
    public PlexusConfiguration getChild(final int i) {
        return new XmlPlexusConfiguration(this.dom.getChild(i));
    }
    
    public PlexusConfiguration getChild(final String name, final boolean createChild) {
        Xpp3Dom child = this.dom.getChild(name);
        if (child == null) {
            if (!createChild) {
                return null;
            }
            child = new Xpp3Dom(name);
            this.dom.addChild(child);
        }
        return new XmlPlexusConfiguration(child);
    }
    
    public PlexusConfiguration[] getChildren() {
        final Xpp3Dom[] doms = this.dom.getChildren();
        final PlexusConfiguration[] children = new XmlPlexusConfiguration[doms.length];
        for (int i = 0; i < children.length; ++i) {
            children[i] = new XmlPlexusConfiguration(doms[i]);
        }
        return children;
    }
    
    public PlexusConfiguration[] getChildren(final String name) {
        final Xpp3Dom[] doms = this.dom.getChildren(name);
        final PlexusConfiguration[] children = new XmlPlexusConfiguration[doms.length];
        for (int i = 0; i < children.length; ++i) {
            children[i] = new XmlPlexusConfiguration(doms[i]);
        }
        return children;
    }
    
    public void addChild(final PlexusConfiguration configuration) {
        this.dom.addChild(((XmlPlexusConfiguration)configuration).getXpp3Dom());
    }
    
    public void addAllChildren(final PlexusConfiguration other) {
        final PlexusConfiguration[] children = other.getChildren();
        for (int i = 0; i < children.length; ++i) {
            this.addChild(children[i]);
        }
    }
    
    public int getChildCount() {
        return this.dom.getChildCount();
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final int depth = 0;
        this.display(this, sb, depth);
        return sb.toString();
    }
    
    private void display(final PlexusConfiguration c, final StringBuffer sb, int depth) {
        sb.append(this.indent(depth)).append('<').append(c.getName()).append('>').append('\n');
        for (int count = c.getChildCount(), i = 0; i < count; ++i) {
            final PlexusConfiguration child = c.getChild(i);
            final int childCount = child.getChildCount();
            ++depth;
            if (childCount > 0) {
                this.display(child, sb, depth);
            }
            else {
                final String value = child.getValue(null);
                if (value != null) {
                    sb.append(this.indent(depth)).append('<').append(child.getName());
                    this.attributes(child, sb);
                    sb.append('>').append(child.getValue(null)).append('<').append('/').append(child.getName()).append('>').append('\n');
                }
                else {
                    sb.append(this.indent(depth)).append('<').append(child.getName());
                    this.attributes(child, sb);
                    sb.append('/').append('>').append("\n");
                }
            }
            --depth;
        }
        sb.append(this.indent(depth)).append('<').append('/').append(c.getName()).append('>').append('\n');
    }
    
    private void attributes(final PlexusConfiguration c, final StringBuffer sb) {
        final String[] names = c.getAttributeNames();
        for (int i = 0; i < names.length; ++i) {
            sb.append(' ').append(names[i]).append('=').append('\"').append(c.getAttribute(names[i], null)).append('\"');
        }
    }
    
    private String indent(final int depth) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; ++i) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
