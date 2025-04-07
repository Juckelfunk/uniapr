// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.incremental;

import java.io.Serializable;
import org.pitest.util.Log;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import edu.utdallas.prf.reloc.apache.commons.codec.binary.Base64;
import java.io.IOException;
import org.pitest.util.Unchecked;
import org.pitest.mutationtest.MutationResult;
import java.util.Iterator;
import java.io.PrintWriter;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.classinfo.HierarchicalClassId;
import java.util.Collection;
import java.util.HashMap;
import java.io.Reader;
import org.pitest.functional.Option;
import org.pitest.mutationtest.ClassHistory;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.MutationIdentifier;
import java.util.Map;
import java.io.BufferedReader;
import java.util.logging.Logger;
import org.pitest.mutationtest.HistoryStore;

public class ObjectOutputStreamHistoryStore implements HistoryStore
{
    private static final Logger LOG;
    private final WriterFactory outputFactory;
    private final BufferedReader input;
    private final Map<MutationIdentifier, MutationStatusTestPair> previousResults;
    private final Map<ClassName, ClassHistory> previousClassPath;
    
    public ObjectOutputStreamHistoryStore(final WriterFactory output, final Option<Reader> input) {
        this.previousResults = new HashMap<MutationIdentifier, MutationStatusTestPair>();
        this.previousClassPath = new HashMap<ClassName, ClassHistory>();
        this.outputFactory = output;
        this.input = this.createReader(input);
    }
    
    private BufferedReader createReader(final Option<Reader> input) {
        if (input.hasSome()) {
            return new BufferedReader(input.value());
        }
        return null;
    }
    
    @Override
    public void recordClassPath(final Collection<HierarchicalClassId> ids, final CoverageDatabase coverageInfo) {
        final PrintWriter output = this.outputFactory.create();
        output.println(ids.size());
        for (final HierarchicalClassId each : ids) {
            final ClassHistory coverage = new ClassHistory(each, coverageInfo.getCoverageIdForClass(each.getName()).toString(16));
            output.println(this.serialize(coverage));
        }
        output.flush();
    }
    
    @Override
    public void recordResult(final MutationResult result) {
        final PrintWriter output = this.outputFactory.create();
        output.println(this.serialize(new IdResult(result.getDetails().getId(), result.getStatusTestPair())));
        output.flush();
    }
    
    @Override
    public Map<MutationIdentifier, MutationStatusTestPair> getHistoricResults() {
        return this.previousResults;
    }
    
    @Override
    public Map<ClassName, ClassHistory> getHistoricClassPath() {
        return this.previousClassPath;
    }
    
    @Override
    public void initialize() {
        if (this.input != null) {
            this.restoreClassPath();
            this.restoreResults();
            try {
                this.input.close();
            }
            catch (IOException e) {
                throw Unchecked.translateCheckedException(e);
            }
        }
    }
    
    private void restoreResults() {
        try {
            for (String line = this.input.readLine(); line != null; line = this.input.readLine()) {
                final IdResult result = this.deserialize(line, IdResult.class);
                this.previousResults.put(result.id, result.status);
            }
        }
        catch (IOException e) {
            ObjectOutputStreamHistoryStore.LOG.warning("Could not read previous results");
        }
    }
    
    private void restoreClassPath() {
        try {
            final long classPathSize = Long.valueOf(this.input.readLine());
            for (int i = 0; i != classPathSize; ++i) {
                final ClassHistory coverage = this.deserialize(this.input.readLine(), ClassHistory.class);
                this.previousClassPath.put(coverage.getName(), coverage);
            }
        }
        catch (IOException e) {
            ObjectOutputStreamHistoryStore.LOG.warning("Could not read previous classpath");
        }
    }
    
    private <T> T deserialize(final String string, final Class<T> clazz) throws IOException {
        try {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decodeBase64(string));
            final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return clazz.cast(objectInputStream.readObject());
        }
        catch (ClassNotFoundException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private <T> String serialize(final T t) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(t);
            return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    static {
        LOG = Log.getLogger();
    }
    
    private static class IdResult implements Serializable
    {
        private static final long serialVersionUID = 1L;
        final MutationIdentifier id;
        final MutationStatusTestPair status;
        
        IdResult(final MutationIdentifier id, final MutationStatusTestPair status) {
            this.id = id;
            this.status = status;
        }
    }
}
