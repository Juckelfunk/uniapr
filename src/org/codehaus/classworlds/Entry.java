// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

class Entry implements Comparable
{
    private final ClassRealm realm;
    private final String pkgName;
    
    Entry(final ClassRealm realm, final String pkgName) {
        this.realm = realm;
        this.pkgName = pkgName;
    }
    
    ClassRealm getRealm() {
        return this.realm;
    }
    
    String getPackageName() {
        return this.pkgName;
    }
    
    boolean matches(final String classname) {
        return classname.startsWith(this.getPackageName());
    }
    
    public int compareTo(final Object thatObj) {
        final Entry that = (Entry)thatObj;
        return this.getPackageName().compareTo(that.getPackageName()) * -1;
    }
    
    public boolean equals(final Object thatObj) {
        final Entry that = (Entry)thatObj;
        return this.getPackageName().equals(that.getPackageName());
    }
    
    public int hashCode() {
        return this.getPackageName().hashCode();
    }
}
