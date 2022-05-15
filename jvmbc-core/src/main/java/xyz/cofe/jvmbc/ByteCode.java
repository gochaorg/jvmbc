package xyz.cofe.jvmbc;

import xyz.cofe.jvmbc.tree.GraphIterable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Байт-код инструкция
 */
public interface ByteCode extends Serializable {
    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    default List<ByteCode> nodes(){
        return List.of();
    }

    /**
     * Обход дерева инструкций
     * @return итератор по дереву
     */
    default GraphIterable<ByteCode> walk(){
        return new GraphIterable<>(List.of(this), from -> from.nodes(), Optional.of(x -> true) );
    }
}
