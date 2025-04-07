// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.tree;

import org.pitest.reloc.asm.TypePath;

public class TypeAnnotationNode extends AnnotationNode
{
    public int typeRef;
    public TypePath typePath;
    
    public TypeAnnotationNode(final int typeRef, final TypePath typePath, final String desc) {
        this(393216, typeRef, typePath, desc);
        if (this.getClass() != TypeAnnotationNode.class) {
            throw new IllegalStateException();
        }
    }
    
    public TypeAnnotationNode(final int api, final int typeRef, final TypePath typePath, final String desc) {
        super(api, desc);
        this.typeRef = typeRef;
        this.typePath = typePath;
    }
}
