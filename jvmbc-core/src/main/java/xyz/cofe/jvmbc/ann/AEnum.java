package xyz.cofe.jvmbc.ann;

import org.objectweb.asm.AnnotationVisitor;
import xyz.cofe.jvmbc.TDesc;

public class AEnum extends AAbstractBC implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public AEnum(){}
    public AEnum(String name, String descriptor, String value){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( descriptor==null )throw new IllegalArgumentException( "descriptor==null" );

        this.name = name;
        this.descProperty = new TDesc(descriptor);
        this.value = value;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public AEnum(AEnum sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.name = sample.getName();
        this.descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        this.value = sample.getValue();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public AEnum clone(){
        return new AEnum(this);
    }

    //region name : String
    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
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
    public TDesc getDesc(){
        return descProperty;
    }

    /**
     * Указывает дескриптор типа данных
     * @param desc Дескриптор типа данных
     */
    public void setDesc(TDesc desc){
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        descProperty = desc;
    }
    //endregion
    //region value : String
    protected String value;

    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
    //endregion

    public String toString(){
        return AEnum.class.getSimpleName()+" name="+name+
            " descriptor="+getDesc()+
            " value="+(value != null ? "\""+value+"\"" : "null" );
    }

    @Override
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitEnum(getName(), getDesc().getRaw(), getValue());
    }
}
