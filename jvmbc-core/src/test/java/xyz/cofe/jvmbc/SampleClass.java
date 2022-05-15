package xyz.cofe.jvmbc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SampleClass<Arg extends Number> {
    private int privateInt = 20;
    protected int protectedInt = 30;
    public int publicInt = 10;
    public final int publicFinalInt = 15;
    public String publicString = "value";
    public final List<String> listOfString = List.of("str");
    public final Map<Integer,List<Integer>> mapIntListOfInt = Map.of(
        1, List.of(10,11),
        2, List.of(20,21)
    );
    public SampleClass<AtomicInteger> intSelf;
    public SampleClass<? extends AtomicInteger> extIntSelf;
    public SampleClass<? super AtomicInteger> ext2IntSelf;
    public String concat(int a,Double b){
        System.out.println("concat of "+a+" "+b);
        return ""+a+""+b;
    }
    public <A extends Number & Runnable, B extends A> void some(A param, B param2){}
}
