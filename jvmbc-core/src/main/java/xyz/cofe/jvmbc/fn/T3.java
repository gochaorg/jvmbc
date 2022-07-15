package xyz.cofe.jvmbc.fn;

/**
 * Кортеж 2го размера
 */
public interface T3<A,B,C> {
    A a();
    B b();
    C c();
    public static <A,B,C,D,E,F> T3<A,B,C> of( A a, B b, C c ){
        return new T3<>() {
            @Override
            public A a(){
                return a;
            }

            @Override
            public B b(){
                return b;
            }

            @Override
            public C c(){
                return c;
            }
        };
    }
}
