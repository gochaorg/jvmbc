package xyz.cofe.jvmbc;

import java.util.List;
import java.util.Map;

public class SampleClass {
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
}
