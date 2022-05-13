package xyz.cofe.jvmbc.bc.mth;

import org.objectweb.asm.MethodVisitor;

public interface MethodWriter {
    void write(MethodVisitor v, MethodWriterCtx ctx);
}
