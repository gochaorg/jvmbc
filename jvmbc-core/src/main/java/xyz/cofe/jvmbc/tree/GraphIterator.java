package xyz.cofe.jvmbc.tree;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Итератор по графу
 * @param <N> тип узла
 */
public class GraphIterator<N> implements Iterator<GraphPath<N>> {
    private final List<GraphPath<N>> workset;
    public final Iterable<N> from;
    public final Function<N,Iterable<N>> follow;
    public final Predicate<GraphPath<N>> followable;

    private GraphIterator( List<GraphPath<N>> workset, Iterable<N> from, Function<N,Iterable<N>> follow, Predicate<GraphPath<N>> followable ) {
        this.workset = workset;
        this.from = from;
        this.followable = followable;
        this.follow = follow;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public GraphIterator( Iterable<N> from, Function<N,Iterable<N>> follow, Optional<Predicate<GraphPath<N>>> followable ){
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( follow==null )throw new IllegalArgumentException( "follow==null" );
        this.workset = new ArrayList<>();
        this.followable = followable.orElse( x -> true );
        this.from = from;
        this.follow = follow;
        for( var n : from ){
            if( n!=null ){
                workset.add(new GraphPath<>(n));
            }
        }
    }

    public GraphIterator( Iterable<N> from, Function<N,Iterable<N>> follow ){
        this(from,follow,Optional.empty());
    }

    public GraphIterator( Iterable<N> from, Function<N,Iterable<N>> follow, Predicate<GraphPath<N>> followable ){
        this(from,follow,Optional.of(followable));
    }

    public GraphIterator( N from, Function<N,Iterable<N>> follow ){
        this(from==null ? List.of() : List.of(from),follow,Optional.empty());
    }

    public GraphIterator( N from, Function<N,Iterable<N>> follow, Predicate<GraphPath<N>> followable ){
        this(from==null ? List.of() : List.of(from),follow,Optional.of(followable));
    }

    @Override
    public boolean hasNext(){
        return !workset.isEmpty();
    }

    @Override
    public GraphPath<N> next(){
        if( workset.isEmpty() )throw new NoSuchElementException();
        var n = workset.remove(0);
        var it = follow.apply(n.node);
        if( it!=null && followable.test(n) ){
            var ls = new ArrayList<GraphPath<N>>();
            for( var f : it ){
                if( f!=null ){
                    ls.add(n.next(f));
                }
            }
            workset.addAll(0,ls);
        }
        return n;
    }

    public GraphIterator<N> withFollowable( Predicate<GraphPath<N>> followable ){
        if( followable==null )throw new IllegalArgumentException( "followable==null" );
        return new GraphIterator<>(new ArrayList<>(workset), from, follow, followable );
    }

    public GraphIterator<N> withFollow( Function<N,Iterable<N>> follow ){
        if( followable==null )throw new IllegalArgumentException( "follow==null" );
        return new GraphIterator<>(new ArrayList<>(workset), from, follow, followable );
    }
}
