package xyz.cofe.jvmbc;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;

import java.lang.reflect.InvocationTargetException;

public class ModifyMethodTest extends CommonForTest {
    @Test
    public void callAsIs(){
        var bc = CBegin.parseByteCode(SampleClass.class);
        try{
            var cls = Class.forName(bc.javaName().getName(),true,cloader(bc));
            var inst = cls.getConstructor().newInstance();

            var meth = cls.getDeclaredMethod("concat",int.class, Double.class);
            var res = meth.invoke(inst,10, 12.0);
            System.out.println(res);
        } catch( ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e ){
            e.printStackTrace();
        }
    }

    @Test
    public void cloneMethod(){
        var bc = CBegin.parseByteCode(SampleClass.class);
        var srcMethOpt = bc.getMethods().stream().filter(m -> m.getName().equals("concat")).findFirst();
        var srcMeth = srcMethOpt.get();

        var cloneMeth = srcMeth.clone();
        cloneMeth.setName("concat_clone");
        bc.getMethods().add(cloneMeth);
        try{
            var cls = Class.forName(bc.javaName().getName(),true,cloader(bc));
            var inst = cls.getConstructor().newInstance();

            var meth = cls.getDeclaredMethod("concat_clone",int.class, Double.class);
            var res = meth.invoke(inst,10, 12.0);
            System.out.println(res);
        } catch( ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e ){
            e.printStackTrace();
        }
    }

    public ClassLoader cloader(CBegin<?,?,?> cBegin){
        var parent = ModifyMethodTest.class.getClassLoader();
        return new ClassLoader() {
            @Override
            public Class<?> loadClass( String name ) throws ClassNotFoundException{
                if( cBegin.javaName().getName().equals(name) ){
                    var bytes = cBegin.toByteCode();
                    return defineClass(cBegin.javaName().getName(), bytes,0,bytes.length);
                }
                return parent.loadClass(name);
            }
        };
    }
}
