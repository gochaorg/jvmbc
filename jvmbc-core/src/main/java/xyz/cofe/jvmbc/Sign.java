package xyz.cofe.jvmbc;

/**
 * Представление signature для generic типов
 */
public class Sign {
    public Sign(){
    }

    public Sign(Sign sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        raw = sample.raw;
    }

    public Sign clone(){ return new Sign(this); }

    protected String raw;
    public String getRaw(){ return raw; }
    public void setRaw(String v){
        raw = v;
    }
}
