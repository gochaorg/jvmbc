package xyz.cofe.jvmbc.l1;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class SimpleLambdaTest {
    @Test
    public void javaLambda01(){
        Function<Function<String,String>,String> test = (f) -> {
            System.out.println("f="+f.getClass());
            return null;
        };
        test.apply( x -> x.repeat(4) );
    }
}
