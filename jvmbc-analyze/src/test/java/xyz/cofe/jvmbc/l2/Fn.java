package xyz.cofe.jvmbc.l2;

import java.io.Serializable;
import java.util.function.Function;

public interface Fn<A,Z> extends Function<A,Z> , Serializable {
}
