package xyz.cofe.jvmbc.prop;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class MutableProperty<A> implements Property<A> {
    private A value;

    public MutableProperty( A initial){
        this.value = initial;
    }

    @Override
    public A get(){
        return value;
    }

    public void set(A value){
        this.value = value;
        for( var ls:listeners ){
            ls.propertyChanged(this);
        }
    }

    private final Set<PropertyListener<A>> listeners = new HashSet<>();

    private void remove(PropertyListener<A> ls){
        listeners.remove(ls);
    }

    @Override
    public AutoCloseable onChange( PropertyListener<A> ls ){
        if( ls==null )throw new IllegalArgumentException("ls==null");
        listeners.add(ls);
        var wref = new WeakReference<>(ls);
        return () -> {
            var ref = wref.get();
            if( ref!=null ){
                remove(ref);
            }
            wref.clear();
        };
    }
}