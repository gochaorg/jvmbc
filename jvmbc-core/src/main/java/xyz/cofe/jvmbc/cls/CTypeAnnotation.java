package xyz.cofe.jvmbc.cls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.TypeRefProperty;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.AnnotationDef;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

public class CTypeAnnotation
    implements ClsByteCode, AnnotationDef, GetAnnotationByteCodes,
    ClazzWriter, TypeRefProperty, TypeRefCTypeAnn
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CTypeAnnotation(){
    }

    public CTypeAnnotation(int typeRef, String typePath, String descriptor, boolean visible){
        this.typeRef = typeRef;
        this.typePath = typePath!=null ? Optional.of(typePath) : Optional.empty();
        descProperty = new TDesc(descriptor);
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public CTypeAnnotation(CTypeAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        typePath = sample.typePath;
        typeRef = sample.typeRef;
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        visible = sample.visible;

        if( sample.annotationByteCodes!=null ){
            annotationByteCodes = new ArrayList<>();
            for( var b : sample.annotationByteCodes ){
                if( b!=null ){
                    annotationByteCodes.add(b.clone());
                }else{
                    annotationByteCodes.add(null);
                }
            }
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CTypeAnnotation clone(){
        return new CTypeAnnotation(this);
    }

    /**
     * Конфигурация экземпляра
     * @param conf конфигурация
     * @return SELF ссылка
     */
    public CTypeAnnotation configure(Consumer<CTypeAnnotation> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    //region typeRef : int
    /**
     *  a reference to the annotated type. The sort of this type reference must be
     * {@link TypeReference#CLASS_TYPE_PARAMETER},
     * {@link TypeReference#CLASS_TYPE_PARAMETER_BOUND} or
     * {@link TypeReference#CLASS_EXTENDS}
     * See {@link TypeReference}.
     */
    protected int typeRef;
    public int getTypeRef(){
        return typeRef;
    }
    public void setTypeRef(int typeRef){
        this.typeRef = typeRef;
    }
    //endregion
    //region typePath : String
    /**
     * the path to the annotated type argument, wildcard bound, array element type, or
     * static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     * 'typeRef' as a whole.
     */
    protected Optional<String> typePath = Optional.empty();
    public Optional<String> getTypePath(){
        return typePath;
    }
    public void setTypePath(Optional<String> typePath){
        //noinspection OptionalAssignedToNull
        if( typePath==null )throw new IllegalArgumentException( "typePath==null" );
        this.typePath = typePath;
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
    //region visible : boolean
    protected boolean visible;
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
    }
    //endregion

    public String toString(){
        return CTypeAnnotation.class.getSimpleName()+
            " typeRef="+typeRef+" typePath="+typePath+" descriptor="+getDesc()+" visible="+visible;
    }

    //region annotationByteCodes : List<AnnotationByteCode>
    protected List<AnnotationByteCode> annotationByteCodes;
    public List<AnnotationByteCode> getAnnotationByteCodes(){
        if(annotationByteCodes==null)annotationByteCodes = new ArrayList<>();
        return annotationByteCodes;
    }
    public void setAnnotationByteCodes(List<AnnotationByteCode> byteCodes){
        annotationByteCodes = byteCodes;
    }
    //endregion

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public List<ByteCode> nodes(){
        if( annotationByteCodes!=null )return Collections.unmodifiableList(annotationByteCodes);
        return List.of();
    }

    @Override
    public void write(ClassWriter v){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        var tp = getTypePath();
        var av = v.visitTypeAnnotation(
            getTypeRef(),
            tp.map(TypePath::fromString).orElse(null),
            getDesc().getRaw(),
            isVisible()
        );

        var abody = annotationByteCodes;
        if( abody!=null ){
            var i = -1;
            for( var ab : abody ){
                i++;
                if( ab!=null ){
                    ab.write(av);
                }else{
                    throw new IllegalStateException("annotationByteCodes["+i+"]==null");
                }
            }
        }
    }
}
