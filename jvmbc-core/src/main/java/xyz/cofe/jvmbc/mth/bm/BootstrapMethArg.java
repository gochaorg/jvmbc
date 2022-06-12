package xyz.cofe.jvmbc.mth.bm;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import xyz.cofe.jvmbc.fn.Either;

import java.io.Serializable;

/**
 * Аргумент bootstrap метода
 * см <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/LambdaMetafactory.html">LambdaMetafactory</a>
 * <a href="https://alex-ber.medium.com/explaining-invokedynamic-bootstrap-method-part-iii-af414bd73fda">Explaining invokedynamic. Bootstrap method. Part III</a>
 * <a href="https://www.baeldung.com/java-invoke-dynamic">An Introduction to Invoke Dynamic in the JVM</a>
 */
public interface BootstrapMethArg extends Serializable {
    public BootstrapMethArg clone();
    public Object toAsmValue();

    public static Either<String,BootstrapMethArg> from( Object value ){
        if( value==null )return Either.left("value is null");
        if( value instanceof Integer )return Either.right(new IntArg( (Integer)value ));
        if( value instanceof Float )return Either.right(new FloatArg( (Float) value ));
        if( value instanceof Long )return Either.right(new LongArg( (Long)value ));
        if( value instanceof Double )return Either.right(new DoubleArg( (Double)value ));
        if( value instanceof String )return Either.right(new StringArg( (String)value ));
        if( value instanceof org.objectweb.asm.Type ){
            var tvalue = (org.objectweb.asm.Type)value;
            return Either.right(new TypeArg(tvalue));
        }else{
            if( value instanceof Handle ) return Either.right(new MethodHandle( (Handle)value ));
            if( value instanceof ConstantDynamic ) return Either.right(new ConstDynamic( (ConstantDynamic)value ));
        }
        return Either.left("unknown type \""+value+"\""+(value!=null ? " "+value.getClass() : "") );
    }
}
