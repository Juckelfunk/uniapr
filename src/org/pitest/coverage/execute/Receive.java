// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.pitest.testapi.Description;
import sun.pitest.CodeCoverageStore;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.util.SafeDataInputStream;
import java.util.concurrent.ConcurrentHashMap;
import org.pitest.coverage.CoverageResult;
import org.pitest.functional.SideEffect1;
import org.pitest.coverage.BlockLocation;
import org.pitest.classinfo.ClassName;
import java.util.Map;
import org.pitest.util.ReceiveStrategy;

final class Receive implements ReceiveStrategy
{
    private final Map<Integer, ClassName> classIdToName;
    private final Map<Long, BlockLocation> probeToBlock;
    private final SideEffect1<CoverageResult> handler;
    
    Receive(final SideEffect1<CoverageResult> handler) {
        this.classIdToName = new ConcurrentHashMap<Integer, ClassName>();
        this.probeToBlock = new ConcurrentHashMap<Long, BlockLocation>();
        this.handler = handler;
    }
    
    @Override
    public void apply(final byte control, final SafeDataInputStream is) {
        switch (control) {
            case 32: {
                final int id = is.readInt();
                final String name = is.readString();
                this.classIdToName.put(id, ClassName.fromString(name));
                break;
            }
            case 4: {
                this.handleProbes(is);
                break;
            }
            case 16: {
                this.handleTestEnd(is);
                break;
            }
        }
    }
    
    private void handleProbes(final SafeDataInputStream is) {
        final int classId = is.readInt();
        final String methodName = is.readString();
        final String methodSig = is.readString();
        final int first = is.readInt();
        final int last = is.readInt();
        final Location loc = Location.location(this.classIdToName.get(classId), MethodName.fromString(methodName), methodSig);
        for (int i = first; i != last + 1; ++i) {
            this.probeToBlock.put(CodeCoverageStore.encode(classId, i), new BlockLocation(loc, i - first));
        }
    }
    
    private void handleTestEnd(final SafeDataInputStream is) {
        final Description d = is.read(Description.class);
        final int numberOfResults = is.readInt();
        final Set<BlockLocation> hits = new HashSet<BlockLocation>(numberOfResults);
        for (int i = 0; i != numberOfResults; ++i) {
            this.readProbeHit(is, hits);
        }
        this.handler.apply(this.createCoverageResult(is, d, hits));
    }
    
    private void readProbeHit(final SafeDataInputStream is, final Set<BlockLocation> hits) {
        final long encoded = is.readLong();
        final BlockLocation location = this.probeToBlock(encoded);
        hits.add(location);
    }
    
    private BlockLocation probeToBlock(final long encoded) {
        return this.probeToBlock.get(encoded);
    }
    
    private CoverageResult createCoverageResult(final SafeDataInputStream is, final Description d, final Collection<BlockLocation> visitedBlocks) {
        final boolean isGreen = is.readBoolean();
        final int executionTime = is.readInt();
        final CoverageResult cr = new CoverageResult(d, executionTime, isGreen, visitedBlocks);
        return cr;
    }
}
