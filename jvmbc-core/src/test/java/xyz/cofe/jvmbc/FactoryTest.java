package xyz.cofe.jvmbc;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.jvmbc.cls.ClassFactory;
import xyz.cofe.jvmbc.mth.MCode;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FactoryTest extends CommonForTest {
    @Test
    public void defaultFactory(){
        var cbegin = CBegin
            .parseByteCode(SampleClass.class, new ClassFactory.Default());
        dump(cbegin);
    }

    @Test
    public void customFactory(){
        var factory = ClassFactory.create(MyMethodBCList::new)
            .defaults()
            .cbegin(MyCBegin::new)
            .build();

        var cbegin = CBegin.parseByteCode(
            SampleClass.class,
            factory
        );

        var meth = cbegin.getMethods().get(0);
        var scn0 = meth.getMethodByteCodes().scn;

        meth.getMethodByteCodes().add( new MCode());

        var scn1 = meth.getMethodByteCodes().scn;
        System.out.println(""+scn0+" "+scn1);

        assertTrue(scn0!=scn1);
    }
}
