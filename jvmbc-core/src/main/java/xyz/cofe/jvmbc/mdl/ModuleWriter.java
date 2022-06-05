package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

public interface ModuleWriter {
    public void write( ModuleVisitor v );
}
