package xyz.cofe.jvmbc.fn;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface F2<A,B,Z> extends BiFunction<A,B,Z>, Serializable {
}
