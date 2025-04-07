// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.boot;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class HotSwapAgent
{
    private static Instrumentation instrumentation;
    
    public static void premain(final String agentArguments, final Instrumentation inst) {
        System.out.println("Installing PIT agent");
        HotSwapAgent.instrumentation = inst;
    }
    
    public static void addTransformer(final ClassFileTransformer transformer) {
        HotSwapAgent.instrumentation.addTransformer(transformer);
    }
    
    public static void agentmain(final String agentArguments, final Instrumentation inst) throws Exception {
        HotSwapAgent.instrumentation = inst;
    }
    
    public static boolean hotSwap(final Class<?> mutateMe, final byte[] bytes) {
        final ClassDefinition[] definitions = { new ClassDefinition(mutateMe, bytes) };
        try {
            HotSwapAgent.instrumentation.redefineClasses(definitions);
            return true;
        }
        catch (ClassNotFoundException ex) {}
        catch (UnmodifiableClassException ex2) {}
        catch (VerifyError verifyError) {}
        catch (InternalError internalError) {}
        return false;
    }
}
