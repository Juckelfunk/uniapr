// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.validation;

import org.apache.maven.model.Model;

public interface ModelValidator
{
    public static final String ROLE = ModelValidator.class.getName();
    
    ModelValidationResult validate(final Model p0);
}
