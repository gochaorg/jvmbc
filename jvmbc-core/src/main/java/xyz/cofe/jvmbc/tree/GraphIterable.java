package xyz.cofe.jvmbc.tree;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class GraphIterable<N> implements Iterable<GraphPath<N>> {
    private final List<N> from;
    private final Function<N,Iterable<N>> follow;
    @SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
    private final Optional<Predicate<GraphPath<N>>> followable;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public GraphIterable( List<N> from, Function<N,Iterable<N>> follow, Optional<Predicate<GraphPath<N>>> followable){
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( follow==null )throw new IllegalArgumentException( "follow==null" );
        //noinspection OptionalAssignedToNull
        if( followable==null )throw new IllegalArgumentException( "followable==null" );
        this.follow = follow;
        this.followable = followable;
        this.from = from;
    }

    @Override
    public GraphIterator<N> iterator(){
        return new GraphIterator<>(from, follow, followable);
    }

    public GraphIterable<N> withFollowable( Predicate<GraphPath<N>> followable ){
        if( followable==null )throw new IllegalArgumentException( "followable==null" );
        return new GraphIterable<N>(from, follow, Optional.of(followable));
    }

    public GraphIterable<N> withFollow( Function<N,Iterable<N>> follow ){
        if( follow==null )throw new IllegalArgumentException( "follow==null" );
        return new GraphIterable<N>(from, follow, followable);
    }
}
