package xyz.cofe.jvmbc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Ann3 {
    int num() default 1;
}
