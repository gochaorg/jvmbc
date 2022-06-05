package xyz.cofe.jvmbc.rec;

import org.objectweb.asm.RecordComponentVisitor;

public interface RecordWriter {
    void write( RecordComponentVisitor v );
}
