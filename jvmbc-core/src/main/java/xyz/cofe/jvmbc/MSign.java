package xyz.cofe.jvmbc;

/**
 * Представление signature для generic методов
 */
public class MSign {
    /**
     * Конструктор
     * @param raw сырое представление
     */
    public MSign( String raw){
        if( raw==null )throw new IllegalArgumentException( "raw==null" );
        this.raw = raw;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MSign( MSign sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        raw = sample.raw;
    }

    public MSign clone(){ return new MSign(this); }

    protected String raw;

    /**
     * Возвращает сырое представление
     * @return сырое представление
     */
    public String getRaw(){ return raw; }
}
