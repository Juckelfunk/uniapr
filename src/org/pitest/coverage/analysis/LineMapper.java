// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import java.util.List;
import java.util.Iterator;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.reloc.asm.tree.MethodNode;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.tree.ClassNode;
import org.pitest.reloc.asm.ClassReader;
import java.util.HashMap;
import java.util.Set;
import org.pitest.coverage.BlockLocation;
import java.util.Map;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.CodeSource;
import org.pitest.coverage.LineMap;

public class LineMapper implements LineMap
{
    private final CodeSource source;
    
    public LineMapper(final CodeSource source) {
        this.source = source;
    }
    
    @Override
    public Map<BlockLocation, Set<Integer>> mapLines(final ClassName clazz) {
        final Map<BlockLocation, Set<Integer>> map = new HashMap<BlockLocation, Set<Integer>>();
        final Option<byte[]> maybeBytes = this.source.fetchClassBytes(clazz);
        for (final byte[] bytes : maybeBytes) {
            final ClassReader cr = new ClassReader(bytes);
            final ClassNode classNode = new ClassNode();
            cr.accept(classNode, 8);
            for (final Object m : classNode.methods) {
                final MethodNode mn = (MethodNode)m;
                final Location l = Location.location(clazz, MethodName.fromString(mn.name), mn.desc);
                final List<Block> blocks = ControlFlowAnalyser.analyze(mn);
                for (int i = 0; i != blocks.size(); ++i) {
                    final BlockLocation bl = new BlockLocation(l, i);
                    map.put(bl, blocks.get(i).getLines());
                }
            }
        }
        return map;
    }
}
