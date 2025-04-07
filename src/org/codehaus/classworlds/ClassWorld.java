// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassWorld
{
    private Map realms;
    
    public ClassWorld(final String realmId, final ClassLoader classLoader) {
        this();
        try {
            this.newRealm(realmId, classLoader);
        }
        catch (DuplicateRealmException ex) {}
    }
    
    public ClassWorld() {
        this.realms = new HashMap();
    }
    
    public ClassRealm newRealm(final String id) throws DuplicateRealmException {
        return this.newRealm(id, null);
    }
    
    public ClassRealm newRealm(final String id, final ClassLoader classLoader) throws DuplicateRealmException {
        if (this.realms.containsKey(id)) {
            throw new DuplicateRealmException(this, id);
        }
        ClassRealm realm = null;
        if (classLoader != null) {
            realm = new DefaultClassRealm(this, id, classLoader);
            this.realms.put(id, realm);
        }
        else {
            realm = new DefaultClassRealm(this, id);
        }
        this.realms.put(id, realm);
        return realm;
    }
    
    public void disposeRealm(final String id) throws NoSuchRealmException {
        this.realms.remove(id);
    }
    
    public ClassRealm getRealm(final String id) throws NoSuchRealmException {
        if (this.realms.containsKey(id)) {
            return this.realms.get(id);
        }
        throw new NoSuchRealmException(this, id);
    }
    
    public Collection getRealms() {
        return this.realms.values();
    }
    
    Class loadClass(final String name) throws ClassNotFoundException {
        return this.getClass().getClassLoader().loadClass(name);
    }
}
