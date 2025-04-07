// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine;

import java.util.Collection;
import java.util.List;
import org.pitest.coverage.ClassLine;
import org.pitest.classinfo.ClassName;
import org.pitest.util.StringUtil;
import org.pitest.util.Preconditions;
import org.pitest.coverage.TestInfo;
import java.util.ArrayList;
import java.io.Serializable;

public final class MutationDetails implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final MutationIdentifier id;
    private final String filename;
    private final int block;
    private final int lineNumber;
    private final String description;
    private final ArrayList<TestInfo> testsInOrder;
    private final boolean isInFinallyBlock;
    private final PoisonStatus poison;
    
    public MutationDetails(final MutationIdentifier id, final String filename, final String description, final int lineNumber, final int block) {
        this(id, filename, description, lineNumber, block, false, PoisonStatus.NORMAL);
    }
    
    public MutationDetails(final MutationIdentifier id, final String filename, final String description, final int lineNumber, final int block, final boolean isInFinallyBlock, final PoisonStatus poison) {
        this.testsInOrder = new ArrayList<TestInfo>();
        this.id = id;
        this.description = Preconditions.checkNotNull(description);
        this.filename = Preconditions.checkNotNull(filename);
        this.lineNumber = lineNumber;
        this.block = block;
        this.isInFinallyBlock = isInFinallyBlock;
        this.poison = poison;
    }
    
    @Override
    public String toString() {
        return "MutationDetails [id=" + this.id + ", filename=" + this.filename + ", block=" + this.block + ", lineNumber=" + this.lineNumber + ", description=" + this.description + ", testsInOrder=" + this.testsInOrder + ", isInFinallyBlock=" + this.isInFinallyBlock + ", poison=" + this.poison + "]";
    }
    
    public MutationDetails withDescription(final String desc) {
        return new MutationDetails(this.id, this.filename, desc, this.lineNumber, this.block, this.isInFinallyBlock, this.poison);
    }
    
    public MutationDetails withPoisonStatus(final PoisonStatus poisonStatus) {
        return new MutationDetails(this.id, this.filename, this.description, this.lineNumber, this.block, this.isInFinallyBlock, poisonStatus);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    @Deprecated
    public String getHtmlSafeDescription() {
        return StringUtil.escapeBasicHtmlChars(this.description);
    }
    
    @Deprecated
    public String getLocation() {
        return this.id.getLocation().describe();
    }
    
    public ClassName getClassName() {
        return this.id.getClassName();
    }
    
    public MethodName getMethod() {
        return this.id.getLocation().getMethodName();
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public ClassLine getClassLine() {
        return new ClassLine(this.id.getClassName(), this.lineNumber);
    }
    
    public MutationIdentifier getId() {
        return this.id;
    }
    
    public List<TestInfo> getTestsInOrder() {
        return this.testsInOrder;
    }
    
    public void addTestsInOrder(final Collection<TestInfo> testNames) {
        this.testsInOrder.addAll(testNames);
        this.testsInOrder.trimToSize();
    }
    
    public boolean mayPoisonJVM() {
        return this.poison.mayPoison();
    }
    
    public boolean isInStaticInitializer() {
        return this.poison == PoisonStatus.IS_STATIC_INITIALIZER_CODE;
    }
    
    public int getBlock() {
        return this.block;
    }
    
    public Boolean matchesId(final MutationIdentifier id) {
        return this.id.matches(id);
    }
    
    public String getMutator() {
        return this.id.getMutator();
    }
    
    public int getFirstIndex() {
        return this.id.getFirstIndex();
    }
    
    public int getInstructionIndex() {
        return this.getFirstIndex() - 1;
    }
    
    public boolean isInFinallyBlock() {
        return this.isInFinallyBlock;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MutationDetails other = (MutationDetails)obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        }
        else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
