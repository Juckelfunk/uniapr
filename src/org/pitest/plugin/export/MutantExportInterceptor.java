// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.plugin.export;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;
import java.io.Writer;
import java.io.PrintWriter;
import org.objectweb.asm.util.Textifier;
import java.io.CharArrayWriter;
import org.objectweb.asm.ClassReader;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import org.pitest.mutationtest.engine.Mutant;
import java.util.List;
import java.util.ArrayList;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.classinfo.ClassName;
import java.nio.file.Path;
import org.pitest.classinfo.ClassByteArraySource;
import java.nio.file.FileSystem;
import org.pitest.mutationtest.build.MutationInterceptor;

public class MutantExportInterceptor implements MutationInterceptor
{
    private final String outDir;
    private final FileSystem fileSystem;
    private final ClassByteArraySource source;
    private Path mutantsDir;
    private ClassName currentClass;
    
    public MutantExportInterceptor(final FileSystem fileSystem, final ClassByteArraySource source, final String outDir) {
        this.fileSystem = fileSystem;
        this.outDir = outDir;
        this.source = source;
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.REPORT;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        this.currentClass = clazz.name();
        final String[] classLocation = ("export." + clazz.name().asJavaName()).split("\\.");
        final Path classDir = this.fileSystem.getPath(this.outDir, classLocation);
        this.mutantsDir = classDir.resolve("mutants");
        try {
            Files.createDirectories(this.mutantsDir, (FileAttribute<?>[])new FileAttribute[0]);
            this.writeBytecodeToDisk(this.source.getBytes(clazz.name().asJavaName()).value(), classDir);
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't create direectory for " + clazz, e);
        }
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        final List<MutationDetails> indexable = new ArrayList<MutationDetails>(mutations);
        try {
            for (int i = 0; i != indexable.size(); ++i) {
                this.exportMutantDetails(m, indexable, i);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error exporting mutants for report", ex);
        }
        return mutations;
    }
    
    private void exportMutantDetails(final Mutater m, final List<MutationDetails> indexable, final int i) throws IOException {
        final MutationDetails md = indexable.get(i);
        final Path mutantFolder = this.mutantsDir.resolve("" + i);
        Files.createDirectories(mutantFolder, (FileAttribute<?>[])new FileAttribute[0]);
        final Mutant mutant = m.getMutation(md.getId());
        this.writeMutantToDisk(mutant, mutantFolder);
        this.writeBytecodeToDisk(mutant.getBytes(), mutantFolder);
        this.writeDetailsToDisk(md, mutantFolder);
    }
    
    private void writeMutantToDisk(final Mutant mutant, final Path mutantFolder) throws IOException {
        final Path outFile = mutantFolder.resolve(this.currentClass.asJavaName() + ".class");
        Files.write(outFile, mutant.getBytes(), StandardOpenOption.CREATE);
    }
    
    private void writeBytecodeToDisk(final byte[] clazz, final Path folder) throws IOException {
        final ClassReader reader = new ClassReader(clazz);
        final CharArrayWriter buffer = new CharArrayWriter();
        reader.accept((ClassVisitor)new TraceClassVisitor((ClassVisitor)null, (Printer)new Textifier(), new PrintWriter(buffer)), 8);
        final Path outFile = folder.resolve(this.currentClass.asJavaName() + ".txt");
        Files.write(outFile, Collections.singleton(buffer.toString()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
    
    private void writeDetailsToDisk(final MutationDetails md, final Path mutantFolder) throws IOException {
        final Path outFile = mutantFolder.resolve("details.txt");
        Files.write(outFile, Collections.singleton(md.toString()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }
    
    @Override
    public void end() {
    }
}
