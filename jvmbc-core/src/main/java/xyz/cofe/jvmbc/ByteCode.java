package xyz.cofe.jvmbc;

import java.io.Serializable;
import java.util.List;

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
}
