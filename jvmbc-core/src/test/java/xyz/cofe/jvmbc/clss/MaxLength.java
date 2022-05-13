package xyz.cofe.jvmbc.clss;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MaxLength {
    int value();
}
