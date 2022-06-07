package xyz.cofe.jvmbc.rec;

import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.TypeRefProperty;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RecTypeAnnotation
    implements
        RecordByteCode,
        GetAnnotationByteCodes,
        TypeRefProperty,
    TypeRefRecTypeAnn
{
    public RecTypeAnnotation(){}
    public RecTypeAnnotation( RecTypeAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        descProperty = sample.descProperty != null ? sample.descProperty.clone() : new TDesc();
        visible = sample.visible;
        typeRef = sample.typeRef;
        typePath = sample.typePath;

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
    public RecTypeAnnotation( int typeRef, String typePath, String desc, boolean visible){
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        descProperty = new TDesc(desc);
        this.visible = visible;
        this.typeRef = typeRef;
        this.typePath = typePath!=null ? Optional.of(typePath) : Optional.empty();
    }

    //region typeRef : int
    private int typeRef;

    public int getTypeRef(){
        return typeRef;
    }

    public void setTypeRef( int typeRef ){
        this.typeRef = typeRef;
    }
    //endregion

    //region typePath : String
    private Optional<String> typePath = Optional.empty();

    public Optional<String> getTypePath(){
        return typePath;
    }

    public void setTypePath( Optional<String> typePath ){
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
    private boolean visible;
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
    }
    //endregion

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
    public RecordByteCode clone(){
        return new RecTypeAnnotation(this);
    }

    @Override
    public void write( RecordComponentVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var tp = getTypePath();
        v.visitTypeAnnotation(
            getTypeRef(),
            tp.map(TypePath::fromString).orElse(null),
            getDesc().getRaw(),
            isVisible()
        );
    }
}
