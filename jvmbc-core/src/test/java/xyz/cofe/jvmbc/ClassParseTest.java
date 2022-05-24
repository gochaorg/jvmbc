package xyz.cofe.jvmbc;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;

public class ClassParseTest extends CommonForTest {
    @Test
    public void sampleParse(){
        var cbegin = CBegin.parseByteCode(SampleClass.class);
        dump(cbegin);
    }
}
