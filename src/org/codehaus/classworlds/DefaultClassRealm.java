// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.util.Vector;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Iterator;
import java.io.IOException;
import java.net.URLStreamHandler;
import java.io.File;
import java.net.URL;
import java.util.TreeSet;

public class DefaultClassRealm implements ClassRealm
{
    private ClassWorld world;
    private String id;
    private TreeSet imports;
    private ClassLoader foreignClassLoader;
    private RealmClassLoader classLoader;
    private ClassRealm parent;
    
    public DefaultClassRealm(final ClassWorld world, final String id) {
        this(world, id, null);
    }
    
    public DefaultClassRealm(final ClassWorld world, final String id, final ClassLoader foreignClassLoader) {
        this.world = world;
        this.id = id;
        this.imports = new TreeSet();
        if (foreignClassLoader != null) {
            this.foreignClassLoader = foreignClassLoader;
        }
        if ("true".equals(System.getProperty("classworlds.bootstrapped"))) {
            this.classLoader = new UberJarRealmClassLoader(this);
        }
        else {
            this.classLoader = new RealmClassLoader(this);
        }
    }
    
    public URL[] getConstituents() {
        return this.classLoader.getURLs();
    }
    
    public ClassRealm getParent() {
        return this.parent;
    }
    
    public void setParent(final ClassRealm parent) {
        this.parent = parent;
    }
    
    public String getId() {
        return this.id;
    }
    
    public ClassWorld getWorld() {
        return this.world;
    }
    
    public void importFrom(final String realmId, final String packageName) throws NoSuchRealmException {
        this.imports.add(new Entry(this.getWorld().getRealm(realmId), packageName));
        this.imports.add(new Entry(this.getWorld().getRealm(realmId), packageName.replace('.', '/')));
    }
    
    public void addConstituent(final URL constituent) {
        this.classLoader.addConstituent(constituent);
    }
    
    public void addConstituent(final String constituent, final byte[] b) throws ClassNotFoundException {
        try {
            File file;
            if (constituent.lastIndexOf(46) != -1) {
                final File path = new File("byteclass/" + constituent.substring(0, constituent.lastIndexOf(46) + 1).replace('.', File.separatorChar));
                file = new File(path, constituent.substring(constituent.lastIndexOf(46) + 1) + ".class");
            }
            else {
                final File path = new File("byteclass/");
                file = new File(path, constituent + ".class");
            }
            this.addConstituent(new URL(null, file.toURL().toExternalForm(), new BytesURLStreamHandler(b)));
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Couldn't load byte stream.", e);
        }
    }
    
    public ClassRealm locateSourceRealm(final String classname) {
        for (final Entry entry : this.imports) {
            if (entry.matches(classname)) {
                return entry.getRealm();
            }
        }
        return this;
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public ClassRealm createChildRealm(final String id) throws DuplicateRealmException {
        final ClassRealm childRealm = this.getWorld().newRealm(id);
        childRealm.setParent(this);
        return childRealm;
    }
    
    public Class loadClass(final String name) throws ClassNotFoundException {
        if (name.startsWith("org.codehaus.classworlds.")) {
            return this.getWorld().loadClass(name);
        }
        try {
            if (this.foreignClassLoader != null) {
                try {
                    return this.foreignClassLoader.loadClass(name);
                }
                catch (ClassNotFoundException ex) {}
            }
            final ClassRealm sourceRealm = this.locateSourceRealm(name);
            if (sourceRealm == this) {
                return this.classLoader.loadClassDirect(name);
            }
            try {
                return sourceRealm.loadClass(name);
            }
            catch (ClassNotFoundException cnfe) {
                return this.classLoader.loadClassDirect(name);
            }
        }
        catch (ClassNotFoundException e) {
            if (this.getParent() != null) {
                return this.getParent().loadClass(name);
            }
            throw e;
        }
    }
    
    public URL getResource(String name) {
        URL resource = null;
        name = UrlUtils.normalizeUrlPath(name);
        if (this.foreignClassLoader != null) {
            resource = this.foreignClassLoader.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        final ClassRealm sourceRealm = this.locateSourceRealm(name);
        if (sourceRealm == this) {
            resource = this.classLoader.getResourceDirect(name);
        }
        else {
            resource = sourceRealm.getResource(name);
            if (resource == null) {
                resource = this.classLoader.getResourceDirect(name);
            }
        }
        if (resource == null && this.getParent() != null) {
            resource = this.getParent().getResource(name);
        }
        return resource;
    }
    
    public InputStream getResourceAsStream(final String name) {
        final URL url = this.getResource(name);
        InputStream is = null;
        if (url != null) {
            try {
                is = url.openStream();
            }
            catch (IOException ex) {}
        }
        return is;
    }
    
    public Enumeration findResources(String name) throws IOException {
        name = UrlUtils.normalizeUrlPath(name);
        final Vector resources = new Vector();
        if (this.foreignClassLoader != null) {
            final Enumeration res = this.foreignClassLoader.getResources(name);
            while (res.hasMoreElements()) {
                resources.addElement(res.nextElement());
            }
        }
        final ClassRealm sourceRealm = this.locateSourceRealm(name);
        if (sourceRealm != this) {
            final Enumeration res2 = sourceRealm.findResources(name);
            while (res2.hasMoreElements()) {
                resources.addElement(res2.nextElement());
            }
        }
        final Enumeration direct = this.classLoader.findResourcesDirect(name);
        while (direct.hasMoreElements()) {
            resources.addElement(direct.nextElement());
        }
        if (this.parent != null) {
            final Enumeration parent = this.getParent().findResources(name);
            while (parent.hasMoreElements()) {
                resources.addElement(parent.nextElement());
            }
        }
        return resources.elements();
    }
    
    public void display() {
        ClassRealm cr = this;
        System.out.println("-----------------------------------------------------");
        this.showUrls(cr);
        while (cr.getParent() != null) {
            System.out.println("\n");
            cr = cr.getParent();
            this.showUrls(cr);
        }
        System.out.println("-----------------------------------------------------");
    }
    
    private void showUrls(final ClassRealm classRealm) {
        System.out.println("this realm = " + classRealm.getId());
        final URL[] urls = classRealm.getConstituents();
        for (int i = 0; i < urls.length; ++i) {
            System.out.println("urls[" + i + "] = " + urls[i]);
        }
        System.out.println("Number of imports: " + this.imports.size());
        final Iterator j = this.imports.iterator();
        while (j.hasNext()) {
            System.out.println("import: " + j.next());
        }
    }
}
