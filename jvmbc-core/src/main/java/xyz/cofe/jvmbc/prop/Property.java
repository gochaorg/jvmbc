package xyz.cofe.jvmbc.prop;

public interface Property<A> {
    A get();
    AutoCloseable onChange(PropertyListener<A> ls);
    public static <X> MutableProperty<X> mutable( X initial){ return new MutableProperty<>(initial); }
    public static <X> ComputedProperty<X> computable(Compute<X> compute){ return ComputedProperty.capture(compute); }
}