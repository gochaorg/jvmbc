package xyz.cofe.jvmbc.bc.ann;

import org.objectweb.asm.AnnotationVisitor;

public interface AnnotationWriter {
    void write(AnnotationVisitor v);
}
