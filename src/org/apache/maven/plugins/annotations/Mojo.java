// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugins.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE })
@Inherited
public @interface Mojo {
    String name();
    
    LifecyclePhase defaultPhase() default LifecyclePhase.NONE;
    
    ResolutionScope requiresDependencyResolution() default ResolutionScope.NONE;
    
    ResolutionScope requiresDependencyCollection() default ResolutionScope.NONE;
    
    InstantiationStrategy instantiationStrategy() default InstantiationStrategy.PER_LOOKUP;
    
    String executionStrategy() default "once-per-session";
    
    boolean requiresProject() default true;
    
    boolean requiresReports() default false;
    
    boolean aggregator() default false;
    
    boolean requiresDirectInvocation() default false;
    
    boolean requiresOnline() default false;
    
    boolean inheritByDefault() default true;
    
    String configurator() default "";
    
    boolean threadSafe() default false;
}
