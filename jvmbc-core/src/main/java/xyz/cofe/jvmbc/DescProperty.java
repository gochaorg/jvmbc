package xyz.cofe.jvmbc;

import java.util.Optional;

/**
 * Описывает тип данных (переменной, поля класса, ...)
 */
public class DescProperty {
    public DescProperty(){
    }

    public DescProperty(String raw){
        this.raw = raw;
    }

    protected String raw;
    public String getRaw(){ return raw; }
    public void setRaw( String raw ){
        this.raw = raw;
    }

    private TypeDesc typeDesc;
}
