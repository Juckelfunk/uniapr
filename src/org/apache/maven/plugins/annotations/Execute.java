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
public @interface Execute {
    LifecyclePhase phase() default LifecyclePhase.NONE;
    
    String goal() default "";
    
    String lifecycle() default "";
}
