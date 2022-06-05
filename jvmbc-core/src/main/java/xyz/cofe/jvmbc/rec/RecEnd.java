package xyz.cofe.jvmbc.rec;

import org.objectweb.asm.RecordComponentVisitor;

public class RecEnd implements RecordByteCode {
    public RecEnd(){}
    public RecEnd(RecEnd sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
    }

    @Override
    public RecEnd clone(){
        return new RecEnd(this);
    }

    @Override
    public void write( RecordComponentVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
