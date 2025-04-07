// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.functional.prelude.Prelude;
import org.pitest.util.Glob;
import org.pitest.functional.F;
import org.pitest.functional.predicate.Predicate;
import org.pitest.util.Preconditions;
import org.pitest.mutationtest.config.TestPluginArguments;
import java.util.Collection;
import java.io.Serializable;

public class CoverageOptions implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Collection<String> include;
    private final Collection<String> exclude;
    private final boolean verbose;
    private final TestPluginArguments pitConfig;
    private final int maxDependencyDistance;
    
    public CoverageOptions(final Collection<String> include, final Collection<String> exclude, final TestPluginArguments pitConfig, final boolean verbose, final int maxDependencyDistance) {
        Preconditions.checkNotNull(pitConfig);
        this.include = include;
        this.exclude = exclude;
        this.verbose = verbose;
        this.pitConfig = pitConfig;
        this.maxDependencyDistance = maxDependencyDistance;
    }
    
    public Predicate<String> getFilter() {
        return (Predicate<String>)Prelude.and(Prelude.or(Glob.toGlobPredicates(this.include)), Prelude.not((F<Object, Boolean>)Prelude.or(Glob.toGlobPredicates(this.exclude))), Prelude.not(commonClasses()));
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public TestPluginArguments getPitConfig() {
        return this.pitConfig;
    }
    
    public int getDependencyAnalysisMaxDistance() {
        return this.maxDependencyDistance;
    }
    
    private static F<String, Boolean> commonClasses() {
        return (F<String, Boolean>)Prelude.or(glob("java/*"), glob("sun/*"), glob("org/pitest/coverage/*"), glob("org/pitest/reloc/*"), glob("org/pitest/boot/*"));
    }
    
    private static Glob glob(final String match) {
        return new Glob(match);
    }
}
