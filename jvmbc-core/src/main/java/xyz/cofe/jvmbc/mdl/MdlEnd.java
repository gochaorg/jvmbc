package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

public class MdlEnd implements ModuleByteCode {
    public MdlEnd(){}

    public MdlEnd(MdlEnd sample){
        if( sample!=null )throw new IllegalArgumentException( "sample!=null" );
    }

    @SuppressWarnings({"ConstantConditions", "MethodDoesntCallSuperMethod"})
    @Override
    public ModuleByteCode clone(){
        return new MdlEnd(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnd();
    }
}
