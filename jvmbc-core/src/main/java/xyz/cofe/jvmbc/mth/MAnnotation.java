package xyz.cofe.jvmbc.mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.AnnotationDef;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

/**
 * Visits an annotation of this method.
 *
 * <p>
 * {@link #desc()} the class descriptor of the annotation class.
 *
 * <p>
 * {@link #visible}  {@literal true} if the annotation is visible at runtime.
 *
 * <p>
 * a visitor to visit the annotation values, or {@literal null} if this visitor is not
 * interested in visiting this annotation.
 */
public class MAnnotation
    extends MAbstractBC
    implements ByteCode, AnnotationDef, GetAnnotationByteCodes, MethodWriter
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MAnnotation(){}
    public MAnnotation(String descriptor, boolean visible){
        this.desc().setRaw(descriptor);
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MAnnotation(MAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        this.visible = sample.isVisible();
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

    public MAnnotation clone(){ return new MAnnotation(this); }

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
        return MAnnotation.class.getSimpleName()+
            " descriptor="+desc()+
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

        var av = v.visitAnnotation(desc().getRaw(), isVisible());

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
