// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.ModuleVisitor;

public class ModuleRemapper extends ModuleVisitor
{
    private final Remapper remapper;
    
    public ModuleRemapper(final ModuleVisitor mv, final Remapper remapper) {
        this(393216, mv, remapper);
    }
    
    protected ModuleRemapper(final int api, final ModuleVisitor mv, final Remapper remapper) {
        super(api, mv);
        this.remapper = remapper;
    }
    
    @Override
    public void visitMainClass(final String mainClass) {
        super.visitMainClass(this.remapper.mapType(mainClass));
    }
    
    @Override
    public void visitPackage(final String packaze) {
        super.visitPackage(this.remapper.mapPackageName(packaze));
    }
    
    @Override
    public void visitRequire(final String module, final int access, final String version) {
        super.visitRequire(this.remapper.mapModuleName(module), access, version);
    }
    
    @Override
    public void visitExport(final String packaze, final int access, final String... modules) {
        String[] newModules = null;
        if (modules != null) {
            newModules = new String[modules.length];
            for (int i = 0; i < modules.length; ++i) {
                newModules[i] = this.remapper.mapModuleName(modules[i]);
            }
        }
        super.visitExport(this.remapper.mapPackageName(packaze), access, newModules);
    }
    
    @Override
    public void visitOpen(final String packaze, final int access, final String... modules) {
        String[] newModules = null;
        if (modules != null) {
            newModules = new String[modules.length];
            for (int i = 0; i < modules.length; ++i) {
                newModules[i] = this.remapper.mapModuleName(modules[i]);
            }
        }
        super.visitOpen(this.remapper.mapPackageName(packaze), access, newModules);
    }
    
    @Override
    public void visitUse(final String service) {
        super.visitUse(this.remapper.mapType(service));
    }
    
    @Override
    public void visitProvide(final String service, final String... providers) {
        final String[] newProviders = new String[providers.length];
        for (int i = 0; i < providers.length; ++i) {
            newProviders[i] = this.remapper.mapType(providers[i]);
        }
        super.visitProvide(this.remapper.mapType(service), newProviders);
    }
}
