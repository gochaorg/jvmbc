package xyz.cofe.jvmbc.mth;

import java.util.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.TypeRefProperty;
import xyz.cofe.jvmbc.ann.AnnotationByteCode;
import xyz.cofe.jvmbc.ann.AnnotationDef;
import xyz.cofe.jvmbc.ann.GetAnnotationByteCodes;

public class MLocalVariableAnnotation extends MAbstractBC
    implements
        ByteCode,
        AnnotationDef,
        GetAnnotationByteCodes,
        MethodWriter,
        TypeRefProperty,
        TypeRefMLocalVarAnn
{
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLocalVariableAnnotation(){}

    /**
     * Конструктор по умолчанию
     */
    public MLocalVariableAnnotation( int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible){
        if( descriptor==null )throw new IllegalArgumentException( "descriptor==null" );

        this.typeRef = typeRef;
        this.typePath = typePath!=null ? Optional.of(typePath.toString()) : Optional.empty();
        if( start!=null ){
            this.startLabels = Arrays.stream(start).map(s -> s!=null ? s.toString() : null).toArray(String[]::new);
        }
        if( end!=null ){
            this.endLabels = Arrays.stream(end).map(s -> s!=null ? s.toString() : null).toArray(String[]::new);
        }
        this.index = index;
        descProperty = new TDesc(descriptor);
        this.visible = visible;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MLocalVariableAnnotation(MLocalVariableAnnotation sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        typeRef = sample.typeRef;
        typePath = sample.typePath;
        if( sample.startLabels!=null )startLabels = Arrays.copyOf(sample.startLabels, sample.startLabels.length);
        if( sample.endLabels!=null )endLabels = Arrays.copyOf(sample.endLabels, sample.endLabels.length);
        if( sample.index!=null )index = Arrays.copyOf(sample.index, sample.index.length);
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

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLocalVariableAnnotation clone(){ return new MLocalVariableAnnotation(this); }

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
    //region startLabels : String[]
    protected String[] startLabels;
    public String[] getStartLabels(){
        return startLabels;
    }

    public void setStartLabels(String[] startLabels){
        this.startLabels = startLabels;
    }
    //endregion
    //region endLabels : String[]
    protected String[] endLabels;
    public String[] getEndLabels(){
        return endLabels;
    }

    public void setEndLabels(String[] endLabels){
        this.endLabels = endLabels;
    }
    //endregion
    //region index : int[]
    protected int[] index;
    public int[] getIndex(){
        return index;
    }

    public void setIndex(int[] index){
        this.index = index;
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
        return MLocalVariableAnnotation.class.getSimpleName()+
            " typeRef="+typeRef+
            " typePath="+typePath+
            " startLabels="+ Arrays.toString(startLabels) +
            " endLabels="+ Arrays.toString(endLabels) +
            " index="+ Arrays.toString(index) +
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
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var tp = getTypePath();

        var av = v.visitLocalVariableAnnotation(
            getTypeRef(),
            tp.map(TypePath::fromString).orElse(null),
            ctx.labelsGet(getStartLabels()),
            ctx.labelsGet(getEndLabels()),
            getIndex(),
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
