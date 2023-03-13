package xyz.cofe.jvmbc.mth

/** Код(номер) инструкции байт-кода */
sealed abstract class OpCode(val code:Int)

object OpCode:
  val values = List[OpCode](
    NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,ICONST_3,
    ICONST_4,ICONST_5,LCONST_0,LCONST_1,
    FCONST_0,FCONST_1,FCONST_2,DCONST_0,DCONST_1,
    BIPUSH,SIPUSH,LDC,
    LDC_W,LDC2_W,
    ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,
    IALOAD,LALOAD,FALOAD,DALOAD,AALOAD,
    BALOAD,CALOAD,SALOAD,ISTORE,LSTORE,
    LSTORE_0,LSTORE_1,LSTORE_2,LSTORE_3,
    FSTORE,FSTORE_0,FSTORE_1,FSTORE_2,FSTORE_3,
    DSTORE,DSTORE_0,DSTORE_1,DSTORE_2,DSTORE_3,
    ASTORE,ASTORE_0,ASTORE_1,ASTORE_2,ASTORE_3,
    IASTORE,LASTORE,FASTORE,DASTORE,AASTORE,
    BASTORE,CASTORE,SASTORE,
    POP,POP2,DUP,DUP_X1,DUP_X2,DUP2,
    DUP2_X1,DUP2_X2,SWAP,IADD,LADD,
    FADD,DADD,ISUB,LSUB,FSUB,DSUB,
    IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,FDIV,DDIV,
    IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,
    DNEG,ISHL,LSHL,ISHR,LSHR,IUSHR,LUSHR,
    IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,
    I2L,I2F,I2D,L2I,L2F,L2D,
    F2I,F2L,F2D,
    D2I,D2L,D2F,
    I2B,I2C,I2S,
    LCMP,FCMPL,FCMPG,DCMPL,DCMPG,
    IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,
    IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,
    IF_ICMPGT,IF_ICMPLE,IF_ACMPEQ,
    IF_ACMPNE,
    GOTO,GOTO_W,
    JSR,RET,
    TABLESWITCH,LOOKUPSWITCH,
    IRETURN,LRETURN,FRETURN,
    DRETURN,ARETURN,RETURN,
    GETSTATIC,PUTSTATIC,
    GETFIELD,PUTFIELD,
    INVOKEVIRTUAL,INVOKESPECIAL,
    INVOKESTATIC,INVOKEINTERFACE,
    INVOKEDYNAMIC,NEW,
    NEWARRAY,ANEWARRAY,ARRAYLENGTH,
    ATHROW,CHECKCAST,
    INSTANCEOF,MONITORENTER,MONITOREXIT,
    MULTIANEWARRAY,IFNULL,IFNONNULL
  )

  /**
   * Поиск инструкции по коду
   * @param code код
   */
  def find(code:Int):Option[? <: OpCode]=values.find(c => c.code==code)

  /**
   * perform no operation
   * <p>Стек [No change]
   */
  object NOP extends OpCode(0)

  /**
   * push a null reference onto the stack
   * <p>Стек → null
   */
  object ACONST_NULL extends OpCode(1)

  /**
   * load the int value −1 onto the stack
   * <p>Стек → -1
   */
  object ICONST_M1 extends OpCode(2)

  /**
   * load the int value 0 onto the stack
   * <p>Стек → 0
   */
  object ICONST_0 extends OpCode(3)

  /**
   * load the int value 1 onto the stack
   * <p>Стек → 1
   */
  object ICONST_1 extends OpCode(4)

  /**
   * load the int value 2 onto the stack
   * <p>Стек → 2
   */
  object ICONST_2 extends OpCode(5) 

  /**
   * load the int value 3 onto the stack
   * <p>Стек → 3
   */
  object ICONST_3 extends OpCode(6) 

  /**
   * load the int value 4 onto the stack
   * <p>Стек → 4
   */
  object ICONST_4 extends OpCode(7) 

  /**
   * load the int value 5 onto the stack
   * <p>Стек → 5
   */
  object ICONST_5 extends OpCode(8) 

  /**
   * push 0L (the number zero with type long) onto the stack
   * <p>Стек → 0
   */
  object LCONST_0 extends OpCode(9) 

  /**
   * push 1L (the number one with type long) onto the stack
   * <p>Стек → 1
   */
  object LCONST_1 extends OpCode(10) 

  /**
   * push 0.0f on the stack
   * <p>Стек → 0f
   */
  object FCONST_0 extends OpCode(11) 

  /**
   * push 1.0f on the stack
   * <p>Стек → 1f
   */
  object FCONST_1 extends OpCode(12) 

  /**
   * push 2.0f on the stack
   * <p>Стек → 2f
   */
  object FCONST_2 extends OpCode(13) 

  /**
   * push 0.0 (double) on the stack
   * <p>Стек → 0.0
   */
  object DCONST_0 extends OpCode(14) 

  /**
   * push 1.0 (double) on the stack
   * <p>Стек → 1.0
   */
  object DCONST_1 extends OpCode(15) 

  /**
   * push a byte onto the stack as an integer value
   * <p>Параметры 1: byte
   * <p>Стек → value
   */
  object BIPUSH extends OpCode(16) // visitIntInsn

  /**
   * push a short onto the stack as an integer value
   * <p>Параметры 2: byte1, byte2
   * <p>Стек → value
   */
  object SIPUSH extends OpCode(17) 

  /**
   * push a constant #index from a constant pool 
   * (String, int, float, Class, java.lang.invoke.MethodType, 
   * java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack
   * <p>Параметры 1: index
   * <p>Стек → value
   */
  object LDC extends OpCode(18) // visitLdcInsn

  /**
   * push a constant #index from a constant pool 
   * (String, int, float, Class, java.lang.invoke.MethodType, 
   * java.lang.invoke.MethodHandle, or a dynamically-computed constant) 
   * onto the stack (wide index is constructed as indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек → value
   */
  object LDC_W extends OpCode(19)

  /**
   * push a constant #index from a constant pool (double, long, or a 
   * dynamically-computed constant) 
   * onto the stack (wide index is constructed as indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек → value
   */
  object LDC2_W extends OpCode(20)

  /**
   * load an int value from a local variable #index
   * <p>Параметры 1: index
   * <p>Стек → value
   */
  object ILOAD extends OpCode(21) // visitVarInsn

  /**
   * load a long value from a local variable #index
   * <p>Параметры 1: index
   * <p>Стек → value
   */
  object LLOAD extends OpCode(22) 

  /**
   * load a float value from a local variable #index
   * <p>Параметры 1: index
   * <p>Стек → value
   */
  object FLOAD extends OpCode(23) 

  /**
   * load a double value from a local variable #index
   * <p>Параметры 1: index
   * <p>Стек → value
   */
  object DLOAD extends OpCode(24) 

  /**
   * load a reference onto the stack from a local variable #index
   * <p>Параметры 1: index
   * <p>Стек → objectref
   */
  object ALOAD extends OpCode(25) 

  /**
   * load an int from an array
   * <p>Стек arrayref, index → value
   */
  object IALOAD extends OpCode(46) // visitInsn

  /**
   * load a long from an array
   * <p>Стек arrayref, index → value
   */
  object LALOAD extends OpCode(47) 

  /**
   * load a float from an array
   * <p>Стек arrayref, index → value
   */
  object FALOAD extends OpCode(48) 

  /**
   * load a double from an array
   * <p>Стек arrayref, index → value
   */
  object DALOAD extends OpCode(49) 

  /**
   * load onto the stack a reference from an array
   * <p>Стек arrayref, index → value
   */
  object AALOAD extends OpCode(50) 

  /**
   * load a byte or Boolean value from an array
   * <p>Стек arrayref, index → value
   */
  object BALOAD extends OpCode(51) 

  /**
   * load a char from an array
   * <p>Стек arrayref, index → value
   */
  object CALOAD extends OpCode(52) 

  /**
   * load short from array
   * <p>Стек arrayref, index → value
   */
  object SALOAD extends OpCode(53) 

  /**
   * load a reference onto the stack from a local variable #index
   * <p>Параметры 1: index
   * <p>Стек → objectref
   */
  object ISTORE extends OpCode(54) // visitVarInsn

  /**
   * store a long value in a local variable #index
   * <p>Параметры 1: index
   * <p>Стек value →
   */
  object LSTORE extends OpCode(55) 

  /**
   * store a long value in a local variable 0
   * <p>Стек value →
   */
  object LSTORE_0 extends OpCode(0x3f)

  /**
   * store a long value in a local variable 1
   * <p>Стек value →
   */
  object LSTORE_1 extends OpCode(0x40)

  /**
   * store a long value in a local variable 2
   * <p>Стек value →
   */
  object LSTORE_2 extends OpCode(0x41) 

  /**
   * store a long value in a local variable 3
   * <p>Стек value →
   */
  object LSTORE_3 extends OpCode(0x42) 

  /**
   * store a float value into a local variable #index
   * <p>Параметры 1: index
   * <p>Стек value →
   */
  object FSTORE extends OpCode(56) 

  /**
   * store a float value into local variable 0
   * <p>Стек value →
   */
  object FSTORE_0 extends OpCode(0x43) 

  /**
   * store a float value into local variable 1
   * <p>Стек value →
   */
  object FSTORE_1 extends OpCode(0x44) 

  /**
   * store a float value into local variable 2
   * <p>Стек value →
   */
  object FSTORE_2 extends OpCode(0x45) 

  /**
   * store a float value into local variable 3
   * <p>Стек value →
   */
  object FSTORE_3 extends OpCode(0x46) 

  /**
   * store a double value into a local variable #index
   * <p>Параметры 1: index
   * <p>Стек value →
   */
  object DSTORE extends OpCode(57)

  /**
   * store a double into local variable 0
   * <p>Параметры 1: index
   * <p>Стек value →
   */
  object DSTORE_0 extends OpCode(0x47)

  /**
   * store a double into local variable 1
   * <p>Параметры 
   * <p>Стек value →
   */
  object DSTORE_1 extends OpCode(0x48)

  /**
   * store a double into local variable 2
   * <p>Стек value →
   */
  object DSTORE_2 extends OpCode(0x49)

  /**
   * store a double into local variable 3
   * <p>Стек value →
   */
  object DSTORE_3 extends OpCode(0x49)

  /**
   * store a reference into a local variable #index
   * <p>Параметры 1: index
   * <p>Стек objectref →
   */
  object ASTORE extends OpCode(58) 

  /**
   * store a reference into local variable 0
   * <p>Стек objectref →
   */
  object ASTORE_0 extends OpCode(0x4b) 

  /**
   * store a reference into local variable 1
   * <p>Стек objectref →
   */
  object ASTORE_1 extends OpCode(0x4c) 

  /**
   * store a reference into local variable 2
   * <p>Стек objectref →
   */
  object ASTORE_2 extends OpCode(0x4d) 

  /**
   * store a reference into local variable 3
   * <p>Стек objectref →
   */
  object ASTORE_3 extends OpCode(0x4e) 

  /**
   * store an int into an array
   * <p>Стек arrayref, index, value →
   */
  object IASTORE extends OpCode(79) // visitInsn

  /**
   * store a long to an array
   * <p>Стек arrayref, index, value →
   */
  object LASTORE extends OpCode(80) 

  /**
   * store a float in an array
   * <p>Стек arrayref, index, value →
   */
  object FASTORE extends OpCode(81) 

  /**
   * store a double into an array
   * <p>Стек arrayref, index, value →
   */
  object DASTORE extends OpCode(82) 

  /**
   * store a reference in an array
   * <p>Стек arrayref, index, value →
   */
  object AASTORE extends OpCode(83) 

  /**
   * store a byte or Boolean value into an array
   * <p>Стек arrayref, index, value →
   */
  object BASTORE extends OpCode(84) 

  /**
   * store a char into an array
   * <p>Стек arrayref, index, value →
   */
  object CASTORE extends OpCode(85) 

  /**
   * store short to array
   * <p>Стек arrayref, index, value →
   */
  object SASTORE extends OpCode(86) 

  /**
   * discard the top value on the stack
   * <p>Стек value →
   */
  object POP extends OpCode(87) 

  /**
   * discard the top two values on the stack (or one value, if it is a double or long)
   * <p>Стек {value2, value1} →
   */
  object POP2 extends OpCode(88) 

  /**
   * duplicate the value on top of the stack
   * <p>Стек value → value, value
   */
  object DUP extends OpCode(89) 

  /**
   * insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long.
   * <p>Стек value2, value1 → value1, value2, value1
   */
  object DUP_X1 extends OpCode(90) 

  /**
   * insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top
   * <p>Стек value3, value2, value1 → value1, value3, value2, value1
   */
  object DUP_X2 extends OpCode(91) 

  /**
   * duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)
   * <p>Стек {value2, value1} → {value2, value1}, {value2, value1}
   */
  object DUP2 extends OpCode(92) 

  /**
   * duplicate two words and insert beneath third word (see explanation above)
   * <p>Стек value3, {value2, value1} → {value2, value1}, value3, {value2, value1}
   */
  object DUP2_X1 extends OpCode(93) 

  /**
   * duplicate two words and insert beneath fourth word
   * <p>Стек {value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1}
   */
  object DUP2_X2 extends OpCode(94) 

  /**
   * swaps two top words on the stack (note that value1 and value2 must not be double or long)
   * <p>Стек value2, value1 → value1, value2
   */
  object SWAP extends OpCode(95) 

  /**
   * add two ints
   * <p>Стек value1, value2 → result
   */
  object IADD extends OpCode(96) 

  /**
   * add two longs
   * <p>Стек value1, value2 → result
   */
  object LADD extends OpCode(97) 

  /**
   * add two floats
   * <p>Стек value1, value2 → result
   */
  object FADD extends OpCode(98) 

  /**
   * add two doubles
   * <p>Стек value1, value2 → result
   */
  object DADD extends OpCode(99) 

  /**
   * int subtract
   * <p>Стек value1, value2 → result
   */
  object ISUB extends OpCode(100) 

  /**
   * subtract two longs
   * <p>Стек value1, value2 → result
   */
  object LSUB extends OpCode(101) 

  /**
   * subtract two floats
   * <p>Стек value1, value2 → result
   */
  object FSUB extends OpCode(102) 

  /**
   * subtract a double from another
   * <p>Стек value1, value2 → result
   */
  object DSUB extends OpCode(103) 

  /**
   * multiply two integers
   * <p>Стек value1, value2 → result
   */
  object IMUL extends OpCode(104) 

  /**
   * multiply two longs
   * <p>Стек value1, value2 → result
   */
  object LMUL extends OpCode(105) 

  /**
   * multiply two floats
   * <p>Стек value1, value2 → result
   */
  object FMUL extends OpCode(106) 

  /**
   * multiply two doubles
   * <p>Стек value1, value2 → result
   */
  object DMUL extends OpCode(107) 

  /**
   * divide two integers
   * <p>Стек value1, value2 → result
   */
  object IDIV extends OpCode(108) 

  /**
   * divide two longs
   * <p>Стек value1, value2 → result
   */
  object LDIV extends OpCode(109) 

  /**
   * divide two floats
   * <p>Стек value1, value2 → result
   */
  object FDIV extends OpCode(110) 

  /**
   * divide two doubles
   * <p>Стек value1, value2 → result
   */
  object DDIV extends OpCode(111) 

  /**
   * logical int remainder
   * <p>Стек value1, value2 → result
   */
  object IREM extends OpCode(112) 

  /**
   * remainder of division of two longs
   * <p>Стек value1, value2 → result
   */
  object LREM extends OpCode(113) 

  /**
   * get the remainder from a division between two floats
   * <p>Стек value1, value2 → result
   */
  object FREM extends OpCode(114) 

  /**
   * get the remainder from a division between two doubles
   * <p>Стек value1, value2 → result
   */
  object DREM extends OpCode(115) 

  /**
   * negate int
   * <p>Стек value → result
   */
  object INEG extends OpCode(116) 

  /**
   * negate a long
   * <p>Стек value → result
   */
  object LNEG extends OpCode(117) 

  /**
   * negate a float
   * <p>Стек value → result
   */
  object FNEG extends OpCode(118) 

  /**
   * negate a double
   * <p>Стек value → result
   */
  object DNEG extends OpCode(119) 

  /**
   * int shift left
   * <p>Стек value1, value2 → result
   */
  object ISHL extends OpCode(120) 

  /**
   * bitwise shift left of a long value1 by int value2 positions
   * <p>Стек value1, value2 → result
   */
  object LSHL extends OpCode(121) 

  /**
   * int arithmetic shift right
   * <p>Стек value1, value2 → result
   */
  object ISHR extends OpCode(122) 

  /**
   * bitwise shift right of a long value1 by int value2 positions
   * <p>Стек value1, value2 → result
   */
  object LSHR extends OpCode(123) 

  /**
   * int logical shift right
   * <p>Стек value1, value2 → result
   */
  object IUSHR extends OpCode(124) 

  /**
   * bitwise shift right of a long value1 by int value2 positions, unsigned
   * <p>Стек value1, value2 → result
   */
  object LUSHR extends OpCode(125) 

  /**
   * perform a bitwise AND on two integers
   * <p>Стек value1, value2 → result
   */
  object IAND extends OpCode(126) 

  /**
   * bitwise AND of two longs
   * <p>Стек value1, value2 → result
   */
  object LAND extends OpCode(127) 

  /**
   * bitwise int OR
   * <p>Стек value1, value2 → result
   */
  object IOR extends OpCode(128) 

  /**
   * bitwise OR of two longs
   * <p>Стек value1, value2 → result
   */
  object LOR extends OpCode(129) 

  /**
   * int xor
   * <p>Стек value1, value2 → result
   */
  object IXOR extends OpCode(130) 

  /**
   * bitwise XOR of two longs
   * <p>Стек value1, value2 → result
   */
  object LXOR extends OpCode(131) 

  /**
   * increment local variable #index by signed byte const
   * <p>Параметры 2: index, const
   * <p>Стек [No change]
   */
  object IINC extends OpCode(132) // visitIincInsn

  /**
   * convert an int into a long
   * <p>Стек value → result
   */
  object I2L extends OpCode(133) // visitInsn

  /**
   * convert an int into a float
   * <p>Стек value → result
   */
  object I2F extends OpCode(134) 

  /**
   * convert an int into a double
   * <p>Стек value → result
   */
  object I2D extends OpCode(135) 

  /**
   * convert an long into a int
   * <p>Стек value → result
   */
  object L2I extends OpCode(136) 

  /**
   * convert an long into a float
   * <p>Стек value → result
   */
  object L2F extends OpCode(137) 

  /**
   * convert an long into a double
   * <p>Стек value → result
   */
  object L2D extends OpCode(138) 

  /**
   * convert an float into a int
   * <p>Стек value → result
   */
  object F2I extends OpCode(139) 

  /**
   * convert an float into a long
   * <p>Стек value → result
   */
  object F2L extends OpCode(140) 

  /**
   * convert an float into a double
   * <p>Стек value → result
   */
  object F2D extends OpCode(141) 

  /**
   * convert an double into a int
   * <p>Стек value → result
   */
  object D2I extends OpCode(142) 

  /**
   * convert an double into a long
   * <p>Стек value → result
   */
  object D2L extends OpCode(143) 

  /**
   * convert an double into a float
   * <p>Стек value → result
   */
  object D2F extends OpCode(144) 

  /**
   * convert an int into a byte
   * <p>Стек value → result
   */
  object I2B extends OpCode(145) 

  /**
   * convert an int into a character
   * <p>Стек value → result
   */
  object I2C extends OpCode(146) 

  /**
   * convert an int into a short
   * <p>Стек value → result
   */
  object I2S extends OpCode(147) 

  /**
   * push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise
   * <p>Стек value1, value2 → result
   */
  object LCMP extends OpCode(148) 

  /**
   * compare two floats, -1 on NaN
   * <p>Стек value1, value2 → result
   */
  object FCMPL extends OpCode(149) 

  /**
   * compare two floats, 1 on NaN
   * <p>Стек value1, value2 → result
   */
  object FCMPG extends OpCode(150) 

  /**
   * compare two doubles, -1 on NaN
   * <p>Стек value1, value2 → result
   */
  object DCMPL extends OpCode(151) 

  /**
   * compare two doubles, 1 on NaN
   * <p>Стек value1, value2 → result
   */
  object DCMPG extends OpCode(152) 

  /**
   * if value is 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   */
  object IFEQ extends OpCode(153) // visitJumpInsn

  /**
   * if value is not 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   */
  object IFNE extends OpCode(154) 

  /**
   * if value is less than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   */
  object IFLT extends OpCode(155) 

  /**
   * if value is greater than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   */
  object IFGE extends OpCode(156) 

  /**
   * if value is greater than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   */
  object IFGT extends OpCode(157) 

  /**
   * if value is less than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   */
  object IFLE extends OpCode(158) 

  /**
   * if ints are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ICMPEQ extends OpCode(159) 

  /**
   * if ints are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ICMPNE extends OpCode(160) 

  /**
   * if value1 is less than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ICMPLT extends OpCode(161) 

  /**
   * if value1 is greater than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ICMPGE extends OpCode(162) 

  /**
   * if value1 is greater than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ICMPGT extends OpCode(163) 

  /**
   * if value1 is less than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ICMPLE extends OpCode(164) 

  /**
   * if references are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ACMPEQ extends OpCode(165) 

  /**
   * if references are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value1, value2 →
   */
  object IF_ACMPNE extends OpCode(166) 

  /**
   * goes to another instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек [no change]
   */
  object GOTO extends OpCode(167) 

  /**
   * goes to another instruction at branchoffset (signed int constructed from unsigned bytes branchbyte1 &lt;&lt; 24 | branchbyte2 &lt;&lt; 16 | branchbyte3 &lt;&lt; 8 | branchbyte4)
   * <p>Параметры 4: branchbyte1, branchbyte2, branchbyte3, branchbyte4
   * <p>Стек [no change]
   */
  object GOTO_W extends OpCode(0xc8)

  /**
   * jump to subroutine at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2) and place the return address on the stack
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек → address
   * <hr>
   * <b>Description</b>
   * <p> The address of the opcode of the instruction immediately following this jsr instruction is pushed onto the operand stack as a value of type returnAddress. The unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, where the offset is (branchbyte1 &lt;&lt; 8) | branchbyte2. Execution proceeds at that offset from the address of this jsr instruction. The target address must be that of an opcode of an instruction within the method that contains this jsr instruction.
   * <p> <b>Notes</b>
   * <p> Note that jsr pushes the address onto the operand stack and ret (§ret) gets it out of a local variable. This asymmetry is intentional.
   * <p> In Oracle's implementation of a compiler for the Java programming language prior to Java SE 6, the jsr instruction was used with the ret instruction in the implementation of the finally clause (§3.13, §4.10.2.5).
   * 
   * <hr>
   * <b>Описание</b>
   * <p> Адрес кода операции инструкции, следующей сразу за этой инструкцией jsr, помещается в стек операндов как значение типа returnAddress. Беззнаковые branchbyte1 и branchbyte2 используются для создания подписанного 16-битного смещения, где смещение равно (branchbyte1 &lt;&lt; 8) | branchbyte2. Выполнение продолжается с этого смещения от адреса этой инструкции jsr. Целевой адрес должен соответствовать коду операции инструкции в методе, который содержит эту инструкцию jsr.
   * <p> <b>Примечания</b>
   * <p> Обратите внимание, что jsr помещает адрес в стек операндов, а ret (§ret) получает его из локальной переменной. Эта асимметрия преднамеренная.
   * <p> В реализации Oracle компилятора для языка программирования Java до Java SE 6 инструкция jsr использовалась с инструкцией ret в реализации предложения finally (§3.13, §4.10.2.5).
   */
  object JSR extends OpCode(168) 

  /**
   * continue execution from address taken from a local variable #index (the asymmetry with jsr is intentional)
   * <p>Параметры 1: index
   * <p>Стек [No change]
   * <hr>
   * Обратите внимание, что jsr (§jsr) помещает адрес в стек операндов, а ret получает его из локальной переменной. Эта асимметрия преднамеренная.
   * <p> В реализации Oracle компилятора для языка программирования Java до Java SE 6 инструкция ret использовалась с инструкциями jsr и jsr_w (§jsr, §jsr_w) в реализации предложения finally (§3.13, §4.10.2.5) ).
   * <p> Инструкцию ret не следует путать с инструкцией return (§return). Команда возврата возвращает управление от метода вызывающей стороне, не передавая никакого значения обратно вызывающей стороне.
   * <p> Код операции ret может использоваться вместе с инструкцией wide (§wide) для доступа к локальной переменной с использованием двухбайтового беззнакового индекса.
   */
  object RET extends OpCode(169) // visitVarInsn


  /**
   * continue execution from an address in the table at offset index
   * <p>Параметры 16+: [0–3 bytes padding], defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4, jump offsets...
   * <p>Стек index →
   */
  object TABLESWITCH extends OpCode(170) // visiTableSwitchInsn


  /**
   * a target address is looked up from a table using a key and execution continues from the instruction at that address
   * <p>Параметры 8+: &lt;0 - 3 bytes padding&gt;, defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, npairs1, npairs2, npairs3, npairs4, match-offset pairs...
   * <p>Стек key →
   */
  object LOOKUPSWITCH extends OpCode(171) // visitLookupSwitch

  /**
   * return an integer from a method
   * <p>Стек value → [empty]
   */
  object IRETURN extends OpCode(172) // visitInsn

  /**
   * return a long value
   * <p>Стек value → [empty]
   */
  object LRETURN extends OpCode(173) 

  /**
   * return a float
   * <p>Стек value → [empty]
   */
  object FRETURN extends OpCode(174) 

  /**
   * return a double from a method
   * <p>Стек value → [empty]
   */
  object DRETURN extends OpCode(175) 

  /**
   * return a reference from a method
   * <p>Стек objectref → [empty]
   */
  object ARETURN extends OpCode(176) 

  /**
   * return void from method
   * <p>Стек → [empty]
   */
  object RETURN extends OpCode(177) 

  /**
   * get a static field value of a class, where the field is identified by field reference in the constant pool index (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек → value
   */
  object GETSTATIC extends OpCode(178) // visitFieldInsn

  /**
   * set static field to value in a class, where the field is identified by a field reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек value →
   */
  object PUTSTATIC extends OpCode(179) 

  /**
   * get a field value of an object objectref, where the field is identified by field reference in the constant pool index (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек objectref → value
   */
  object GETFIELD extends OpCode(180) 

  /**
   * set field to value in an object objectref, where the field is identified by a field reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек objectref, value →
   */
  object PUTFIELD extends OpCode(181) 

  /**
   * invoke virtual method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек objectref, [arg1, arg2, ...] → result
   */
  object INVOKEVIRTUAL extends OpCode(182) // visitMethodInsn

  /**
   * invoke instance method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек objectref, [arg1, arg2, ...] → result
   */
  object INVOKESPECIAL extends OpCode(183) 

  /**
   * invoke a static method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек [arg1, arg2, ...] → result
   */
  object INVOKESTATIC extends OpCode(184) 

  /**
   * invokes an interface method on object objectref and puts the result on the stack (might be void); the interface method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 4: indexbyte1, indexbyte2, count, 0
   * <p>Стек objectref, [arg1, arg2, ...] → result
   */
  object INVOKEINTERFACE extends OpCode(185) 

  /**
   * invokes a dynamic method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 4: indexbyte1, indexbyte2, 0, 0
   * <p>Стек [arg1, arg2, ...] → result
   */
  object INVOKEDYNAMIC extends OpCode(186) // visitInvokeDynamicInsn

  /**
   * create new object of type identified by class reference in constant pool index (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек → objectref
   */
  object NEW extends OpCode(187) // visitTypeInsn

  /**
   * create new array with count elements of primitive type identified by atype
   * <p>Параметры 1: atype
   * <p>Стек count → arrayref
   */
  object NEWARRAY extends OpCode(188) // visitIntInsn

  /**
   * create a new array of references of length count and component type identified by the class reference index (indexbyte1 &lt;&lt; 8 | indexbyte2) in the constant pool
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек count → arrayref
   */
  object ANEWARRAY extends OpCode(189) // visitTypeInsn

  /**
   * get the length of an array
   * <p>Стек arrayref → length
   */
  object ARRAYLENGTH extends OpCode(190) // visitInsn

  /**
   * throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)
   * <p>Стек objectref → [empty], objectref
   */
  object ATHROW extends OpCode(191) 

  /**
   * checks whether an objectref is of a certain type, the class reference of which is in the constant pool at index (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек objectref → objectref
   */
  object CHECKCAST extends OpCode(192) // visitTypeInsn

  /**
   * determines if an object objectref is of a given type, identified by class reference index in constant pool (indexbyte1 &lt;&lt; 8 | indexbyte2)
   * <p>Параметры 2: indexbyte1, indexbyte2
   * <p>Стек objectref → result
   */
  object INSTANCEOF extends OpCode(193) 

  /**
   * enter monitor for object ("grab the lock" – start of synchronized() section)
   * <p>Стек objectref → 
   */
  object MONITORENTER extends OpCode(194) // visitInsn

  /**
   * exit monitor for object ("release the lock" – end of synchronized() section)
   * <p>Стек objectref → 
   */
  object MONITOREXIT extends OpCode(195) 

  /**
   * create a new array of dimensions dimensions of type identified by class reference in constant pool index 
   * (indexbyte1 &lt;&lt; 8 | indexbyte2); the sizes of each dimension is identified by count1, [count2, etc.]
   * <p>Параметры 3: indexbyte1, indexbyte2, dimensions
   * <p>Стек count1, [count2,...] → arrayref
   * <p><a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.multianewarray">Из оф доки</a>
   * Format
   * 
   * <pre>
   * multianewarray
   * indexbyte1
   * indexbyte2
   * dimensions
   * </pre>
   * 
   * Forms
   * <pre>
   * multianewarray = 197 (0xc5)</pre>
   * 
   * Operand Stack
   * <pre>
   * .., count1, [count2, ...] →
   * .., arrayref
   * </pre>
   * 
   * Description
   * 
   * <p>
   * The dimensions operand is an unsigned byte that must be greater than or equal to 1. It represents the number of dimensions of the array to be created. The operand stack must contain dimensions values. Each such value represents the number of components in a dimension of the array to be created, must be of type int, and must be non-negative. The count1 is the desired length in the first dimension, count2 in the second, etc.
   * 
   * <p>
   * All of the count values are popped off the operand stack. The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at the index must be a symbolic reference to a class, array, or interface type. The named class, array, or interface type is resolved (§5.4.3.1). The resulting entry must be an array class type of dimensionality greater than or equal to dimensions.
   * 
   * <p>
   * A new multidimensional array of the array type is allocated from the garbage-collected heap. If any count value is zero, no subsequent dimensions are allocated. The components of the array in the first dimension are initialized to subarrays of the type of the second dimension, and so on. The components of the last allocated dimension of the array are initialized to the default initial value (§2.3, §2.4) for the element type of the array type. A reference arrayref to the new array is pushed onto the operand stack.
   * 
   * <p>
   * Linking Exceptions
   * 
   * <p>During resolution of the symbolic reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be thrown.
   * <p>Otherwise, if the current class does not have permission to access the element type of the resolved array class, multianewarray throws an IllegalAccessError.
   * 
   * 
   * <p>Run-time Exception
   * 
   * <p>Otherwise, if any of the dimensions values on the operand stack are less than zero, the multianewarray instruction throws a NegativeArraySizeException.
   * 
   * <p>Notes
   * 
   * <p>It may be more efficient to use newarray or anewarray (§newarray, §anewarray) when creating an array of a single dimension.
   * <p>The array class referenced via the run-time constant pool may have more dimensions than the dimensions operand of the multianewarray instruction. In that case, only the first dimensions of the dimensions of the array are created.
   */
  object MULTIANEWARRAY extends OpCode(197) // visitMultiANewArrayInsn

  /**
   * if value is null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   * <p><a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.ifnull">Из оф доки</a>
   * Description
   * <p>The value must of type reference. It is popped from the operand stack. If value is null, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, where the offset is calculated to be (branchbyte1 &lt;&lt; 8) | branchbyte2. Execution then proceeds at that offset from the address of the opcode of this ifnull instruction. The target address must be that of an opcode of an instruction within the method that contains this ifnull instruction.
   * <p>Otherwise, execution proceeds at the address of the instruction following this ifnull instruction.
   */
  object IFNULL extends OpCode(198) // visitJumpInsn

  /**
   * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.ifnonnull">Официальная дока</a>
   * if value is not null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 &lt;&lt; 8 | branchbyte2)
   * <p>Параметры 2: branchbyte1, branchbyte2
   * <p>Стек value →
   * <p>Description
   * <p>The value must be of type reference. It is popped from the operand stack. If value is not null, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, where the offset is calculated to be (branchbyte1 &lt;&lt; 8) | branchbyte2. Execution then proceeds at that offset from the address of the opcode of this ifnonnull instruction. The target address must be that of an opcode of an instruction within the method that contains this ifnonnull instruction.
   * <p>Otherwise, execution proceeds at the address of the instruction following this ifnonnull instruction.
   */
  object IFNONNULL extends OpCode(199) 
