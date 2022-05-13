package xyz.cofe.jvmbc.bc.mth;

import xyz.cofe.jvmbc.bc.ByteCode;

public interface MethodByteCode extends ByteCode, MethodWriter {
    public MethodByteCode clone();
}
