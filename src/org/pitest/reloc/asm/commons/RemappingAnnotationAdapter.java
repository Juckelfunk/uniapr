// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.AnnotationVisitor;

@Deprecated
public class RemappingAnnotationAdapter extends AnnotationVisitor
{
    protected final Remapper remapper;
    
    public RemappingAnnotationAdapter(final AnnotationVisitor av, final Remapper remapper) {
        this(393216, av, remapper);
    }
    
    protected RemappingAnnotationAdapter(final int api, final AnnotationVisitor av, final Remapper remapper) {
        super(api, av);
        this.remapper = remapper;
    }
    
    @Override
    public void visit(final String name, final Object value) {
        this.av.visit(name, this.remapper.mapValue(value));
    }
    
    @Override
    public void visitEnum(final String name, final String desc, final String value) {
        this.av.visitEnum(name, this.remapper.mapDesc(desc), value);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        final AnnotationVisitor v = this.av.visitAnnotation(name, this.remapper.mapDesc(desc));
        return (v == null) ? null : ((v == this.av) ? this : new RemappingAnnotationAdapter(v, this.remapper));
    }
    
    @Override
    public AnnotationVisitor visitArray(final String name) {
        final AnnotationVisitor v = this.av.visitArray(name);
        return (v == null) ? null : ((v == this.av) ? this : new RemappingAnnotationAdapter(v, this.remapper));
    }
}
