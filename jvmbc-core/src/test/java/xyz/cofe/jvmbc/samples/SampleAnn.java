package xyz.cofe.jvmbc.samples;

@Ann1
public class SampleAnn {
    @Ann2("aa")
    int a;

    public SampleAnn(@Ann3 int a){
        this.a = a;
        System.out.println("xx");
    }

    @Ann3(num = 3)
    public void some(){
    }
}
