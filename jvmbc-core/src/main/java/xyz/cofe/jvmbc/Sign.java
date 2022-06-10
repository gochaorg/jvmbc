package xyz.cofe.jvmbc;

/**
 * Представление signature для generic типов
 */
public class Sign {
    /**
     * Конструктор
     * @param raw сырое представление
     */
    public Sign(String raw){
        if( raw==null )throw new IllegalArgumentException( "raw==null" );
        this.raw = raw;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public Sign(Sign sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        raw = sample.raw;
    }

    public Sign clone(){ return new Sign(this); }

    protected String raw;

    /**
     * Возвращает сырое представление
     * @return сырое представление
     */
    public String getRaw(){ return raw; }
}
