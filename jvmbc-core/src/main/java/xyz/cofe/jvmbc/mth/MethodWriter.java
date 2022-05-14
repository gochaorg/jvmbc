package xyz.cofe.jvmbc.mth;

import org.objectweb.asm.MethodVisitor;

public interface MethodWriter {
    void write(MethodVisitor v, MethodWriterCtx ctx);
}
