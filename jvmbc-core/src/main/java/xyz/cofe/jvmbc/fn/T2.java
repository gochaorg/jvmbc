package xyz.cofe.jvmbc.fn;

/**
 * Кортеж 2го размера
 */
public interface T2<A,B> {
    A a();
    B b();
    public static <A,B> T2<A,B> of(A a,B b){
        return new T2<A,B>() {
            @Override
            public A a(){
                return a;
            }

            @Override
            public B b(){
                return b;
            }
        };
    }
}
