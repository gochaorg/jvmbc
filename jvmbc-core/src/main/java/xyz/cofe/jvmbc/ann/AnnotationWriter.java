package xyz.cofe.jvmbc.ann;

import org.objectweb.asm.AnnotationVisitor;

public interface AnnotationWriter {
    void write(AnnotationVisitor v);
}
