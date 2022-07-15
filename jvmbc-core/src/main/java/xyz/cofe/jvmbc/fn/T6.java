package xyz.cofe.jvmbc.fn;

/**
 * Кортеж 2го размера
 */
public interface T6<A,B,C,D,E,F> {
    A a();
    B b();
    C c();
    D d();
    E e();
    F f();
    public static <A,B,C,D,E,F> T6<A,B,C,D,E,F> of(A a, B b, C c, D d, E e, F f){
        return new T6<>() {
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

            @Override
            public F f(){
                return f;
            }
        };
    }
}
