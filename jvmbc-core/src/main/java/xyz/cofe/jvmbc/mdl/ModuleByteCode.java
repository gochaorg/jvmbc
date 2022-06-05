package xyz.cofe.jvmbc.mdl;

import xyz.cofe.jvmbc.ByteCode;

public interface ModuleByteCode extends ByteCode, ModuleWriter {
    public ModuleByteCode clone();
}
