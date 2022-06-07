package xyz.cofe.jvmbc.cls;

import org.objectweb.asm.ClassWriter;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.rec.RecordByteCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CRecord implements ClsByteCode {
    public CRecord(){}
    public CRecord(CRecord sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.name = sample.getName();
        if( sample.descProperty!=null )descProperty = sample.descProperty.clone();
        this.sign = sample.getSign();

        if( sample.recordByteCodes!=null ){
            recordByteCodes = new ArrayList<>();
            sample.recordByteCodes.forEach( c -> {
                if( c!=null ){
                    recordByteCodes.add(c.clone());
                }else{
                    recordByteCodes.add(null);
                }
            });
        }
    }
    public CRecord(String name, String desc, String sign){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( desc==null )throw new IllegalArgumentException( "desc==null" );
        this.name = name;
        descProperty = new TDesc(desc);
        this.sign = sign;
    }

    protected String name;
    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

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

    protected String sign; //todo optional

    public String getSign(){
        return sign;
    }

    public void setSign( String sign ){
        this.sign = sign;
    }

    protected List<RecordByteCode> recordByteCodes;
    public List<RecordByteCode> getRecordByteCodes(){
        if( recordByteCodes!=null ) return recordByteCodes;
        recordByteCodes = new ArrayList<>();
        return recordByteCodes;
    }
    public void setRecordByteCodes(List<RecordByteCode> recordByteCodes){
        this.recordByteCodes = recordByteCodes;
    }

    @Override
    public void write( ClassWriter v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        var recVisit = v.visitRecordComponent(getName(), getDesc().getRaw(), getSign());
        //noinspection ConstantConditions
        if( recVisit!=null ){
            getRecordByteCodes().forEach( recBc -> {
                if( recBc!=null ){
                    recBc.write(recVisit);
                }
            });
        }
    }

    @Override
    public CRecord clone(){
        return new CRecord(this);
    }

    /**
     * Возвращает дочерние узлы
     *
     * @return дочерние узлы
     */
    @Override
    public List<ByteCode> nodes(){
        if( recordByteCodes!=null ){
            return recordByteCodes.stream().map( x -> (ByteCode)x ).collect(Collectors.toList());
        }
        return ClsByteCode.super.nodes();
    }
}
