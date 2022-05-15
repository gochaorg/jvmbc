package xyz.cofe.jvmbc.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Описывает путь в дереве/графе
 * @param <N> Тип узла
 */
public class GraphPath<N> {
    /** предшествующий узел */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public final Optional<GraphPath<N>> parent;

    /** узел */
    public final N node;

    /**
     * Уровень, 0 - корень
     */
    public final int level;

    /**
     * Конструктор
     * @param node узел
     */
    public GraphPath( N node ){
        if( node==null )throw new IllegalArgumentException("node==null");
        this.node = node;
        this.parent = Optional.empty();
        level = 0;
    }

    /**
     * Конструктор
     * @param node узел
     * @param parent предшествующий узел
     */
    public GraphPath( N node, GraphPath<N> parent ){
        if( node==null )throw new IllegalArgumentException("node==null");
        if( parent==null )throw new IllegalArgumentException( "parent==null" );
        this.node = node;
        this.parent = Optional.of(parent);
        level = parent.level+1;
    }

    /**
     * Конструирует следующий путь, от текущего
     * @param next следующий узел
     * @return следующий путь
     */
    public GraphPath<N> next( N next ){
        if( next==null )throw new IllegalArgumentException( "next==null" );
        return new GraphPath<>(next, this);
    }

    /**
     * Представляет в виде списка
     * @return список
     */
    public List<N> toList(){
        ArrayList<N> list = new ArrayList<>(level);
        GraphPath<N> p = this;
        while( p!=null ){
            list.add(0,p.node);
            p = p.parent.orElse(null);
        }
        return list;
    }
}
