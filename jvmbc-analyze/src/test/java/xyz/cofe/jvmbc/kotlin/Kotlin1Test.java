package xyz.cofe.jvmbc.kotlin;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.jvmbc.clss.CDump;

public class Kotlin1Test {
    @Test
    public void test01(){
        //File file = new File()
        var url1 = Kotlin1Test.class.getResource("/kotlin/MainKt.class");
        if( url1==null )return;

        var cb = CBegin.parseByteCode(url1);
        CDump.dump(cb);
    }
}
