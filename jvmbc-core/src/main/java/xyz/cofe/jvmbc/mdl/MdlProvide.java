package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MdlProvide implements ModuleByteCode {
    public MdlProvide(){}
    public MdlProvide(MdlProvide sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.service = sample.service;
        if( sample.providers!=null ){
            getProviders().addAll(sample.getProviders());
        }
    }
    public MdlProvide(String service, String... providers){
        this.service = service;
        if( providers!=null ){
            getProviders().addAll(Arrays.asList(providers));
        }
    }

    protected String service;
    public String getService(){
        return service;
    }

    public void setService( String service ){
        this.service = service;
    }

    protected List<String> providers;

    public List<String> getProviders(){
        if( providers!=null )return providers;
        providers = new ArrayList<>();
        return providers;
    }

    public void setProviders( List<String> providers ){
        this.providers = providers;
    }

    @Override
    public MdlProvide clone(){
        return new MdlProvide(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitProvide(service, getProviders().toArray(new String[0]));
    }
}
