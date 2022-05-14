package xyz.cofe.jvmbc.fld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.AnnotationDef;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

public class FTypeAnnotation implements FieldByteCode, AnnotationDef, GetAnnotationByteCodes {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public FTypeAnnotation(){
    }

    public FTypeAnnotation(int typeRef, String typePath, String descriptor, boolean visible){
        this.typeRef = typeRef;
        this.typePath = typePath;
        this.desc().setRaw(descriptor);
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public FTypeAnnotation(FTypeAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        typeRef = sample.getTypeRef();
        typePath = sample.getTypePath();
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        visible = sample.isVisible();

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
    public FTypeAnnotation clone(){
        return new FTypeAnnotation(this);
    }

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
    protected String typePath;
    public String getTypePath(){
        return typePath;
    }
    public void setTypePath(String typePath){
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
    public TDesc desc(){
        if( descProperty!=null )return descProperty;
        descProperty = new TDesc();
        return descProperty;
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
        return FTypeAnnotation.class.getSimpleName()+" typeRef="+typeRef+" typePath="+typePath+" descriptor="+desc()+" visible="+visible;
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
    public void write(FieldVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        var tp = getTypePath();
        var av = v.visitTypeAnnotation(
            getTypeRef(),
            tp!=null ? TypePath.fromString(tp) : null,
            desc().getRaw(),
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
