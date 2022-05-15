package xyz.cofe.jvmbc.cls;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;

public interface EmptyList<LIST extends List<N>, N> extends Supplier<LIST>, Serializable {
}
