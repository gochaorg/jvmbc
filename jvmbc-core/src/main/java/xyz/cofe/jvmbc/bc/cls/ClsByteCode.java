package xyz.cofe.jvmbc.bc.cls;

import xyz.cofe.jvmbc.bc.ByteCode;

public interface ClsByteCode extends ByteCode, ClazzWriter {
    public ClsByteCode clone();
}
