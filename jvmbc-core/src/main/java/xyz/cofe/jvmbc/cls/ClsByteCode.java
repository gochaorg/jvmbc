package xyz.cofe.jvmbc.cls;

import xyz.cofe.jvmbc.ByteCode;

public interface ClsByteCode extends ByteCode, ClazzWriter {
    public ClsByteCode clone();
}
