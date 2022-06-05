package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

public class MdlMainClass implements ModuleByteCode {
    public MdlMainClass(){}
    public MdlMainClass(String mainClass){
        this.mainClass = mainClass;
    }
    public MdlMainClass(MdlMainClass sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        mainClass = sample.mainClass;
    }

    protected String mainClass;

    public String getMainClass(){
        return mainClass;
    }

    public void setMainClass( String mainClass ){
        this.mainClass = mainClass;
    }

    @Override
    public MdlMainClass clone(){
        return new MdlMainClass(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitMainClass(mainClass);
    }
}
