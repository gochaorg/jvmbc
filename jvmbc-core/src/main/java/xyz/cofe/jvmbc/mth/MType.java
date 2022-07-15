package xyz.cofe.jvmbc.mth;

import org.objectweb.asm.MethodVisitor;
import xyz.cofe.jvmbc.ByteCode;

/**
 * the opcode of the type instruction to be visited. This opcode is either NEW,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.anewarray">ANEWARRAY</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.checkcast">CHECKCAST</a> or
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.instanceof">INSTANCEOF</a>.
 * 
 * см {@link OpCode#ANEWARRAY}, {@link OpCode#CHECKCAST}, {@link OpCode#INSTANCEOF}
 */
public class MType extends MAbstractBC implements ByteCode, MethodWriter, OpcodeProperty {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MType(){}
    
    /**
     * Конструктор
     * @param op код инструкции
     * @param type тип
     */
    public MType( int op, String type ){
        this.opcode = op;
        this.type = type;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MType( MType sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        opcode = sample.opcode;
        type = sample.type;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MType clone(){ return new MType(this); }

    //region opcode : int
    private int opcode;

    /**
     * Возвращает код инструкции
     * @return Код инструкции
     */
    public int getOpcode(){
        return opcode;
    }

    /**
     * Указывает код инструкции
     * @param opcode Код инструкции
     */
    public void setOpcode(int opcode){
        this.opcode = opcode;
    }
    //endregion
    //region type : String
    private String type;

    /**
     * Возвращает тип
     * @return тип
     */
    public String getType(){
        return type;
    }

    /**
     * Указывает тип
     * @param type Тип
     */
    public void setType(String type){
        this.type = type;
    }
    //endregion

    public String toString(){
        return MType.class.getSimpleName()+
            " opcode="+OpCode.code(opcode).map(OpCode::name).orElse("?")+"#"+opcode+
            " operand="+ type
            ;
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        v.visitTypeInsn(getOpcode(), getType());
    }
}
