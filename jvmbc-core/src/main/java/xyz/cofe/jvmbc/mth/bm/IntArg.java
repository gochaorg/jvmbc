package xyz.cofe.jvmbc.mth.bm;

/**
 * Аргумент bootstrap метода
 */
public class IntArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public IntArg(){}
    public IntArg(Integer v){
        value = v;
    }

    //region clone
    /**
     * Конструктор копирования
     * @param sample образец
     */
    public IntArg(IntArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        value = sample.getValue();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public IntArg clone(){ return new IntArg(this); }
    //endregion
    //region value : Integer
    private Integer value;

    public Integer getValue(){
        return value;
    }

    public void setValue(Integer value){
        this.value = value;
    }
    //endregion

    public String toString(){
        return "IntArg{"+value+"}";
    }

    @Override
    public Object toAsmValue(){
        return getValue();
    }
}
