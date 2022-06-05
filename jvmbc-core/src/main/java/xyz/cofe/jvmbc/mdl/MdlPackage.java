package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

public class MdlPackage implements ModuleByteCode {
    public MdlPackage(){}
    public MdlPackage(String packaze){}
    public MdlPackage(MdlPackage sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        packaze = sample.packaze;
    }

    //region packaze : String
    protected String packaze;

    public String getPackaze(){
        return packaze;
    }

    public void setPackaze( String packaze ){
        this.packaze = packaze;
    }
    //endregion

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MdlPackage clone(){
        return new MdlPackage(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitPackage(packaze);
    }
}
