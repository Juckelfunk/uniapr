// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.verify;

import org.pitest.functional.SideEffect1;
import org.pitest.functional.F;
import org.pitest.help.PitHelpError;
import org.pitest.help.Help;
import java.util.Collection;
import org.pitest.classinfo.ClassInfo;
import org.pitest.functional.FCollection;
import org.pitest.classpath.CodeSource;

public class DefaultBuildVerifier implements BuildVerifier
{
    @Override
    public void verify(final CodeSource code) {
        final Collection<ClassInfo> codeClasses = FCollection.filter(code.getCode(), isNotSynthetic());
        if (this.hasMutableCode(codeClasses)) {
            this.checkAtLeastOneClassHasLineNumbers(codeClasses);
            FCollection.forEach(codeClasses, this.throwErrorIfHasNoSourceFile());
        }
    }
    
    private boolean hasMutableCode(final Collection<ClassInfo> codeClasses) {
        return !codeClasses.isEmpty() && this.hasAtLeastOneClass(codeClasses);
    }
    
    private boolean hasAtLeastOneClass(final Collection<ClassInfo> codeClasses) {
        return FCollection.contains(codeClasses, aConcreteClass());
    }
    
    private void checkAtLeastOneClassHasLineNumbers(final Collection<ClassInfo> codeClasses) {
        if (!FCollection.contains(codeClasses, aClassWithLineNumbers())) {
            throw new PitHelpError(Help.NO_LINE_NUMBERS, new Object[0]);
        }
    }
    
    private static F<ClassInfo, Boolean> aConcreteClass() {
        return new F<ClassInfo, Boolean>() {
            @Override
            public Boolean apply(final ClassInfo a) {
                return !a.isInterface();
            }
        };
    }
    
    private static F<ClassInfo, Boolean> aClassWithLineNumbers() {
        return new F<ClassInfo, Boolean>() {
            @Override
            public Boolean apply(final ClassInfo a) {
                return a.getNumberOfCodeLines() != 0;
            }
        };
    }
    
    private SideEffect1<ClassInfo> throwErrorIfHasNoSourceFile() {
        return new SideEffect1<ClassInfo>() {
            @Override
            public void apply(final ClassInfo a) {
                if (a.getSourceFileName() == null) {
                    throw new PitHelpError(Help.NO_SOURCE_FILE, new Object[] { a.getName().asJavaName() });
                }
            }
        };
    }
    
    private static F<ClassInfo, Boolean> isNotSynthetic() {
        return new F<ClassInfo, Boolean>() {
            @Override
            public Boolean apply(final ClassInfo a) {
                return !a.isSynthetic();
            }
        };
    }
}
