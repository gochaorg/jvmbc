package xyz.cofe.jvmbc.mth;

/**
 * Свойство - код инструкции
 */
public interface OpcodeProperty {
    /**
     * Возвращает код инструкции
     * @return код инструкции
     */
    public int getOpcode();

    /**
     * Указывает код инструкции
     * @param opcode код инструкции
     */
    public void setOpcode(int opcode);
}
