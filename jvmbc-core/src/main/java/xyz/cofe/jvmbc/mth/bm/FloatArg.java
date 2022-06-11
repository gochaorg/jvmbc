package xyz.cofe.jvmbc.mth.bm;

/**
 * Аргумент bootstrap метода
 */
public class FloatArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public FloatArg(){}
    public FloatArg(Float v){
        value = v;
    }

    //region clone
    /**
     * Конструктор копирования
     * @param sample образец
     */
    public FloatArg(FloatArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        value = sample.getValue();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public FloatArg clone(){ return new FloatArg(this); }
    //endregion
    //region value : Float
    private Float value;
    public Float getValue(){
        return value;
    }
    public void setValue(Float value){
        this.value = value;
    }
    //endregion

    public String toString(){
        return "FloatArg{"+value+"}";
    }

    @Override
    public Object toAsmValue(){
        return getValue();
    }
}
