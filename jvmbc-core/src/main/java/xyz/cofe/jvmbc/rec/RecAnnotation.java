package xyz.cofe.jvmbc.rec;

import org.objectweb.asm.RecordComponentVisitor;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecAnnotation implements RecordByteCode, GetAnnotationByteCodes {
    public RecAnnotation(){}
    public RecAnnotation(RecAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        desc = sample.desc != null ? sample.desc.clone() : null;
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
    public RecAnnotation(String desc,boolean visible){
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        desc().setRaw(desc);
        this.visible = visible;
    }

    private TDesc desc;
    public TDesc desc(){
        if( desc!=null )return desc;
        desc = new TDesc();
        return desc;
    }

    private boolean visible;
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
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
    public RecordByteCode clone(){
        return new RecAnnotation(this);
    }

    @Override
    public void write( RecordComponentVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitAnnotation(desc().getRaw(), isVisible());
    }
}
