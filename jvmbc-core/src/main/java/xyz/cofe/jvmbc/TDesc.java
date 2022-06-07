package xyz.cofe.jvmbc;

import java.io.Serializable;
import java.util.Optional;

/**
 * Описывает тип данных (переменной, поля класса, ...)
 */
public class TDesc implements Serializable {
    /**
     * Конструктор по умолчанию
     */
    public TDesc(){
    }

    /**
     * Конструктор
     * @param raw "сырое" значение
     */
    public TDesc( String raw ){
        this.raw = raw;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public TDesc( TDesc sample ){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.raw = sample.raw;
        this.typeDesc = sample.typeDesc;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public TDesc clone(){
        return new TDesc(this);
    }

    /**
     * "сырое" значение
     */
    protected String raw;

    /**
     * Возвращает "сырое" значение
     * @return "сырое" значение или null
     */
    public String getRaw(){ return raw; }

    /**
     * Указывает "сырое" значение
     * @param raw "сырое" значение или null
     */
    private void setRaw( String raw ){
        this.raw = raw;
        this.typeDesc = null;
    }

    private transient TypeDesc typeDesc;

    /**
     * Возвращает тип значения
     * @return тип значения
     */
    public Optional<TypeDesc> tryGet(){
        if( typeDesc!=null )return Optional.of(typeDesc);
        var raw = this.raw;
        if( raw==null )return Optional.empty();
        try{
            typeDesc = TypeDesc.parse(raw);
            return Optional.of(typeDesc);
        } catch( Error e ){
            return Optional.empty();
        }
    }

    /**
     * Возвращает тип значения
     * @return тип значения
     * @throws IllegalStateException если не установлено raw
     * @throws Error если raw значение не получилось распознать
     */
    public TypeDesc get(){
        if( typeDesc!=null )return typeDesc;
        var raw = this.raw;
        if( raw==null )throw new IllegalStateException("raw value is null");
        typeDesc = TypeDesc.parse(raw);
        return typeDesc;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(TDesc.class.getSimpleName()).append("{");
        sb.append("raw=").append(raw);
        tryGet().ifPresent( t -> {
            sb.append(" get=").append(t);
        });
        sb.append("}");
        return sb.toString();
    }
}
