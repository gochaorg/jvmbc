package xyz.cofe.jvmbc.bc.ann;

import xyz.cofe.jvmbc.bc.ByteCode;

public interface AnnotationByteCode extends ByteCode, AnnotationWriter {
    public AnnotationByteCode clone();
}
