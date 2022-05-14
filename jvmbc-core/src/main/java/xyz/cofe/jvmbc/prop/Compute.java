package xyz.cofe.jvmbc.prop;

import java.io.Serializable;
import java.util.function.Supplier;

public interface Compute<R> extends Supplier<R>, Serializable {
}

