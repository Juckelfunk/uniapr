// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds.uberjar.boot;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Bootstrapper
{
    public static final String LAUNCHER_CLASS_NAME = "org.codehaus.classworlds.Launcher";
    private String[] args;
    private InitialClassLoader classLoader;
    
    public static void main(final String[] args) throws Exception {
        System.setProperty("java.protocol.handler.pkgs", "org.codehaus.classworlds.uberjar.protocol");
        final Bootstrapper bootstrapper = new Bootstrapper(args);
        bootstrapper.bootstrap();
    }
    
    public Bootstrapper(final String[] args) throws Exception {
        this.args = args;
        this.classLoader = new InitialClassLoader();
    }
    
    protected ClassLoader getInitialClassLoader() {
        return this.classLoader;
    }
    
    public void bootstrap() throws Exception {
        final ClassLoader cl = this.getInitialClassLoader();
        final Class launcherClass = cl.loadClass("org.codehaus.classworlds.Launcher");
        final Method[] methods = launcherClass.getMethods();
        Method mainMethod = null;
        for (int i = 0; i < methods.length; ++i) {
            if ("main".equals(methods[i].getName())) {
                final int modifiers = methods[i].getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    if (Modifier.isPublic(modifiers)) {
                        if (methods[i].getReturnType() == Void.TYPE) {
                            final Class[] paramTypes = methods[i].getParameterTypes();
                            if (paramTypes.length == 1) {
                                if (paramTypes[0] == String[].class) {
                                    mainMethod = methods[i];
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (mainMethod == null) {
            throw new NoSuchMethodException("org.codehaus.classworlds.Launcher::main(String[] args)");
        }
        System.setProperty("classworlds.bootstrapped", "true");
        mainMethod.invoke(launcherClass, this.args);
    }
}
