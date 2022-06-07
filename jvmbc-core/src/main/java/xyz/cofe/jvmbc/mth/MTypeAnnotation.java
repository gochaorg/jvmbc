package xyz.cofe.jvmbc.mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.TypeRefProperty;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.AnnotationDef;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

public class MTypeAnnotation extends MAbstractBC
    implements
        ByteCode,
        AnnotationDef,
        GetAnnotationByteCodes,
        MethodWriter,
        TypeRefProperty,
        TypeRefMTypeAnn
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MTypeAnnotation(){}
    public MTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        this.typeRef = typeRef;
        this.typePath = typePath!=null ? Optional.of(typePath.toString()) : Optional.empty();
        this.descProperty = new TDesc(descriptor);
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MTypeAnnotation(MTypeAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.typeRef = sample.typeRef;
        this.typePath = sample.typePath;
        this.descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        this.visible = sample.visible;

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

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MTypeAnnotation clone(){ return new MTypeAnnotation(this); }

    //region typeRef : int
    protected int typeRef;
    public int getTypeRef(){
        return typeRef;
    }

    public void setTypeRef(int typeRef){
        this.typeRef = typeRef;
    }
    //endregion
    //region typePath : String
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
        return MTypeAnnotation.class.getSimpleName()+
            " typeRef="+typeRef+
            " typePath="+typePath+
            " descriptor="+getDesc()+
            " visible="+visible;
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
    public void write(MethodVisitor v, MethodWriterCtx ctx){
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
