package xyz.cofe.jvmbc.fn;

import java.io.Serializable;

public interface F3<A,B,C,Z> extends Serializable {
    Z apply(A a,B b,C c);
}
