package xyz.cofe.jvmbc.mth.bm;

import org.objectweb.asm.Type;

import java.util.Optional;

/**
 * Аргумент bootstrap метода
 */
public class TypeArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public TypeArg(){}

    /**
     * Конструктор
     * @param type тип
     */
    public TypeArg(Type type){
        this.type = type!=null ? type.toString() : null;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public TypeArg(TypeArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        type = sample.getType();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TypeArg clone(){ return new TypeArg(this); }

    private String type;
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String toString(){
        return "Type "+type;
    }

    public Optional<Type> toType(){
        return type!=null ?
            Optional.of(Type.getType(type)) :
            Optional.empty();
    }

    @Override
    public Object toAsmValue(){
        return toType().orElse(null);
    }
}
