package xyz.cofe.jvmbc.rec;

import xyz.cofe.jvmbc.ByteCode;

public interface RecordByteCode extends ByteCode, RecordWriter {
    public RecordByteCode clone();
}
