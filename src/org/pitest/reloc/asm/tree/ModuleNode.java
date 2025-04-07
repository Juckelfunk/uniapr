// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.tree;

import org.pitest.reloc.asm.ClassVisitor;
import java.util.ArrayList;
import java.util.List;
import org.pitest.reloc.asm.ModuleVisitor;

public class ModuleNode extends ModuleVisitor
{
    public String name;
    public int access;
    public String version;
    public String mainClass;
    public List<String> packages;
    public List<ModuleRequireNode> requires;
    public List<ModuleExportNode> exports;
    public List<ModuleOpenNode> opens;
    public List<String> uses;
    public List<ModuleProvideNode> provides;
    
    public ModuleNode(final String name, final int access, final String version) {
        super(393216);
        this.name = name;
        this.access = access;
        this.version = version;
    }
    
    public ModuleNode(final int api, final String name, final int access, final String version, final List<ModuleRequireNode> requires, final List<ModuleExportNode> exports, final List<ModuleOpenNode> opens, final List<String> uses, final List<ModuleProvideNode> provides) {
        super(api);
        this.name = name;
        this.access = access;
        this.version = version;
        this.requires = requires;
        this.exports = exports;
        this.opens = opens;
        this.uses = uses;
        this.provides = provides;
        if (this.getClass() != ModuleNode.class) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public void visitMainClass(final String mainClass) {
        this.mainClass = mainClass;
    }
    
    @Override
    public void visitPackage(final String packaze) {
        if (this.packages == null) {
            this.packages = new ArrayList<String>(5);
        }
        this.packages.add(packaze);
    }
    
    @Override
    public void visitRequire(final String module, final int access, final String version) {
        if (this.requires == null) {
            this.requires = new ArrayList<ModuleRequireNode>(5);
        }
        this.requires.add(new ModuleRequireNode(module, access, version));
    }
    
    @Override
    public void visitExport(final String packaze, final int access, final String... modules) {
        if (this.exports == null) {
            this.exports = new ArrayList<ModuleExportNode>(5);
        }
        List<String> moduleList = null;
        if (modules != null) {
            moduleList = new ArrayList<String>(modules.length);
            for (int i = 0; i < modules.length; ++i) {
                moduleList.add(modules[i]);
            }
        }
        this.exports.add(new ModuleExportNode(packaze, access, moduleList));
    }
    
    @Override
    public void visitOpen(final String packaze, final int access, final String... modules) {
        if (this.opens == null) {
            this.opens = new ArrayList<ModuleOpenNode>(5);
        }
        List<String> moduleList = null;
        if (modules != null) {
            moduleList = new ArrayList<String>(modules.length);
            for (int i = 0; i < modules.length; ++i) {
                moduleList.add(modules[i]);
            }
        }
        this.opens.add(new ModuleOpenNode(packaze, access, moduleList));
    }
    
    @Override
    public void visitUse(final String service) {
        if (this.uses == null) {
            this.uses = new ArrayList<String>(5);
        }
        this.uses.add(service);
    }
    
    @Override
    public void visitProvide(final String service, final String... providers) {
        if (this.provides == null) {
            this.provides = new ArrayList<ModuleProvideNode>(5);
        }
        final ArrayList<String> providerList = new ArrayList<String>(providers.length);
        for (int i = 0; i < providers.length; ++i) {
            providerList.add(providers[i]);
        }
        this.provides.add(new ModuleProvideNode(service, providerList));
    }
    
    @Override
    public void visitEnd() {
    }
    
    public void accept(final ClassVisitor cv) {
        final ModuleVisitor mv = cv.visitModule(this.name, this.access, this.version);
        if (mv == null) {
            return;
        }
        if (this.mainClass != null) {
            mv.visitMainClass(this.mainClass);
        }
        if (this.packages != null) {
            for (int i = 0; i < this.packages.size(); ++i) {
                mv.visitPackage(this.packages.get(i));
            }
        }
        if (this.requires != null) {
            for (int i = 0; i < this.requires.size(); ++i) {
                this.requires.get(i).accept(mv);
            }
        }
        if (this.exports != null) {
            for (int i = 0; i < this.exports.size(); ++i) {
                this.exports.get(i).accept(mv);
            }
        }
        if (this.opens != null) {
            for (int i = 0; i < this.opens.size(); ++i) {
                this.opens.get(i).accept(mv);
            }
        }
        if (this.uses != null) {
            for (int i = 0; i < this.uses.size(); ++i) {
                mv.visitUse(this.uses.get(i));
            }
        }
        if (this.provides != null) {
            for (int i = 0; i < this.provides.size(); ++i) {
                this.provides.get(i).accept(mv);
            }
        }
    }
}
