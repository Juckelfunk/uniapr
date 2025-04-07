// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.net.URL;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;

public class Launcher
{
    protected static final String CLASSWORLDS_CONF = "classworlds.conf";
    protected static final String UBERJAR_CONF_DIR = "WORLDS-INF/conf/";
    protected ClassLoader systemClassLoader;
    protected String mainClassName;
    protected String mainRealmName;
    protected ClassWorld world;
    private int exitCode;
    
    public Launcher() {
        this.exitCode = 0;
    }
    
    public void setSystemClassLoader(final ClassLoader loader) {
        this.systemClassLoader = loader;
    }
    
    public ClassLoader getSystemClassLoader() {
        return this.systemClassLoader;
    }
    
    public int getExitCode() {
        return this.exitCode;
    }
    
    public void setAppMain(final String mainClassName, final String mainRealmName) {
        this.mainClassName = mainClassName;
        this.mainRealmName = mainRealmName;
    }
    
    public String getMainRealmName() {
        return this.mainRealmName;
    }
    
    public String getMainClassName() {
        return this.mainClassName;
    }
    
    public void setWorld(final ClassWorld world) {
        this.world = world;
    }
    
    public ClassWorld getWorld() {
        return this.world;
    }
    
    public void configure(final InputStream is) throws IOException, MalformedURLException, ConfigurationException, DuplicateRealmException, NoSuchRealmException {
        final Configurator configurator = new Configurator(this);
        configurator.configure(is);
    }
    
    public Class getMainClass() throws ClassNotFoundException, NoSuchRealmException {
        return this.getMainRealm().loadClass(this.getMainClassName());
    }
    
    public ClassRealm getMainRealm() throws NoSuchRealmException {
        return this.getWorld().getRealm(this.getMainRealmName());
    }
    
    protected Method getEnhancedMainMethod() throws ClassNotFoundException, NoSuchMethodException, NoSuchRealmException {
        final Method[] methods = this.getMainClass().getMethods();
        final Class cwClass = this.getMainRealm().loadClass(ClassWorld.class.getName());
        final Method m = this.getMainClass().getMethod("main", String[].class, cwClass);
        final int modifiers = m.getModifiers();
        if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && (m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE)) {
            return m;
        }
        throw new NoSuchMethodException("public static void main(String[] args, ClassWorld world)");
    }
    
    protected Method getMainMethod() throws ClassNotFoundException, NoSuchMethodException, NoSuchRealmException {
        final Method m = this.getMainClass().getMethod("main", String[].class);
        final int modifiers = m.getModifiers();
        if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && (m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE)) {
            return m;
        }
        throw new NoSuchMethodException("public static void main(String[] args) in " + this.getMainClass());
    }
    
    public void launch(final String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        try {
            this.launchEnhanced(args);
        }
        catch (NoSuchMethodException e) {
            this.launchStandard(args);
        }
    }
    
    protected void launchEnhanced(final String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        final ClassRealm mainRealm = this.getMainRealm();
        final Class mainClass = this.getMainClass();
        final Method mainMethod = this.getEnhancedMainMethod();
        final ClassLoader cl = mainRealm.getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        final Object ret = mainMethod.invoke(mainClass, args, this.getWorld());
        if (ret instanceof Integer) {
            this.exitCode = (int)ret;
        }
    }
    
    protected void launchStandard(final String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        final ClassRealm mainRealm = this.getMainRealm();
        final Class mainClass = this.getMainClass();
        final Method mainMethod = this.getMainMethod();
        Thread.currentThread().setContextClassLoader(mainRealm.getClassLoader());
        final Object ret = mainMethod.invoke(mainClass, args);
        if (ret instanceof Integer) {
            this.exitCode = (int)ret;
        }
    }
    
    public static void main(final String[] args) {
        try {
            final int exitCode = mainWithExitCode(args);
            System.exit(exitCode);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
    
    public static int mainWithExitCode(final String[] args) throws Exception {
        final String classworldsConf = System.getProperty("classworlds.conf");
        InputStream is = null;
        final Launcher launcher = new Launcher();
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        launcher.setSystemClassLoader(cl);
        if (classworldsConf != null) {
            is = new FileInputStream(classworldsConf);
        }
        else if ("true".equals(System.getProperty("classworlds.bootstrapped"))) {
            is = cl.getResourceAsStream("WORLDS-INF/conf/classworlds.conf");
        }
        else {
            is = cl.getResourceAsStream("classworlds.conf");
        }
        if (is == null) {
            throw new Exception("classworlds configuration not specified nor found in the classpath");
        }
        launcher.configure(is);
        try {
            launcher.launch(args);
        }
        catch (InvocationTargetException e) {
            final ClassRealm realm = launcher.getWorld().getRealm(launcher.getMainRealmName());
            final URL[] constituents = realm.getConstituents();
            System.out.println("---------------------------------------------------");
            for (int i = 0; i < constituents.length; ++i) {
                System.out.println("constituent[" + i + "]: " + constituents[i]);
            }
            System.out.println("---------------------------------------------------");
            final Throwable t = e.getTargetException();
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw e;
        }
        return launcher.getExitCode();
    }
}
