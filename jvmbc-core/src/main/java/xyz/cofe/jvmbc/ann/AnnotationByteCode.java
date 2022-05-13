package xyz.cofe.jvmbc.ann;

import xyz.cofe.jvmbc.ByteCode;

public interface AnnotationByteCode extends ByteCode, AnnotationWriter {
    public AnnotationByteCode clone();
}
