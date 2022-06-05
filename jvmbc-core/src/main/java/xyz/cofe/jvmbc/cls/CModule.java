package xyz.cofe.jvmbc.cls;

import org.objectweb.asm.ClassWriter;
import xyz.cofe.jvmbc.mdl.Modulo;

public class CModule extends Modulo implements ClsByteCode {
    public CModule(){
    }

    public CModule( CModule sample ){
        super(sample);
        name = sample.name;
        access = sample.access;
        version = sample.version;
    }

    //region name : String
    protected String name;
    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }
    //endregion
    //region access : int
    protected int access;
    public int getAccess(){
        return access;
    }

    public void setAccess( int access ){
        this.access = access;
    }
    //endregion
    //region version : String
    protected String version;

    public String getVersion(){
        return version;
    }

    public void setVersion( String version ){
        this.version = version;
    }
    //endregion

    @Override
    public CModule clone(){
        return new CModule(this);
    }

    @Override
    public void write( ClassWriter v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        var modv = v.visitModule(getName(), getAccess(), getVersion());

        //noinspection ConstantConditions
        if( modv!=null ) write(modv);
    }
}
