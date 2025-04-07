// 
// Decompiled by Procyon v0.5.36
// 

package sun.pitest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public final class CodeCoverageStore
{
    private static final int CLASS_HIT_INDEX = 0;
    public static final String CLASS_NAME;
    public static final String PROBE_METHOD_NAME = "visitProbes";
    private static InvokeReceiver invokeQueue;
    private static int classId;
    private static final Map<Integer, boolean[]> CLASS_HITS;
    
    public static void init(final InvokeReceiver invokeQueue) {
        CodeCoverageStore.invokeQueue = invokeQueue;
    }
    
    private CodeCoverageStore() {
    }
    
    public static void visitSingleProbe(final int classId, final int probe) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[probe + 1] = (bs[0] = true);
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean[] probes) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        for (int i = 0; i != probes.length; ++i) {
            if (probes[i]) {
                bs[i + offset + 1] = true;
            }
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
        if (p9) {
            bs[offset + 10] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
        if (p9) {
            bs[offset + 10] = true;
        }
        if (p10) {
            bs[offset + 11] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final boolean p11) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
        if (p9) {
            bs[offset + 10] = true;
        }
        if (p10) {
            bs[offset + 11] = true;
        }
        if (p11) {
            bs[offset + 12] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final boolean p11, final boolean p12) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
        if (p9) {
            bs[offset + 10] = true;
        }
        if (p10) {
            bs[offset + 11] = true;
        }
        if (p11) {
            bs[offset + 12] = true;
        }
        if (p12) {
            bs[offset + 13] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final boolean p11, final boolean p12, final boolean p13) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
        if (p9) {
            bs[offset + 10] = true;
        }
        if (p10) {
            bs[offset + 11] = true;
        }
        if (p11) {
            bs[offset + 12] = true;
        }
        if (p12) {
            bs[offset + 13] = true;
        }
        if (p13) {
            bs[offset + 14] = true;
        }
    }
    
    public static void visitProbes(final int classId, final int offset, final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final boolean p9, final boolean p10, final boolean p11, final boolean p12, final boolean p13, final boolean p14) {
        final boolean[] bs = CodeCoverageStore.CLASS_HITS.get(classId);
        bs[0] = true;
        if (p0) {
            bs[offset + 1] = true;
        }
        if (p1) {
            bs[offset + 2] = true;
        }
        if (p2) {
            bs[offset + 3] = true;
        }
        if (p3) {
            bs[offset + 4] = true;
        }
        if (p4) {
            bs[offset + 5] = true;
        }
        if (p5) {
            bs[offset + 6] = true;
        }
        if (p6) {
            bs[offset + 7] = true;
        }
        if (p7) {
            bs[offset + 8] = true;
        }
        if (p8) {
            bs[offset + 9] = true;
        }
        if (p9) {
            bs[offset + 10] = true;
        }
        if (p10) {
            bs[offset + 11] = true;
        }
        if (p11) {
            bs[offset + 12] = true;
        }
        if (p12) {
            bs[offset + 13] = true;
        }
        if (p13) {
            bs[offset + 14] = true;
        }
        if (p14) {
            bs[offset + 15] = true;
        }
    }
    
    public static synchronized void reset() {
        for (final Map.Entry<Integer, boolean[]> each : CodeCoverageStore.CLASS_HITS.entrySet()) {
            CodeCoverageStore.CLASS_HITS.put(each.getKey(), new boolean[each.getValue().length]);
        }
    }
    
    public static synchronized Collection<Long> getHits() {
        final Collection<Long> blockHits = new ArrayList<Long>();
        for (final Map.Entry<Integer, boolean[]> each : CodeCoverageStore.CLASS_HITS.entrySet()) {
            final boolean[] bs = each.getValue();
            if (!bs[0]) {
                continue;
            }
            final int classId = each.getKey();
            for (int probeId = 1; probeId != bs.length; ++probeId) {
                if (bs[probeId]) {
                    blockHits.add(encode(classId, probeId - 1));
                }
            }
        }
        return blockHits;
    }
    
    public static int registerClass(final String className) {
        final int id = nextId();
        CodeCoverageStore.invokeQueue.registerClass(id, className);
        return id;
    }
    
    public static void registerMethod(final int clazz, final String methodName, final String methodDesc, final int firstProbe, final int lastProbe) {
        CodeCoverageStore.invokeQueue.registerProbes(clazz, methodName, methodDesc, firstProbe, lastProbe);
    }
    
    private static synchronized int nextId() {
        return CodeCoverageStore.classId++;
    }
    
    public static int decodeClassId(final long value) {
        return (int)(value >> 32);
    }
    
    public static int decodeLineId(final long value) {
        return (int)(value & -1L);
    }
    
    public static long encode(final int classId, final int line) {
        return (long)classId << 32 | (long)line;
    }
    
    public static void registerClassProbes(final int classId, final int probeCount) {
        CodeCoverageStore.CLASS_HITS.put(classId, new boolean[probeCount + 1]);
    }
    
    public static void resetAllStaticState() {
        CodeCoverageStore.CLASS_HITS.clear();
    }
    
    static {
        CLASS_NAME = CodeCoverageStore.class.getName().replace('.', '/');
        CodeCoverageStore.classId = 0;
        CLASS_HITS = new ConcurrentHashMap<Integer, boolean[]>();
    }
}
