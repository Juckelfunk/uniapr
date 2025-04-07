// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.io.InputStream;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;

public class EmbeddedLauncher extends Launcher
{
    private static String LAUNCH_METHOD;
    
    public void setAppMain(final String mainClassName, final String mainRealmName) {
        super.mainClassName = mainClassName;
        super.mainRealmName = mainRealmName;
    }
    
    public String getMainRealmName() {
        return super.mainRealmName;
    }
    
    public String getMainClassName() {
        return super.mainClassName;
    }
    
    public Class getMainClass() throws ClassNotFoundException, NoSuchRealmException {
        return this.getMainRealm().loadClass(this.getMainClassName());
    }
    
    public ClassRealm getMainRealm() throws NoSuchRealmException {
        return this.getWorld().getRealm(this.getMainRealmName());
    }
    
    protected Method getEnhancedMainMethod() throws ClassNotFoundException, NoSuchMethodException, NoSuchRealmException {
        final Method[] methods = this.getMainClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (EmbeddedLauncher.LAUNCH_METHOD.equals(method.getName())) {
                final int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    if (method.getReturnType() == Void.TYPE) {
                        final Class[] paramTypes = method.getParameterTypes();
                        if (paramTypes.length == 2) {
                            if (paramTypes[0] == String[].class) {
                                if (paramTypes[1] == ClassWorld.class) {
                                    return method;
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NoSuchMethodException("public void execute(ClassWorld world)");
    }
    
    protected void launchX() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        final ClassRealm mainRealm = this.getMainRealm();
        final Class mainClass = this.getMainClass();
        final Method mainMethod = this.getEnhancedMainMethod();
        Thread.currentThread().setContextClassLoader(mainRealm.getClassLoader());
        mainMethod.invoke(mainClass, this.getWorld());
    }
    
    public void launch() throws Exception {
        final String classworldsConf = System.getProperty("classworlds.conf");
        InputStream is = null;
        if (classworldsConf != null) {
            is = new FileInputStream(classworldsConf);
        }
        else {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if ("true".equals(System.getProperty("classworlds.bootstrapped"))) {
                is = cl.getResourceAsStream("WORLDS-INF/conf/classworlds.conf");
            }
            else {
                is = cl.getResourceAsStream("classworlds.conf");
            }
        }
        if (is == null) {
            throw new Exception("classworlds configuration not specified nor found in the classpath");
        }
        this.configure(is);
        try {
            this.launchX();
        }
        catch (InvocationTargetException e) {
            final Throwable t = e.getTargetException();
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw e;
        }
    }
    
    static {
        EmbeddedLauncher.LAUNCH_METHOD = "execute";
    }
}
