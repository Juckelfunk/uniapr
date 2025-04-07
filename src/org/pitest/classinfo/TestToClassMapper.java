// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;

public class TestToClassMapper
{
    private static final int TEST_LENGTH;
    private final Repository repository;
    
    public TestToClassMapper(final Repository repository) {
        this.repository = repository;
    }
    
    public Option<ClassName> findTestee(final String className) {
        final ClassName name = ClassName.fromString(className);
        if (name.asJavaName().endsWith("Test") && this.tryName(name.withoutSuffixChars(TestToClassMapper.TEST_LENGTH))) {
            return Option.some(name.withoutSuffixChars(TestToClassMapper.TEST_LENGTH));
        }
        if (name.getNameWithoutPackage().asJavaName().startsWith("Test") && this.tryName(name.withoutPrefixChars(TestToClassMapper.TEST_LENGTH))) {
            return Option.some(name.withoutPrefixChars(TestToClassMapper.TEST_LENGTH));
        }
        return (Option<ClassName>)Option.none();
    }
    
    private boolean tryName(final ClassName name) {
        return this.repository.hasClass(name);
    }
    
    static {
        TEST_LENGTH = "Test".length();
    }
}
