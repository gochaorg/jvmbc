package xyz.cofe.jvmbc.mth.bm;

/**
 * Аргумент bootstrap метода
 */
public class DoubleArg implements BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public DoubleArg(){}
    public DoubleArg(Double v){
        value = v;
    }
    //region clone
    /**
     * Конструктор копирования
     * @param sample образец
     */
    public DoubleArg(DoubleArg sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        value = sample.getValue();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public DoubleArg clone(){ return new DoubleArg(this); }
    //endregion

    //region value : Double
    private Double value;
    public Double getValue(){
        return value;
    }
    public void setValue(Double value){
        this.value = value;
    }
    //endregion

    public String toString(){
        return "DoubleArg{"+value+"}";
    }

    @Override
    public Object toAsmValue(){
        return getValue();
    }
}
