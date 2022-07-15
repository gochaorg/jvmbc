package xyz.cofe.jvmbc.fn;

/**
 * Кортеж 1го размера
 */
public interface T1<A> {
    A a();
    public static <A> T1<A> of(A a){
        return new T1<A>() {
            @Override
            public A a(){
                return a;
            }
        };
    }
}
