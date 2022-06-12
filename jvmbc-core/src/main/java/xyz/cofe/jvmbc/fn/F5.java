package xyz.cofe.jvmbc.fn;

import java.io.Serializable;

public interface F5<A,B,C,D,E,Z> extends Serializable {
    Z apply(A a,B b,C c,D d,E e);
}
