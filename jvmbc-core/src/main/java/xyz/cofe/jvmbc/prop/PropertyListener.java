package xyz.cofe.jvmbc.prop;

public interface PropertyListener<A> {
    void propertyChanged(Property<A> property);
}