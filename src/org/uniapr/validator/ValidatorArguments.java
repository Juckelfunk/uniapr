// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.validator;

import edu.utdallas.prf.reloc.apache.commons.lang3.Validate;
import java.io.Serializable;
import java.util.Map;
import org.uniapr.Patch;
import java.util.Collection;
import org.uniapr.commons.process.ChildProcessCommonArguments;

class ValidatorArguments extends ChildProcessCommonArguments
{
    private final PraPRTestComparator testComparator;
    private final Collection<Patch> patches;
    private final Map<String, Long> testsTiming;
    private final long timeoutBias;
    private final double timeoutCoefficient;
    private final boolean resetJVM;
    
    public ValidatorArguments(final PraPRTestComparator testComparator, final Collection<String> appClassNames, final Collection<Patch> patches, final Map<String, Long> testsTiming, final long timeoutBias, final double timeoutCoefficient, final boolean resetJVM) {
        super(appClassNames);
        this.testComparator = testComparator;
        Validate.isInstanceOf(Serializable.class, patches);
        Validate.isInstanceOf(Serializable.class, testsTiming);
        this.patches = patches;
        this.testsTiming = testsTiming;
        this.timeoutBias = timeoutBias;
        this.timeoutCoefficient = timeoutCoefficient;
        this.resetJVM = resetJVM;
    }
    
    public PraPRTestComparator getTestComparator() {
        return this.testComparator;
    }
    
    public Collection<Patch> getPatches() {
        return this.patches;
    }
    
    public Map<String, Long> getTestsTiming() {
        return this.testsTiming;
    }
    
    public long getTimeoutBias() {
        return this.timeoutBias;
    }
    
    public double getTimeoutCoefficient() {
        return this.timeoutCoefficient;
    }
    
    public boolean shouldResetJVM() {
        return this.resetJVM;
    }
}
