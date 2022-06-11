package xyz.cofe.jvmbc.mth.bm;

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

    public ConstDynamic(@NonNull ConstantDynamic constantDynamic){
        //noinspection ConstantConditions
        if( constantDynamic==null )throw new IllegalArgumentException( "constantDynamic==null" );
        name = constantDynamic.getName();
        desc = constantDynamic.getDescriptor();

        var bm = constantDynamic.getBootstrapMethod();
        bootstrapMethod = bm!=null ? new MethodHandle(bm) : null;

        args = new ArrayList<>();
        for( var ai=0;ai<constantDynamic.getBootstrapMethodArgumentCount();ai++ ){
            var a = constantDynamic.getBootstrapMethodArgument(ai);
            args.add(
                BootstrapMethArg
                    .from(a)
                    .orRuntimeError( (err)-> new IllegalArgumentException("ConstDynamic"))
            );
        }
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
    private MethodHandle bootstrapMethod;
    public MethodHandle getBootstrapMethod(){
        return bootstrapMethod;
    }
    public void setBootstrapMethod( MethodHandle bootstrapMethod ){
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

    public ConstantDynamic toConstantDynamic(){
        return new ConstantDynamic(
            getName(),
            getDesc(),
            bootstrapMethod!=null ? bootstrapMethod.toHandle() : null,
            args == null ? null :
                args.stream().map( arg -> arg.toAsmValue() ).toArray()
        );
    }

    @Override
    public Object toAsmValue(){
        return toConstantDynamic();
    }
}
