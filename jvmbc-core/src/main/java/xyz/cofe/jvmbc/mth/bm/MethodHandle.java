package xyz.cofe.jvmbc.mth.bm;

import org.objectweb.asm.Handle;
import xyz.cofe.jvmbc.MDesc;

import java.io.Serializable;
import java.util.Objects;

public class MethodHandle implements Serializable, BootstrapMethArg {
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public MethodHandle(){
    }

    /**
     * Конструктор из образца asm
     * @param sample образец
     */
    public MethodHandle( org.objectweb.asm.Handle sample ){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        tag = sample.getTag();
        desc().setRaw(sample.getDesc());
        name = sample.getName();
        owner = sample.getOwner();
        iface = sample.isInterface();
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public MethodHandle( MethodHandle sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        tag = sample.getTag();
        descProperty = sample.descProperty!=null ? sample.descProperty.clone() : null;
        name = sample.getName();
        owner = sample.getOwner();
        iface = sample.isIface();
    }
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MethodHandle clone(){
        return new MethodHandle(this);
    }

    //region tag : int
    private int tag;

    public int getTag(){
        return tag;
    }
    public void setTag(int tag){
        this.tag = tag;
    }
    //endregion
    //region descriptor : String - дескриптор типов параметров и результата
    /**
     * дескриптор типов параметров и результата
     */
    protected MDesc descProperty;

    /**
     * Возвращает дескриптор типов параметров и результата
     * @return дескриптор метода
     */
    public MDesc desc(){
        if( descProperty!=null )return descProperty;
        descProperty = new MDesc();
        return descProperty;
    }
    //endregion
    //region name : String
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion
    //region owner : String
    private String owner;

    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion
    //region iface : boolean
    private boolean iface;

    public boolean isIface(){
        return iface;
    }

    public void setIface(boolean iface){
        this.iface = iface;
    }
    //endregion

    @Override
    public String toString(){
        return MethodHandle.class.getSimpleName()+" { " +
            "tag=" + tag +
            ", desc='" + desc() + '\'' +
            ", name='" + name + '\'' +
            ", owner='" + owner + '\'' +
            ", iface=" + iface +
            '}';
    }

    @Override
    public boolean equals(Object o){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        MethodHandle handle = (MethodHandle) o;
        return
            tag == handle.tag &&
            iface == handle.iface &&
                Objects.equals(desc().getRaw(), handle.desc().getRaw()) &&
                Objects.equals(name, handle.name) &&
                Objects.equals(owner, handle.owner);
    }

    @Override
    public int hashCode(){
        return Objects.hash(tag, desc().getRaw(), name, owner, iface);
    }

    public org.objectweb.asm.Handle toHandle(){
        return new Handle(
            getTag(),
            getOwner(),
            getName(),
            descProperty!=null ? descProperty.getRaw() : null,
            isIface()
        );
    }
}
