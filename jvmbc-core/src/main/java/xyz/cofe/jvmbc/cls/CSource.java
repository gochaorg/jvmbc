package xyz.cofe.jvmbc.cls;

import java.util.Optional;
import java.util.function.Consumer;

import org.objectweb.asm.ClassWriter;

/**
 * Содержит имя исходного класса/файла отладки (debug)
 */
public class CSource implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CSource(){
    }
    public CSource(String source, String debug){
        this.source = source!=null ? Optional.of(source) : Optional.empty();
        this.debug = debug!=null ? Optional.of(debug) : Optional.empty();
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CSource(CSource sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        source = sample.source;
        debug = sample.debug;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CSource clone(){
        return new CSource(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CSource configure(Consumer<CSource> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region source : String
    protected Optional<String> source;
    public Optional<String> getSource(){
        return source;
    }
    public void setSource(Optional<String> source){
        //noinspection OptionalAssignedToNull
        if( source==null )throw new IllegalArgumentException( "source==null" );
        this.source = source;
    }
    //endregion
    //region debug : String
    protected Optional<String> debug;

    public Optional<String> getDebug(){
        return debug;
    }

    public void setDebug(Optional<String> debug){
        //noinspection OptionalAssignedToNull
        if( debug==null )throw new IllegalArgumentException( "debug==null" );
        this.debug = debug;
    }
    //endregion

    @Override
    public String toString(){
        return CSource.class.getSimpleName()+" " +
            "source=" +source +
            ", debug="+debug;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitSource(
            getSource().orElse(null),
            getDebug().orElse(null)
        );
    }
}
