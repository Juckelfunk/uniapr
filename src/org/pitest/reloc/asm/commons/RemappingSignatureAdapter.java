// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.signature.SignatureVisitor;

@Deprecated
public class RemappingSignatureAdapter extends SignatureVisitor
{
    private final SignatureVisitor v;
    private final Remapper remapper;
    private String className;
    
    public RemappingSignatureAdapter(final SignatureVisitor v, final Remapper remapper) {
        this(393216, v, remapper);
    }
    
    protected RemappingSignatureAdapter(final int api, final SignatureVisitor v, final Remapper remapper) {
        super(api);
        this.v = v;
        this.remapper = remapper;
    }
    
    @Override
    public void visitClassType(final String name) {
        this.className = name;
        this.v.visitClassType(this.remapper.mapType(name));
    }
    
    @Override
    public void visitInnerClassType(final String name) {
        final String remappedOuter = this.remapper.mapType(this.className) + '$';
        this.className = this.className + '$' + name;
        final String remappedName = this.remapper.mapType(this.className);
        final int index = remappedName.startsWith(remappedOuter) ? remappedOuter.length() : (remappedName.lastIndexOf(36) + 1);
        this.v.visitInnerClassType(remappedName.substring(index));
    }
    
    @Override
    public void visitFormalTypeParameter(final String name) {
        this.v.visitFormalTypeParameter(name);
    }
    
    @Override
    public void visitTypeVariable(final String name) {
        this.v.visitTypeVariable(name);
    }
    
    @Override
    public SignatureVisitor visitArrayType() {
        this.v.visitArrayType();
        return this;
    }
    
    @Override
    public void visitBaseType(final char descriptor) {
        this.v.visitBaseType(descriptor);
    }
    
    @Override
    public SignatureVisitor visitClassBound() {
        this.v.visitClassBound();
        return this;
    }
    
    @Override
    public SignatureVisitor visitExceptionType() {
        this.v.visitExceptionType();
        return this;
    }
    
    @Override
    public SignatureVisitor visitInterface() {
        this.v.visitInterface();
        return this;
    }
    
    @Override
    public SignatureVisitor visitInterfaceBound() {
        this.v.visitInterfaceBound();
        return this;
    }
    
    @Override
    public SignatureVisitor visitParameterType() {
        this.v.visitParameterType();
        return this;
    }
    
    @Override
    public SignatureVisitor visitReturnType() {
        this.v.visitReturnType();
        return this;
    }
    
    @Override
    public SignatureVisitor visitSuperclass() {
        this.v.visitSuperclass();
        return this;
    }
    
    @Override
    public void visitTypeArgument() {
        this.v.visitTypeArgument();
    }
    
    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        this.v.visitTypeArgument(wildcard);
        return this;
    }
    
    @Override
    public void visitEnd() {
        this.v.visitEnd();
    }
}
