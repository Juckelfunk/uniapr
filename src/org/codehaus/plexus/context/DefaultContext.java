// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.context;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

public class DefaultContext implements Context
{
    private static Hidden HIDDEN_MAKER;
    private Map contextData;
    private Context parent;
    private boolean readOnly;
    
    public DefaultContext(final Map contextData, final Context parent) {
        this.parent = parent;
        this.contextData = contextData;
    }
    
    public DefaultContext(final Map contextData) {
        this(contextData, null);
    }
    
    public DefaultContext(final Context parent) {
        this(new Hashtable(), parent);
    }
    
    public DefaultContext() {
        this((Context)null);
    }
    
    public boolean contains(final Object key) {
        final Object data = this.contextData.get(key);
        if (null != data) {
            return !(data instanceof Hidden);
        }
        return null != this.parent && this.parent.contains(key);
    }
    
    public Object get(final Object key) throws ContextException {
        final Object data = this.contextData.get(key);
        if (data != null) {
            if (data instanceof Hidden) {
                throw new ContextException("Unable to locate " + key);
            }
            return data;
        }
        else {
            if (this.parent == null) {
                throw new ContextException("Unable to resolve context key: " + key);
            }
            return this.parent.get(key);
        }
    }
    
    public void put(final Object key, final Object value) throws IllegalStateException {
        this.checkWriteable();
        if (null == value) {
            this.contextData.remove(key);
        }
        else {
            this.contextData.put(key, value);
        }
    }
    
    public void hide(final Object key) throws IllegalStateException {
        this.checkWriteable();
        this.contextData.put(key, DefaultContext.HIDDEN_MAKER);
    }
    
    protected Map getContextData() {
        return this.contextData;
    }
    
    protected Context getParent() {
        return this.parent;
    }
    
    public void makeReadOnly() {
        this.readOnly = true;
    }
    
    protected void checkWriteable() throws IllegalStateException {
        if (this.readOnly) {
            throw new IllegalStateException("Context is read only and can not be modified");
        }
    }
    
    static {
        DefaultContext.HIDDEN_MAKER = new Hidden();
    }
    
    private static class Hidden implements Serializable
    {
    }
}
