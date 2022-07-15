package xyz.cofe.jvmbc.fn;

/**
 * Кортеж 2го размера
 */
public interface T4<A,B,C,D> {
    A a();
    B b();
    C c();
    D d();
    public static <A,B,C,D,E,F> T4<A,B,C,D> of( A a, B b, C c, D d ){
        return new T4<>() {
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

            @Override
            public D d(){
                return d;
            }
        };
    }
}
