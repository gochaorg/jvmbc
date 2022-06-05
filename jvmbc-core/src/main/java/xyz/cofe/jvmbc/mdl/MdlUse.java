package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

public class MdlUse implements ModuleByteCode {
    public MdlUse(){}
    public MdlUse(MdlUse sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.service = sample.service;
    }
    public MdlUse(String service){
        if( service==null )throw new IllegalArgumentException( "service==null" );
    }

    protected String service;

    public String getService(){
        return service;
    }

    public void setService( String service ){
        this.service = service;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MdlUse clone(){
        return new MdlUse(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitUse(service);
    }
}
