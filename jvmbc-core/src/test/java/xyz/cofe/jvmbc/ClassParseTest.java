package xyz.cofe.jvmbc;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;

public class ClassParseTest {

    @Test
    public void sampleParse(){
        var cbegin = CBegin.parseByteCode(SampleClass.class);
        System.out.println(cbegin);

        for( var cfld : cbegin.getFields() ){
            System.out.println(cfld);
        }

        for( var cmeth : cbegin.getMethods() ){
            System.out.println(cmeth);
        }
    }
}
