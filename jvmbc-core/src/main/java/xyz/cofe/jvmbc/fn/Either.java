package xyz.cofe.jvmbc.fn;

import java.util.Optional;

/**
 * Значение ИЛИ. Определенно или левое или правое значение
 * @param <LEFT> Левое значение
 * @param <RIGHT> Правое значение
 */
public class Either<LEFT,RIGHT> {
    public final LEFT leftValue;
    public final boolean isLeft;

    public final RIGHT rightValue;
    public final boolean isRight;

    private Either( LEFT leftValue, boolean isLeft, RIGHT rightValue, boolean isRight){
        this.isLeft = isLeft;
        this.leftValue = leftValue;

        this.isRight = isRight;
        this.rightValue = rightValue;
    }

    public static <L,R> Either<L,R> left( L leftValue ){
        return new Either<>(leftValue, true, null, false);
    }

    public static <L,R> Either<L,R> right( R rightValue ){
        return new Either<>(null, false, rightValue, true);
    }

    public <A> A ifRightOrNull( F1<RIGHT,A> compute ){
        if( compute==null )throw new IllegalArgumentException( "compute==null" );
        return isRight ?
            compute.apply(rightValue) : null;
    }

    public Optional<RIGHT> rightOpt(){
        return isRight ? Optional.of(rightValue) : Optional.empty();
    }

    public <A> A ifLeftOrNull( F1<LEFT,A> compute ){
        if( compute==null )throw new IllegalArgumentException( "compute==null" );
        return isLeft ?
            compute.apply(leftValue) : null;
    }

    public Optional<LEFT> leftOpt(){
        return isLeft ? Optional.of(leftValue) : Optional.empty();
    }

    public <L> Optional<L> left( F1<LEFT,L> map ){
        if( map==null )throw new IllegalArgumentException( "map==null" );
        return isLeft ? Optional.of(map.apply(leftValue)) : Optional.empty();
    }

    public <R> Optional<R> right( F1<RIGHT,R> map ){
        if( map==null )throw new IllegalArgumentException( "map==null" );
        return isRight ? Optional.of(map.apply(rightValue)) : Optional.empty();
    }
}
