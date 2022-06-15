package xyz.cofe.jvmbc.cls;

import xyz.cofe.jvmbc.ByteCode;

public interface ClsByteCode extends ByteCode, ClazzWriter {
    /**
     * Клонирование.
     *
     * Любой класс реализующий ClsByteCode, обязан переопределить так:
     * <pre>public SELF clone()</pre>
     * где SELF - обязательно тип реализующий ClsByteCode,
     * иначе может сломаться в CBegin
     * @return клон
     */
    public ClsByteCode clone();
}
