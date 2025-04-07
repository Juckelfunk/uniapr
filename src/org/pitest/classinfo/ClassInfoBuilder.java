// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ClassInfoBuilder
{
    int access;
    ClassIdentifier id;
    String outerClass;
    String superClass;
    String sourceFile;
    final Set<Integer> codeLines;
    final Set<String> annotations;
    final Map<ClassName, Object> classAnnotationValues;
    
    ClassInfoBuilder() {
        this.codeLines = new HashSet<Integer>();
        this.annotations = new HashSet<String>(0);
        this.classAnnotationValues = new HashMap<ClassName, Object>(0);
    }
    
    public void registerCodeLine(final int line) {
        this.codeLines.add(line);
    }
    
    public void registerAnnotation(final String annotation) {
        this.annotations.add(annotation);
    }
    
    public void registerClassAnnotationValue(final ClassName annotation, final Object value) {
        this.classAnnotationValues.put(annotation, value);
    }
}
