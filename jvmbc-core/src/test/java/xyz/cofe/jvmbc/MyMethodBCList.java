package xyz.cofe.jvmbc;

import xyz.cofe.jvmbc.mth.MethodByteCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class MyMethodBCList extends ArrayList<MethodByteCode> implements Serializable {
    public transient long scn = 0;
    protected void changed(){
        scn++;
    }

    @Override
    public MethodByteCode set( int index, MethodByteCode element ){
        var r = super.set(index, element);
        changed();
        return r;
    }

    @Override
    public boolean add( MethodByteCode methodByteCode ){
        var r = super.add(methodByteCode);
        changed();
        return r;
    }

    @Override
    public void add( int index, MethodByteCode element ){
        super.add(index, element);
        changed();
    }

    @Override
    public MethodByteCode remove( int index ){
        var r = super.remove(index);
        changed();
        return r;
    }

    @Override
    public boolean remove( Object o ){
        var r = super.remove(o);
        changed();
        return r;
    }

    @Override
    public void clear(){
        super.clear();
        changed();
    }

    @Override
    public boolean addAll( Collection<? extends MethodByteCode> c ){
        var r = super.addAll(c);
        changed();
        return r;
    }

    @Override
    public boolean addAll( int index, Collection<? extends MethodByteCode> c ){
        var r = super.addAll(index, c);
        changed();
        return r;
    }

    @Override
    protected void removeRange( int fromIndex, int toIndex ){
        super.removeRange(fromIndex, toIndex);
        changed();
    }

    @Override
    public boolean removeAll( Collection<?> c ){
        var r = super.removeAll(c);
        changed();
        return r;
    }

    @Override
    public boolean retainAll( Collection<?> c ){
        var r = super.retainAll(c);
        changed();
        return r;
    }

    @Override
    public boolean removeIf( Predicate<? super MethodByteCode> filter ){
        var r = super.removeIf(filter);
        changed();
        return r;
    }

    @Override
    public void replaceAll( UnaryOperator<MethodByteCode> operator ){
        super.replaceAll(operator);
        changed();
    }

    @Override
    public void sort( Comparator<? super MethodByteCode> c ){
        super.sort(c);
        changed();
    }
}
