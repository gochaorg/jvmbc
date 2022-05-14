package xyz.cofe.jvmbc.fld;

import xyz.cofe.jvmbc.ByteCode;

public interface FieldByteCode extends ByteCode, FieldWriter {
    public FieldByteCode clone();
}
