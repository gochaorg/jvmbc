package xyz.cofe.jvmbc.fn;

import java.io.Serializable;

public interface F4<A,B,C,D,Z> extends Serializable {
    Z apply(A a,B b,C c,D d);
}
