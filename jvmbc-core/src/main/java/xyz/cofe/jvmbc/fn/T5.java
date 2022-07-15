package xyz.cofe.jvmbc.fn;

/**
 * Кортеж 2го размера
 */
public interface T5<A,B,C,D,E> {
    A a();
    B b();
    C c();
    D d();
    E e();
    public static <A,B,C,D,E,F> T5<A,B,C,D,E> of( A a, B b, C c, D d, E e ){
        return new T5<>() {
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

            @Override
            public E e(){
                return e;
            }
        };
    }
}
