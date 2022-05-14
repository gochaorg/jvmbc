package xyz.cofe.jvmbc.ann;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;

public class EmANameDesc extends EmbededAnnotation implements AnnotationWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public EmANameDesc(){
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public EmANameDesc(EmANameDesc sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );

        name = sample.getName();
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;

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

    public EmANameDesc clone(){
        return new EmANameDesc(this);
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
    public TDesc desc(){
        if( descProperty!=null )return descProperty;
        descProperty = new TDesc();
        return descProperty;
    }
    //endregion

    public String toString(){
        return EmANameDesc.class.getSimpleName()+" name="+name+" descriptor="+desc();
    }

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
    public void write(AnnotationVisitor v){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var nv = v.visitAnnotation(getName(), desc().getRaw());
        var body = annotationByteCodes;
        if( body!=null ){
            for( var b : body ){
                if( b instanceof AnnotationWriter ){
                    ((AnnotationWriter)b).write(nv);
                }
            }
        }
    }
}
