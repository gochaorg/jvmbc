package xyz.cofe.jvmbc.cls;

import org.objectweb.asm.ClassWriter;
import xyz.cofe.jvmbc.mdl.Modulo;

import java.util.Optional;

public class CModule extends Modulo implements ClsByteCode, ModuleFlags {
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
    protected Optional<String> version = Optional.empty();

    public Optional<String> getVersion(){
        return version;
    }

    public void setVersion( Optional<String> version ){
        //noinspection OptionalAssignedToNull
        if( version==null )throw new IllegalArgumentException( "version==null" );
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

        var modv = v.visitModule(getName(), getAccess(), getVersion().orElse(null));

        //noinspection ConstantConditions
        if( modv!=null ) write(modv);
    }
}
