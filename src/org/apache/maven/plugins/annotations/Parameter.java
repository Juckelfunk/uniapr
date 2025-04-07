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
@Target({ ElementType.FIELD })
@Inherited
public @interface Parameter {
    String name() default "";
    
    String alias() default "";
    
    String property() default "";
    
    String defaultValue() default "";
    
    boolean required() default false;
    
    boolean readonly() default false;
}
