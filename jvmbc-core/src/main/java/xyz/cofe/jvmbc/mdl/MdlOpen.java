package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MdlOpen implements ModuleByteCode {
    public MdlOpen(){}
    public MdlOpen(MdlOpen sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.packaze = sample.packaze;
        this.access = sample.access;
        if( sample.modules!=null ){
            modules = new ArrayList<>(sample.modules);
        }
    }
    public MdlOpen(String packaze, int access, String... modules){
        this.packaze = packaze;
        this.access = access;
        if( modules!=null ){
            this.modules = new ArrayList<>();
            this.modules.addAll(Arrays.asList(modules));
        }
    }

    protected String packaze;
    public String getPackaze(){
        return packaze;
    }

    public void setPackaze( String packaze ){
        this.packaze = packaze;
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

    protected int access;

    public int getAccess(){
        return access;
    }

    public void setAccess( int access ){
        this.access = access;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MdlOpen clone(){
        return new MdlOpen(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitOpen(packaze, access, getModules().toArray(new String[]{}));
    }
}
