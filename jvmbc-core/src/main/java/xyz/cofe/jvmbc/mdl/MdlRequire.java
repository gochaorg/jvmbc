package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;
import xyz.cofe.jvmbc.AccFlagsProperty;

public class MdlRequire implements ModuleByteCode, AccFlagsProperty {
    public MdlRequire(){}
    public MdlRequire(MdlRequire sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
    }

    public MdlRequire(String module, int access, String version){
        this.module = module;
        this.access = access;
        this.version = version;
    }

    protected String module;
    public String getModule(){
        return module;
    }
    public void setModule( String module ){
        this.module = module;
    }

    protected int access;
    public int getAccess(){
        return access;
    }
    public void setAccess( int access ){
        this.access = access;
    }

    protected String version;
    public String getVersion(){
        return version;
    }
    public void setVersion( String version ){
        this.version = version;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MdlRequire clone(){
        return new MdlRequire(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitRequire(module, access, version);
    }
}
