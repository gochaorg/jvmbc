package xyz.cofe.jvmbc.fld;

import org.objectweb.asm.FieldVisitor;

public interface FieldWriter {
    void write(FieldVisitor v);
}
