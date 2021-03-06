package xyz.cofe.jvmbc.mth;

import org.objectweb.asm.MethodVisitor;

/**
 * Метка в исходном коде/точка перехода
 */
public class MLabel extends MAbstractBC implements MethodWriter {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MLabel(){}
    
    /**
     * Конструктор
     * @param name имя метки
     */
    public MLabel(String name){this.name = name;}

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MLabel(MLabel sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        name = sample.getName();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod") public MLabel clone(){ return new MLabel(this); }

    private String name;
    
    /**
     * Возвращает название метки
     * @return название метки
     */
    public String getName(){ return name; }
    
    /**
     * Указывает название метки
     * @param name название метки
     */
    public void setName(String name){ this.name = name; }

    @Override
    public String toString(){
        return MLabel.class.getSimpleName()+" name="+name;
    }

    /**
     * final - ибо на конкретный MLabel нужно ссылаться
     * @return хеш код
     */
    @Override
    public final int hashCode(){
//        var n = name;
//        if( n!=null )return n.hashCode();
//        return super.hashCode();
        return super.hashCode();
    }

    /**
     * final - ибо на конкретный MLabel нужно ссылаться
     * @param obj образец
     * @return эквивалентны
     */
    @Override
    public final boolean equals(Object obj){
        return super.equals(obj);
//        if( obj==null )return false;
//        if( obj.getClass()!= MLabel.class )return false;
//        var lb = (MLabel)obj;
//        var n0 = name;
//        var n1 = lb.name;
//        if( n0==null && n1==null )return true;
//        if( n0!=null && n1==null )return false;
//        //noinspection ConstantConditions
//        if( n1!=null && n0==null )return false;
//        return n0.equals(n1);
    }

    @Override
    public void write(MethodVisitor v, MethodWriterCtx ctx){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        if( ctx==null )throw new IllegalArgumentException( "ctx==null" );

        var ln = getName();
        if( ln==null )throw new IllegalStateException("name not defined");

        v.visitLabel(ctx.labelCreateOrGet(ln));
    }
}
