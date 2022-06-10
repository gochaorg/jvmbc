package xyz.cofe.jvmbc.bm;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.ConstantDynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConstDynamic implements BootstrapMethArg {
    public ConstDynamic(){
    }

    public ConstDynamic(@NonNull ConstDynamic sample){
        //noinspection ConstantConditions
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.name;
        desc = sample.desc;
        bootstrapMethod = sample.bootstrapMethod!=null
            ? sample.bootstrapMethod.clone()
            : null;
        if( sample.args!=null ){
            args = sample.args.stream().map( a -> a!=null ? a.clone() : a ).collect(Collectors.toList());
        }
    }

    @Override
    public ConstDynamic clone(){
        return new ConstDynamic(this);
    }

    //region name : String
    private String name;
    public String getName(){
        return name;
    }
    public void setName( String name ){
        this.name = name;
    }
    //endregion
    //region desc : String
    private String desc;
    public String getDesc(){
        return desc;
    }
    public void setDesc( String desc ){
        this.desc = desc;
    }
    //endregion
    //region bootstrapMethod : MHandle
    private MHandle bootstrapMethod;
    public MHandle getBootstrapMethod(){
        return bootstrapMethod;
    }
    public void setBootstrapMethod( MHandle bootstrapMethod ){
        this.bootstrapMethod = bootstrapMethod;
    }
    //endregion
    //region args : List<BootstrapMethArg>
    private List<BootstrapMethArg> args;
    public List<BootstrapMethArg> getArgs(){
        return args;
    }
    public void setArgs( List<BootstrapMethArg> args ){
        this.args = args;
    }
    //endregion

//    public ConstantDynamic toConstantDynamic(){
//
//    }
}
