// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import java.util.ArrayList;
import java.util.Collection;
import org.uniapr.validator.PatchValidator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.uniapr.validator.PraPRTestComparator;
import java.util.List;
import org.pitest.process.ProcessArgs;

public class PatchValidationEngine
{
    private final ProcessArgs defaultProcessArgs;
    private final List<String> appClassNames;
    private final PraPRTestComparator comparator;
    private final Map<String, Long> testsTiming;
    private final long timeoutBias;
    private final double timeoutCoefficient;
    private final Map<PatchId, Patch> patchMap;
    private final ValidationStatusTable statusTable;
    private final boolean resetJVM;
    
    private PatchValidationEngine(final ProcessArgs defaultProcessArgs, final List<String> appClassNames, final PraPRTestComparator comparator, final Map<String, Long> testsTiming, final long timeoutBias, final double timeoutCoefficient, final Map<PatchId, Patch> patchMap, final ValidationStatusTable statusTable, final boolean resetJVM) {
        this.defaultProcessArgs = defaultProcessArgs;
        this.comparator = comparator;
        this.appClassNames = appClassNames;
        this.testsTiming = testsTiming;
        this.timeoutBias = timeoutBias;
        this.timeoutCoefficient = timeoutCoefficient;
        this.patchMap = patchMap;
        this.statusTable = statusTable;
        this.resetJVM = resetJVM;
    }
    
    public static PatchValidationEngine createEngine() {
        return new PatchValidationEngine(null, null, null, null, 0L, -1.0, null, null, false);
    }
    
    public PatchValidationEngine forProcessArgs(final ProcessArgs processArgs) {
        return new PatchValidationEngine(processArgs, this.appClassNames, this.comparator, this.testsTiming, this.timeoutBias, this.timeoutCoefficient, this.patchMap, this.statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forAppClassNames(final List<String> appClassNames) {
        return new PatchValidationEngine(this.defaultProcessArgs, appClassNames, this.comparator, this.testsTiming, this.timeoutBias, this.timeoutCoefficient, this.patchMap, this.statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forComparator(final PraPRTestComparator comparator) {
        return new PatchValidationEngine(this.defaultProcessArgs, this.appClassNames, comparator, this.testsTiming, this.timeoutBias, this.timeoutCoefficient, this.patchMap, this.statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forTestsTiming(final Map<String, Long> testsTiming) {
        return new PatchValidationEngine(this.defaultProcessArgs, this.appClassNames, this.comparator, testsTiming, this.timeoutBias, this.timeoutCoefficient, this.patchMap, this.statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forTimeoutCoefficient(final double timeoutCoefficient) {
        return new PatchValidationEngine(this.defaultProcessArgs, this.appClassNames, this.comparator, this.testsTiming, this.timeoutBias, timeoutCoefficient, this.patchMap, this.statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forPatches(final List<Patch> patches) {
        final Map<PatchId, Patch> patchMap = new LinkedHashMap<PatchId, Patch>();
        final ValidationStatusTable statusTable = new ValidationStatusTable();
        for (final Patch patch : patches) {
            final PatchId id = patch.getId();
            patchMap.put(id, patch);
            statusTable.put(id, ValidationStatus.NOT_STARTED);
        }
        return new PatchValidationEngine(this.defaultProcessArgs, this.appClassNames, this.comparator, this.testsTiming, this.timeoutBias, this.timeoutCoefficient, patchMap, statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forTimeoutBias(final long timeoutBias) {
        return new PatchValidationEngine(this.defaultProcessArgs, this.appClassNames, this.comparator, this.testsTiming, timeoutBias, this.timeoutCoefficient, this.patchMap, this.statusTable, this.resetJVM);
    }
    
    public PatchValidationEngine forResetJVM(final boolean resetJVM) {
        return new PatchValidationEngine(this.defaultProcessArgs, this.appClassNames, this.comparator, this.testsTiming, this.timeoutBias, this.timeoutCoefficient, this.patchMap, this.statusTable, resetJVM);
    }
    
    public void test() {
        int iterationCount = 0;
        Collection<Patch> notStarted;
        while (!(notStarted = this.getNotStartedPatches()).isEmpty()) {
            System.out.println("Iteration #" + ++iterationCount);
            final ValidationStatusTable statusTable = PatchValidator.testPatches(this.defaultProcessArgs, this.comparator, this.appClassNames, notStarted, this.testsTiming, this.timeoutBias, this.timeoutCoefficient, this.resetJVM);
            for (final Map.Entry<PatchId, ValidationStatus> entry : statusTable.entrySet()) {
                if (entry.getValue() != ValidationStatus.NOT_STARTED) {
                    this.statusTable.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    
    private List<Patch> getNotStartedPatches() {
        final ArrayList<Patch> notStarted = new ArrayList<Patch>();
        for (final Map.Entry<PatchId, ValidationStatus> statusEntry : this.statusTable.entrySet()) {
            if (statusEntry.getValue() == ValidationStatus.NOT_STARTED) {
                notStarted.add(this.patchMap.get(statusEntry.getKey()));
            }
        }
        notStarted.trimToSize();
        return notStarted;
    }
    
    public ValidationStatusTable getStatusTable() {
        return this.statusTable;
    }
}
