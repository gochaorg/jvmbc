package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MdlExport implements ModuleByteCode {
    public MdlExport(){
    }
    public MdlExport(MdlExport sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        packaze = sample.packaze;
        access = sample.access;
        if( sample.modules!=null ){
            getModules().addAll(sample.getModules());
        }
    }
    public MdlExport(String packaze, int access, String... modules){
        this.packaze = packaze;
        this.access = access;
        if( modules!=null ){
            getModules().addAll(Arrays.asList(modules));
        }
    }

    protected String packaze;
    public String getPackaze(){
        return packaze;
    }

    public void setPackaze( String packaze ){
        this.packaze = packaze;
    }

    protected int access;
    public int getAccess(){
        return access;
    }

    public void setAccess( int access ){
        this.access = access;
    }

    protected List<String> modules;

    public List<String> getModules(){
        if( modules!=null )return modules;
        modules = new ArrayList<>();
        return modules;
    }

    public void setModules( List<String> modules ){
        this.modules = modules;
    }

    @Override
    public MdlExport clone(){
        return new MdlExport(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitExport(packaze,access,getModules().toArray(new String[0]));
    }
}
