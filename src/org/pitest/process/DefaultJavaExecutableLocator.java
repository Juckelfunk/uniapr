// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.process;

public class DefaultJavaExecutableLocator implements JavaExecutableLocator
{
    @Override
    public String javaExecutable() {
        final String separator = System.getProperty("file.separator");
        return System.getProperty("java.home") + separator + "bin" + separator + "java";
    }
}
