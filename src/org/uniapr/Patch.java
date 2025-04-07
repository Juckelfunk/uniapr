// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import java.util.Iterator;
import org.pitest.boot.HotSwapAgent;
import java.util.LinkedList;
import java.util.Map;
import org.uniapr.junit.runner.JUnitRunner;
import org.pitest.classinfo.ClassByteArraySource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;

public class Patch implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final PatchId id;
    public final String patchName;
    private final List<LoadedClass> patchedClasses;
    private final List<String> coveringTestNames;
    
    public Patch(final Collection<LoadedClass> patchedClasses, final Collection<String> coveringTestNames, final String patchName) {
        this.id = PatchId.alloc();
        this.patchName = patchName;
        this.patchedClasses = new ArrayList<LoadedClass>(patchedClasses);
        this.coveringTestNames = new ArrayList<String>(coveringTestNames);
    }
    
    public int test(final ClassByteArraySource byteArraySource, final ClassLoader loader, final JUnitRunner runner, final Map<String, Long> testsTiming, final long timeoutBias, final double timeoutCoefficient) throws ClassNotFoundException {
        final List<LoadedClass> originalClasses = new LinkedList<LoadedClass>();
        try {
            for (final LoadedClass patchedClass : this.patchedClasses) {
                final String className = patchedClass.getJavaName();
                final Class<?> clazz = Class.forName(className, false, loader);
                originalClasses.add(new LoadedClass(className, byteArraySource.getBytes(className).value()));
                if (!HotSwapAgent.hotSwap(clazz, patchedClass.getBytes())) {
                    throw new IllegalStateException("Unable to install the class " + className);
                }
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        final int res = runner.run(testsTiming, timeoutBias, timeoutCoefficient);
        for (final LoadedClass original : originalClasses) {
            final String className2 = original.getJavaName();
            final Class<?> clazz2 = Class.forName(className2, false, loader);
            if (!HotSwapAgent.hotSwap(clazz2, original.getBytes())) {
                throw new IllegalStateException("Unable to restore the class " + className2);
            }
        }
        return res;
    }
    
    public PatchId getId() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Patch that = (Patch)o;
        return this.id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
