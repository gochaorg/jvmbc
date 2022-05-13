package xyz.cofe.jvmbc.mth;

import xyz.cofe.jvmbc.ByteCode;

public interface MethodByteCode extends ByteCode, MethodWriter {
    public MethodByteCode clone();
}
