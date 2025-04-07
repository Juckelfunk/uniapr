// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.util.Iterator;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class Timings
{
    private final Map<Stage, TimeSpan> timings;
    
    public Timings() {
        this.timings = new LinkedHashMap<Stage, TimeSpan>();
    }
    
    public void registerStart(final Stage stage) {
        this.timings.put(stage, new TimeSpan(System.currentTimeMillis(), 0L));
    }
    
    public void registerEnd(final Stage stage) {
        final long end = System.currentTimeMillis();
        this.timings.get(stage).setEnd(end);
    }
    
    public void report(final PrintStream ps) {
        long total = 0L;
        for (final Map.Entry<Stage, TimeSpan> each : this.timings.entrySet()) {
            total += each.getValue().duration();
            ps.println("> " + each.getKey() + " : " + each.getValue());
        }
        ps.println(StringUtil.separatorLine());
        ps.println("> Total  : " + new TimeSpan(0L, total));
        ps.println(StringUtil.separatorLine());
    }
    
    public enum Stage
    {
        BUILD_MUTATION_TESTS("build mutation tests"), 
        RUN_MUTATION_TESTS("run mutation analysis"), 
        SCAN_CLASS_PATH("scan classpath"), 
        COVERAGE("coverage and dependency analysis");
        
        private final String description;
        
        private Stage(final String desc) {
            this.description = desc;
        }
        
        @Override
        public String toString() {
            return this.description;
        }
    }
}
