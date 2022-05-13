package xyz.cofe.jvmbc.bc.fld;

import xyz.cofe.jvmbc.bc.ByteCode;

public interface FieldByteCode extends ByteCode, FieldWriter {
    public FieldByteCode clone();
}
