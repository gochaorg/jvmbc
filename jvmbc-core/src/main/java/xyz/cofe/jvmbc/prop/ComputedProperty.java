package xyz.cofe.jvmbc.prop;

import xyz.cofe.jvmbc.close.Closeables;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class ComputedProperty<A> implements Property<A> {
    protected final Closeables listenProps = new Closeables();

    protected void listen( Property<?> ... props ){
        for( var prop : props ){
            prop.onChange( (p)->{
                if( computed ){
                    computed = false;
                    fireChanged();
                }
            });
        }
    }

    protected void listen( Iterable<Property<?>> props ){
        for( var prop : props ){
            prop.onChange( (p)->{
                if( computed ){
                    computed = false;
                    fireChanged();
                }
            });
        }
    }

    protected abstract Supplier<A> eval();

    protected A value;
    protected boolean computed = false;

    @Override
    public A get(){
        if( computed ){
            return value;
        }else{
            value = recurProtectEval( eval() );
            computed = true;
            return value;
        }
    }

    protected final ThreadLocal<Integer> recurLevel = ThreadLocal.withInitial(() -> 0);

    protected A recurProtectEval( Supplier<A> eval ){
        try{
            var level = recurLevel.get();
            if( level>0 ){
                throw new IllegalStateException("recursive compute value");
            }
            recurLevel.set(level+1);
            return eval.get();
        } finally {
            var level = recurLevel.get();
            recurLevel.set(level-1);
        }
    }

    protected void fireChanged(){
        for( var ls:listeners ){
            ls.propertyChanged(this);
        }
    }

    protected final Set<PropertyListener<A>> listeners = new HashSet<>();

    protected void remove(PropertyListener<A> ls){
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

    public static <R> ComputedProperty<R> capture( Compute<R> computing ){
        if( computing==null )throw new IllegalArgumentException( "computing==null" );

        Method writeReplace = null;
        try{
            writeReplace = computing.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) writeReplace.invoke(computing);

            var props = new ArrayList<Property<?>>();

            for( var ai=0; ai<sl.getCapturedArgCount(); ai++ ){
                var arg = sl.getCapturedArg(ai);
                if( arg instanceof Property ){
                    props.add((Property<?>) arg);
                }
            }

            var computedProp = new ComputedProperty<R>(){
                @Override
                protected Supplier<R> eval(){
                    return computing;
                }
            };

            computedProp.listen(props);

            return computedProp;
        } catch( NoSuchMethodException | InvocationTargetException | IllegalAccessException e ){
            throw new Error(e);
        }
    }
}