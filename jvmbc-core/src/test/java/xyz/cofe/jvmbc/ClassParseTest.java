package xyz.cofe.jvmbc;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.jvmbc.cls.CMethod;
import xyz.cofe.jvmbc.cls.CModule;
import xyz.cofe.jvmbc.fld.FieldEnd;
import xyz.cofe.jvmbc.mth.OpCode;
import xyz.cofe.jvmbc.mth.OpcodeProperty;
import xyz.cofe.jvmbc.samples.SampleClass;

import java.util.List;
import java.util.Optional;

public class ClassParseTest extends CommonForTest {
    @Test
    public void sampleParse(){
        var cbegin = CBegin.parseByteCode(SampleClass.class);
        dump(cbegin);
    }

    @Test
    public void sampleParse1(){
        var props = new Props();
        props
            .ignore("class")
            .ignore(CMethod.class,"methodByteCodes")
            .ignore(CBegin.class, "fields", "innerClasses", "methods", "nestMembers", "order", "source")
            .ignore(prop -> prop.propValue instanceof List && ((List)prop.propValue).size()==1 && ((List)prop.propValue).get(0) instanceof FieldEnd)
            .conv(Optional.class, opt -> opt.isEmpty() ? Optional.empty() : opt.get())
            .conv(TDesc.class,
                td -> td.tryGet().map(TypeDesc::toString).orElse(td.getRaw())
            )
            .conv(Sign.class, s -> s.raw)
            .conv( prop -> {
                if( prop.owner instanceof OpcodeProperty && prop.name.equals("opcode") && prop.propValue instanceof Number ){
                    var ocode1 = OpCode.code( ((Number)prop.propValue).intValue() ).map( ocode -> {
                        return ""+ocode+"#"+prop.propValue;
                    });
                    if( ocode1.isPresent() ){
                        return prop.with(ocode1.get(), String.class);
                    }
                }
                return prop;
            })
        ;

        var cbegin = CBegin.parseByteCode(SampleClass.class);
        for( var gn : cbegin.walk() ){
            System.out.print(" ".repeat(gn.level));
            System.out.print(gn.node.getClass().getSimpleName());
            for( var prop : props.decode(gn.node)){
                System.out.print(" "+prop.name+":"+prop.propValue);
            }
            System.out.println();
        }
    }
}
