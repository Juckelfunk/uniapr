// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.logging;

import org.objectweb.asm.Label;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.Set;
import org.objectweb.asm.MethodVisitor;

class LoggingLineScanner extends MethodVisitor
{
    private final Set<Integer> lines;
    private final Set<String> loggingClasses;
    private int currentLineNumber;
    
    LoggingLineScanner(final Set<Integer> lines, final Set<String> loggingClasses) {
        super(393216);
        this.lines = lines;
        this.loggingClasses = loggingClasses;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (FCollection.contains(this.loggingClasses, matches(owner))) {
            this.lines.add(this.currentLineNumber);
        }
    }
    
    private static F<String, Boolean> matches(final String owner) {
        return new F<String, Boolean>() {
            @Override
            public Boolean apply(final String a) {
                return owner.startsWith(a);
            }
        };
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.currentLineNumber = line;
    }
}
