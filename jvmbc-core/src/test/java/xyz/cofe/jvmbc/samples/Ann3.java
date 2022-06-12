package xyz.cofe.jvmbc.samples;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Ann3 {
    int num() default 1;
}
