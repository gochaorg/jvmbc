package xyz.cofe.jvmbc;

import java.io.Serializable;
import java.util.Optional;

/**
 * Описывает сигнатуру (без generic) метода
 */
public class MDesc implements Serializable {
    /**
     * Конструктор по умолчанию
     */
    public MDesc(){
    }

    /**
     * Конструктор
     * @param raw "сырое" значение
     */
    public MDesc( String raw ){
        this.raw = raw;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MDesc( MDesc sample ){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.raw = sample.raw;
        this.typeDesc = sample.typeDesc;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MDesc clone(){
        return new MDesc(this);
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
    public void setRaw( String raw ){
        this.raw = raw;
        this.typeDesc = null;
    }

    private transient MethodDescTypes typeDesc;

    /**
     * Возвращает тип значения
     * @return тип значения
     */
    public Optional<MethodDescTypes> tryGet(){
        if( typeDesc!=null )return Optional.of(typeDesc);
        var raw = this.raw;
        if( raw==null )return Optional.empty();
        try{
            typeDesc = MethodDescTypes.parse(raw);
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
    public MethodDescTypes get(){
        if( typeDesc!=null )return typeDesc;
        var raw = this.raw;
        if( raw==null )throw new IllegalStateException("raw value is null");
        typeDesc = MethodDescTypes.parse(raw);
        return typeDesc;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(MDesc.class.getSimpleName()).append("{");
        sb.append("raw=").append(raw);
        tryGet().ifPresent( t -> {
            sb.append(" get=").append(t);
        });
        sb.append("}");
        return sb.toString();
    }
}
