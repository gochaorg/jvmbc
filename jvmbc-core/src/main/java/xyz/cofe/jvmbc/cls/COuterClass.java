package xyz.cofe.jvmbc.cls;

import java.util.Optional;
import java.util.function.Consumer;

import org.objectweb.asm.ClassWriter;
import xyz.cofe.jvmbc.TDesc;

public class COuterClass implements ClsByteCode, ClazzWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public COuterClass(){}
    public COuterClass(String owner, String name, String descriptor){
        this.owner = owner;
        this.name = name!=null ? Optional.of(name) : Optional.empty();
        this.desc().setRaw(descriptor);
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public COuterClass(COuterClass sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        owner = sample.owner;
        name = sample.name;
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public COuterClass clone(){
        return new COuterClass(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public COuterClass configure(Consumer<COuterClass> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region owner : String
    protected String owner;
    public String getOwner(){
        return owner;
    }
    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
    //region name : String
    protected Optional<String> name = Optional.empty();
    public Optional<String> getName(){
        return name;
    }
    public void setName(Optional<String> name){
        //noinspection OptionalAssignedToNull
        if( name==null )throw new IllegalArgumentException( "name==null" );
        this.name = name;
    }
    //endregion
    //region desc() - дескриптор типа
    /**
     * Дескриптор типа данных
     */
    protected TDesc descProperty;

    /**
     * Возвращает дескриптор типа данных
     * @return Дескриптор типа данных
     */
    public TDesc desc(){ //todo optional
        if( descProperty!=null )return descProperty;
        descProperty = new TDesc();
        return descProperty;
    }
    //endregion

    @Override
    public String toString(){
        return COuterClass.class.getSimpleName()+" " +
            "owner=" + owner +
            " name=" + name +
            " descriptor=" + desc() ;
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitOuterClass(getOwner(),getName().orElse(null), desc().getRaw());
    }
}
