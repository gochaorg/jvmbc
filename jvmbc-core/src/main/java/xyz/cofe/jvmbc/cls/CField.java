package xyz.cofe.jvmbc.cls;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.jvmbc.AccFlag;
import xyz.cofe.jvmbc.AccFlags;
import xyz.cofe.jvmbc.AccFlagsProperty;
import xyz.cofe.jvmbc.Sign;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.fld.FieldByteCode;
import xyz.cofe.jvmbc.fld.FieldEnd;

/**
 * Описывает поле класса
 * <p>
Пример
 <pre>
 public @interface Desc {
     String value();
 }

 &#64;Desc("sample User2")
 public class User2 {
     public User2(){}
     public User2(String name1){
     this.name = name1;
     }

     &#64;Desc("name of user")
     private String name;

     &#64;Desc("name of user")
     public String getName(){ return name; }
     public void setName( @Required @MaxLength(100) @MinLength(1) String name ){ this.name = name; }

     private List<String> emails;
     &#64;Desc("emails of user")
     public List<String> getEmails(){ return emails; }
     public void setEmails( List<String> emails ){ this.emails = emails; }
 }
 </pre>

 Будет описываться как:

 <pre>
 &lt;CBegin version="55" ...
 &lt;CField
   name="name"
   access="2"
   accessDecode="[Private]"
   descriptor="Ljava/lang/String;"
   /&gt;
 </pre>
 */
public class CField implements ClsByteCode, ClazzWriter, AccFlagsProperty, FieldFlags {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CField(){}

    /**
     * Конструктор
     * @param access флаги доступа к полю
     * @param name имя поля
     * @param descriptor дескриптор типа
     * @param signature сигнатура, в случае Generic
     * @param value значение
     */
    public CField(int access, String name, String descriptor, String signature, Object value){
        this.access = access;
        this.name = name;
        descProperty = new TDesc(descriptor);
        this.signature = signature!=null ? Optional.of(new Sign(signature)) : Optional.empty();
        this.value = value;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CField(CField sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        access = sample.getAccess();
        name = sample.getName();
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        signature = sample.getSignature();
        value = sample.getValue();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CField clone(){
        return new CField(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CField configure(Consumer<CField> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region access - флаги доступа к полю
    /**
     * флаги доступа к полю {@link AccFlags}
     */
    protected int access;

    /**
     * Возвращает флаги доступа к полю {@link AccFlags}
     * @return флаги доступа
     */
    public int getAccess(){
        return access;
    }

    /**
     * Указывает флаги доступа к полю {@link AccFlags}
     * @param access флаги доступа
     */
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name - имя поля
    /**
     * имя поля
     */
    protected String name;

    /**
     * Возвращает имя поля
     * @return имя поля
     */
    public String getName(){
        return name;
    }

    /**
     * Указывает имя поля
     * @param name имя поля
     */
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
    //region signature - сигнатура, в случае Generic
    /**
     * сигнатура, в случае Generic
     */
    protected Optional<Sign> signature = Optional.empty(); // //todo optional

    /**
     * Возвращает сигнатуру в случае Generic типа
     * @return сигнатура или null
     */
    public Optional<Sign> getSignature(){
        return signature;
    }

    /**
     * Указывает сигнатуру в случае Generic типа
     * @param signature сигнатура или null
     */
    public void setSignature(Optional<Sign> signature){
        //noinspection OptionalAssignedToNull
        if( signature==null )throw new IllegalArgumentException( "signature==null" );
        this.signature = signature;
    }
    //endregion
    //region value - значение
    /**
     * значение поля
     */
    protected Object value; // //todo optional

    /**
     * Возвращает значение поля
     * @return значение
     */
    public Object getValue(){
        return value;
    }

    /**
     * Указывает значение поля
     * @param value значение
     */
    public void setValue(Object value){
        this.value = value;
    }
    //endregion

    @Override
    public String toString(){
        return CField.class.getSimpleName() +
            " access="+ AccFlag.flags(access, AccFlag.Scope.FIELD)+"#"+access+
            " name=" + name +
            " desc=" + getDesc() +
            " signature=" + signature +
            " value=" + value ;
    }

    //region fieldByteCodes : List<FieldByteCode> - Байт-код связанный с полем - аннотации
    /**
     * Байт-код связанный с полем - аннотации
     */
    protected List<FieldByteCode> fieldByteCodes;

    /**
     * Возвращает байт-код связанный с полем - аннотации
     * @return байт-код связанный с полем
     */
    public List<FieldByteCode> getFieldByteCodes(){
        if( fieldByteCodes==null )fieldByteCodes = new ArrayList<>();
        return fieldByteCodes;
    }

    /**
     * Указывает байт-код связанный с полем - аннотации
     * @param fld байт-код связанный с полем
     */
    public void setFieldByteCodes(List<FieldByteCode> fld){
        this.fieldByteCodes = fld;
    }
    //endregion

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var fv = v.visitField(
            getAccess(),
            getName(),
            getDesc().getRaw(),
            getSignature().map(Sign::getRaw).orElse(null),
            getValue()
        );
        var body = fieldByteCodes;
        if( body!=null ){
            for( var b : body ){
                if( !(b instanceof FieldEnd) ){
                    b.write(fv);
                }
            }
        }
        fv.visitEnd();
    }
}
