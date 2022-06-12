package xyz.cofe.jvmbc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.jvmbc.Sign;
import xyz.cofe.jvmbc.TDesc;

import java.util.Optional;

/**
 * Определение локальной переменной
 */
public class MLocalVariable extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLocalVariable(){}

    /**
     * Конструктор
     * @param name имя переменной
     * @param descriptor дескриптор типа локальной переменной
     * @param signature сигнатура для Generic типа локальной переменной или null
     * @param labelStart первая инструкция, соответствующая области действия этой локальной переменной (включительно).
     * @param labelEnd последняя инструкция, соответствующая области действия этой локальной переменной (исключая).
     * @param index индекс переменной
     */
    public MLocalVariable(String name, String descriptor, String signature, String labelStart, String labelEnd, int index){
        this.name = name;
        descProperty = new TDesc(descriptor);
        this.signature = signature!=null ? Optional.of(new Sign(signature)) : Optional.empty();
        this.labelStart = labelStart;
        this.labelEnd = labelEnd;
        this.index = index;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MLocalVariable(MLocalVariable sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.name;
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        signature = sample.signature;
        labelStart = sample.labelStart;
        labelEnd = sample.labelEnd;
        index = sample.index;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLocalVariable clone(){ return new MLocalVariable(this); }

    //region name : String - имя переменной
    private String name;
    
    /**
     * Возвращает имя переменной
     * @return имя переменной
     */
    public String getName(){
        return name;
    }
    
    /**
     * Указывает имя переменной
     * @param name имя переменной
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
    //region signature : String - сигнатура для Generic типа локальной переменной или null
    private Optional<Sign> signature = Optional.empty();
    
    /**
     * Возвращает сигнатура для Generic типа локальной переменной или null
     * @return сигнатура или null
     */
    public Optional<Sign> getSignature(){
        return signature;
    }
    
    /**
     * Указывает сигнатура для Generic типа локальной переменной или null
     * @param signature сигнатура или null
     */
    public void setSignature(Optional<Sign> signature){
        //noinspection OptionalAssignedToNull
        if( signature==null )throw new IllegalArgumentException( "signature==null" );
        this.signature = signature;
    }
    //endregion
    //region labelStart : String - первая инструкция, соответствующая области действия этой локальной переменной (включительно).
    private String labelStart;
    
    /**
     * Возвращает первая инструкция, соответствующая области действия этой локальной переменной (включительно).
     * @return первая инструкция, соответствующая области действия
     */
    public String getLabelStart(){
        return labelStart;
    }
    
    /**
     * Указывает первая инструкция, соответствующая области действия этой локальной переменной (включительно).
     * @param labelStart первая инструкция, соответствующая области действия
     */
    public void setLabelStart(String labelStart){
        this.labelStart = labelStart;
    }
    //endregion
    //region labelEnd : String - последняя инструкция, соответствующая области действия этой локальной переменной (исключая).
    private String labelEnd;
    
    /**
     * Возвращает последняя инструкция, соответствующая области действия этой локальной переменной (исключая).
     * @return последняя инструкция  (исключая)
     */
    public String getLabelEnd(){
        return labelEnd;
    }
    
    /**
     * Указывает последняя инструкция, соответствующая области действия этой локальной переменной (исключая).
     * @param labelEnd последняя инструкция  (исключая)
     */
    public void setLabelEnd(String labelEnd){
        this.labelEnd = labelEnd;
    }
    //endregion
    //region index : int - индекс переменной
    private int index;
    
    /**
     * Возвращает индекс переменной
     * @return индекс переменной
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * Указывает индекс переменной
     * @param index индекс переменной
     */
    public void setIndex(int index){
        this.index = index;
    }
    //endregion

    public String toString(){
        return MLocalVariable.class.getSimpleName()+
            " name="+name+
            " descriptor="+getDesc()+
            " signature="+signature+
            " start="+labelStart+
            " end="+labelEnd+
            " index="+index;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var ls = getLabelStart();
        var le = getLabelEnd();

        v.visitLocalVariable(
            getName(),
            getDesc().getRaw(),
            getSignature().map(Sign::getRaw).orElse(null),
            ls!=null ? ctx.labelGet(ls) : null,
            le!=null ? ctx.labelGet(le) : null,
            getIndex()
        );
    }
}
