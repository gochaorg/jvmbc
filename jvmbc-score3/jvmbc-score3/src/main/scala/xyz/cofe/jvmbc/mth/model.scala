package xyz.cofe.jvmbc
package mth

import ann.AnnCode
import bm.BootstrapArg
import bm.LdcType

sealed trait MethCode extends ByteCode

/** Начала байт-кода метода */
case class MCode() extends MethCode

/** Конец метода */
case class MEnd() extends MethCode

/**
 * Простая инструкция
 *
 * the opcode of the instruction to be visited. This opcode is either
 * 
 * | Мнемоника    | Код | Параметры | Стек | Описание |
 * |--------------|-----|-----------|------|----------|
 * | NOP          | 00  | c | [No change] | perform no operation |
 * | ACONST_NULL  | 01  |   | → null                   | push a null reference onto the stack |
 * | ICONST_M1    | 02  |   | → -1                     | load the int value −1 onto the stack |
 * | ICONST_0     | 03  |   | → 0                      | load the int value 0 onto the stack |
 * | ICONST_1     | 04  |   | → 1                      | load the int value 1 onto the stack |
 * | ICONST_2     | 05  |   | → 2                      | load the int value 2 onto the stack |
 * | ICONST_3     | 06  |   | → 3                      | load the int value 3 onto the stack |
 * | ICONST_4     | 07  |   | → 4                      | load the int value 4 onto the stack |
 * | ICONST_5     | 08  |   | → 5                      | load the int value 5 onto the stack |
 * | LCONST_0     | 09  |   | → 0L                     | push 0L (the number zero with type long) onto the stack |
 * | LCONST_1     | 0a  |   | → 1L                     | push 1L (the number one with type long) onto the stack |
 * | FCONST_0     | 0b  |   | → 0.0f                   | push 0.0f on the stack |
 * | FCONST_1     | 0c  |   | → 1.0f                   | push 1.0f on the stack |
 * | FCONST_2     | 0d  |   | → 2.0f                   | push 2.0f on the stack |
 * | DCONST_0     | 0e  |   | → 0.0                    | push 0.0 (double) on the stack |
 * | DCONST_1     | 0f  |   | → 1.0                    | push 1.0 (double) on the stack |
 * | IALOAD       | 2e  |   | arrayref, index → value  | load an int from an array |
 * | LALOAD       | 2f  |   | arrayref, index → value  | load a long from an array |
 * | FALOAD       | 30  |   | arrayref, index → value  | load a float from an array |
 * | DALOAD       | 31  |   | arrayref, index → value  | load a double from an array |
 * | AALOAD       | 32  |   | arrayref, index → value  | load onto the stack a reference from an array |
 * | BALOAD       | 33  |   | arrayref, index → value  | load a byte or Boolean value from an array |
 * | CALOAD       | 34  |   | arrayref, index → value  | load a char from an array |
 * | SALOAD       | 35  |   | arrayref, index → value  | load short from array |
 * | IASTORE      | 4f  |   | arrayref, index, value → | store an int into an array |
 * | LASTORE      | 50  |   | arrayref, index, value → | store a long to an array |
 * | FASTORE      | 51  |   | arrayref, index, value → | store a float in an array |
 * | DASTORE      | 52  |   | arrayref, index, value → | store a double into an array |
 * | AASTORE      | 53  |   | arrayref, index, value → | store a reference in an array |
 * | BASTORE      | 54  |   | arrayref, index, value → | store a byte or Boolean value into an array |
 * | CASTORE      | 55  |   | arrayref, index, value → | store a char into an array |
 * | SASTORE      | 56  |   | arrayref, index, value → | store short to array |
 * | POP          | 57  |   | value → | discard the top value on the stack |
 * | POP2         | 58  |   | {value2, value1} → | discard the top two values on the stack (or one value, if it is a double or long) |
 * | DUP          | 59  |   | value → value, value | duplicate the value on top of the stack |
 * | DUP_X1       | 5a  |   | value2, value1 → value1, value2, value1 | insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long. |
 * | DUP_X2       | 5b  |   | value3, value2, value1 → value1, value3, value2, value1 | insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top |
 * | DUP2         | 5c  |   | {value2, value1} → {value2, value1}, {value2, value1} | duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long) |
 * | DUP2_X1      | 5d  |   | value3, {value2, value1} → {value2, value1}, value3, {value2, value1} | duplicate two words and insert beneath third word (see explanation above) |
 * | DUP2_X2      | 5e  |   | {value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1} | duplicate two words and insert beneath fourth word |
 * | SWAP         | 5f  |   | value2, value1 → value1, value2 | swaps two top words on the stack (note that value1 and value2 must not be double or long) |
 * | IADD         | 60  |   | value1, value2 → result | add two ints |
 * | LADD         | 61  |   | value1, value2 → result | add two longs |
 * | FADD         | 62  |   | value1, value2 → result | add two floats |
 * | DADD         | 63  |   | value1, value2 → result | add two doubles |
 * | ISUB         | 64  |   | value1, value2 → result | int subtract |
 * | LSUB         | 65  |   | value1, value2 → result | subtract two longs |
 * | FSUB         | 66  |   | value1, value2 → result | subtract two floats |
 * | DSUB         | 67  |   | value1, value2 → result | subtract a double from another |
 * | IMUL         | 68  |   | value1, value2 → result | multiply two integers |
 * | LMUL         | 69  |   | value1, value2 → result | multiply two longs |
 * | FMUL         | 6a  |   | value1, value2 → result | multiply two floats |
 * | DMUL         | 6b  |   | value1, value2 → result | multiply two doubles |
 * | IDIV         | 6c  |   | value1, value2 → result | divide two integers |
 * | LDIV         | 6d  |   | value1, value2 → result | divide two longs |
 * | FDIV         | 6e  |   | value1, value2 → result | divide two floats |
 * | DDIV         | 6f  |   | value1, value2 → result | divide two doubles |
 * | IREM         | 70  |   | value1, value2 → result | logical int remainder |
 * | LREM         | 71  |   | value1, value2 → result | remainder of division of two longs |
 * | FREM         | 72  |   | value1, value2 → result | get the remainder from a division between two floats |
 * | DREM         | 73  |   | value1, value2 → result | get the remainder from a division between two doubles |
 * | INEG         | 74  |   | value → result          | negate int |
 * | LNEG         | 75  |   | value → result          | negate a long |
 * | FNEG         | 76  |   | value → result          | negate a float |
 * | DNEG         | 74  |   | value → result          | negate a double |
 * | ISHL         | 78  |   | value1, value2 → result | int shift left |
 * | LSHL         | 79  |   | value1, value2 → result | bitwise shift left of a long value1 by int value2 positions |
 * | ISHR         | 7a  |   | value1, value2 → result | int arithmetic shift right |
 * | LSHR         | 7b  |   | value1, value2 → result | bitwise shift right of a long value1 by int value2 positions |
 * | IUSHR        | 7c  |   | value1, value2 → result | int logical shift right |
 * | LUSHR        | 7d  |   | value1, value2 → result | bitwise shift right of a long value1 by int value2 positions, unsigned |
 * | IAND         | 7e  |   | value1, value2 → result | perform a bitwise AND on two integers |
 * | LAND         | 7f  |   | value1, value2 → result | bitwise AND of two longs |
 * | IOR          | 80  |   | value1, value2 → result | bitwise int OR |
 * | LOR          | 81  |   | value1, value2 → result | bitwise OR of two longs |
 * | IXOR         | 82  |   | value1, value2 → result | int xor |
 * | LXOR         | 83  |   | value1, value2 → result | bitwise XOR of two longs |
 * | I2L          | 85  |   | value → result          | convert an int into a long |
 * | I2F          | 86  |   | value → result          | convert an int into a float |
 * | I2D          | 87  |   | value → result          | convert an int into a double |
 * | L2I          | 88  |   | value → result          | convert an long into a int |
 * | L2F          | 89  |   | value → result          | convert an long into a float |
 * | L2D          | 8a  |   | value → result          | convert an long into a double |
 * | F2I          | 8b  |   | value → result          | convert an float into a int |
 * | F2L          | 8c  |   | value → result          | convert an float into a long |
 * | F2D          | 8d  |   | value → result          | convert an float into a double |
 * | D2I          | 8e  |   | value → result          | convert an double into a int |
 * | D2L          | 8f  |   | value → result          | convert an double into a long |
 * | D2F          | 90  |   | value → result          | convert an double into a float |
 * | I2B          | 91  |   | value → result          | convert an int into a byte |
 * | I2C          | 92  |   | value → result          | convert an int into a character |
 * | I2S          | 93  |   | value → result          | convert an int into a short |
 * | LCMP         | 94  |   | value1, value2 → result | push 0 if the two longs are the same, 1 if value1 is greater than value2, -1 otherwise |
 * | FCMPL        | 95  |   | value1, value2 → result | compare two floats, -1 on NaN |
 * | FCMPG        | 96  |   | value1, value2 → result | compare two floats, 1 on NaN |
 * | DCMPL        | 97  |   | value1, value2 → result | compare two doubles, -1 on NaN |
 * | DCMPG        | 98  |   | value1, value2 → result | compare two doubles, 1 on NaN |
 * | IRETURN      | ac  |   | value → [empty]         | return an integer from a method |
 * | LRETURN      | ad  |   | value → [empty]         | return a long value |
 * | FRETURN      | ae  |   | value → [empty]         | return a float |
 * | DRETURN      | af  |   | value → [empty]         | return a double from a method |
 * | ARETURN      | b0  |   | objectref → [empty]     | return a reference from a method |
 * | RETURN       | b1  |   | → [empty]               | return void from method |
 * | ARRAYLENGTH  | be  |   | arrayref → length       | get the length of an array |
 * | ATHROW       | bf  |   | objectref → [empty], objectref | throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable) |
 * | MONITORENTER | c2  |   | objectref →             | enter monitor for object ("grab the lock" – start of synchronized() section) |
 * | MONITOREXIT  | c3  |   | objectref →             | exit monitor for object ("release the lock" – end of synchronized() section) |
 * @param op
 */
case class MInst(op:OpCode) extends MethCode

//case class MInsnAnnotation(typeRef:Int,typePath:Option[String],desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode

/** 
 * @param parameterCount
 * @param visible
 */
case class MAnnotableParameterCount(parameterCount:Int,visible:Boolean) extends MethCode

/**
 * Visits an annotation of this method.
 *
 * a visitor to visit the annotation values, or {@literal null} if this visitor is not
 * interested in visiting this annotation.
 * 
 * @param desc the class descriptor of the annotation class.
 * @param visible true - if the annotation is visible at runtime.
 * @param annotations
 */
case class MAnnotation(desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode

/**
 * Visits the default value of this annotation interface method.
 *
 * <p>a visitor to the visit the actual default value of this annotation interface method, or
 * {@literal null} if this visitor is not interested in visiting this default value. The
 * 'name' parameters passed to the methods of this annotation visitor are ignored. Moreover,
 * exacly one visit method must be called on this annotation visitor, followed by visitEnd.
 * @param annotations
 */
case class MAnnotationDefault(annotations:Seq[AnnCode]) extends MethCode

/**
 * Доступ к полю объекта
 * <p>
 *
 * the opcode of the type instruction to be visited. This opcode is either
 * GETSTATIC,
 * PUTSTATIC,
 * GETFIELD or
 * PUTFIELD.
 *
 * <hr>
 *     getstatic
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Get static field from class
 *<h2 style="font-weight: bold"> Format</h2>
 *
 * <pre>
 * getstatic
 * indexbyte1
 * indexbyte2
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * getstatic = 178 (0xb2)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class or interface in which the field is to be found. The referenced field is resolved (§5.4.3.2).
 *
 * On successful resolution of the field, the class or interface that declared the resolved field is initialized (§5.5) if that class or interface has not already been initialized.
 *
 * The value of the class or interface field is fetched and pushed onto the operand stack.
 * <h2 style="font-weight: bold">Linking Exceptions</h2>
 *
 * During resolution of the symbolic reference to the class or interface field, any of the exceptions pertaining to field resolution (§5.4.3.2) can be thrown.
 *
 * Otherwise, if the resolved field is not a static (class) field or an interface field, getstatic throws an IncompatibleClassChangeError.
 * <h2 style="font-weight: bold">Run-time Exception</h2>
 *
 * Otherwise, if execution of this getstatic instruction causes initialization of the referenced class or interface, getstatic may throw an Error as detailed in §5.5.
 *
 * <hr>
 *
 * putstatic
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Set static field in class
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * putstatic
 * indexbyte1
 * indexbyte2
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * putstatic = 179 (0xb3)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class or interface in which the field is to be found. The referenced field is resolved (§5.4.3.2).
 *
 * <p>
 * On successful resolution of the field, the class or interface that declared the resolved field is initialized (§5.5) if that class or interface has not already been initialized.
 *
 * <p>
 * The type of a value stored by a putstatic instruction must be compatible with the descriptor of the referenced field (§4.3.2). If the field descriptor type is boolean, byte, char, short, or int, then the value must be an int. If the field descriptor type is float, long, or double, then the value must be a float, long, or double, respectively. If the field descriptor type is a reference type, then the value must be of a type that is assignment compatible (JLS §5.2) with the field descriptor type. If the field is final, it must be declared in the current class, and the instruction must occur in the &lt;clinit&gt; method of the current class (§2.9).
 *
 * <p>
 * The value is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. The class field is set to value'.
 *
 * <h2 style="font-weight: bold">Linking Exceptions</h2>
 *
 * During resolution of the symbolic reference to the class or interface field, any of the exceptions pertaining to field resolution (§5.4.3.2) can be thrown.
 *
 * <p>
 * Otherwise, if the resolved field is not a static (class) field or an interface field, putstatic throws an IncompatibleClassChangeError.
 *
 * <p>
 * Otherwise, if the field is final, it must be declared in the current class, and the instruction must occur in the &lt;clinit&gt; method of the current class. Otherwise, an IllegalAccessError is thrown.
 *
 * <h2 style="font-weight: bold">Run-time Exception</h2>
 *
 * Otherwise, if execution of this putstatic instruction causes initialization of the referenced class or interface, putstatic may throw an Error as detailed in §5.5.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * A putstatic instruction may be used only to set the value of an interface field on the initialization of that field. Interface fields may be assigned to only once, on execution of an interface variable initialization expression when the interface is initialized (§5.5, JLS §9.3.1).
 *
 * <hr>
 *
 getfield
 <h2 style="font-weight: bold">Operation</h2>

 Fetch field from object
 <h2 style="font-weight: bold">Format</h2>

 <pre>
 getfield
 indexbyte1
 indexbyte2
 </pre>

 <h2 style="font-weight: bold">Forms</h2>

 getfield = 180 (0xb4)
 <h2 style="font-weight: bold">Operand Stack</h2>

 <pre>
 ..., objectref →
 ..., value
 </pre>

 <h2 style="font-weight: bold">Description</h2>

 The objectref, which must be of type reference, is popped from the operand stack. The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found. The referenced field is resolved (§5.4.3.2). The value of the referenced field in objectref is fetched and pushed onto the operand stack.

 The type of objectref must not be an array type. If the field is protected, and it is a member of a superclass of the current class, and the field is not declared in the same run-time package (§5.3) as the current class, then the class of objectref must be either the current class or a subclass of the current class.
 <h2 style="font-weight: bold">Linking Exceptions</h2>

 During resolution of the symbolic reference to the field, any of the errors pertaining to field resolution (§5.4.3.2) can be thrown.

 Otherwise, if the resolved field is a static field, getfield throws an IncompatibleClassChangeError.
 <h2 style="font-weight: bold">Run-time Exception</h2>

 Otherwise, if objectref is null, the getfield instruction throws a NullPointerException.
 <h2 style="font-weight: bold">Notes</h2>

 The getfield instruction cannot be used to access the length field of an array. The arraylength instruction (§arraylength) is used instead.


 <hr>

 putfield
 <h2 style="font-weight: bold">Operation</h2>

 Set field in object
 <h2 style="font-weight: bold">Format</h2>

 <pre>
 putfield
 indexbyte1
 indexbyte2
 </pre>

 <h2 style="font-weight: bold">Forms</h2>

 putfield = 181 (0xb5)

 <h2 style="font-weight: bold">Operand Stack</h2>

 <pre>
 ..., objectref, value →
 ...
 </pre>

 <h2 style="font-weight: bold">Description</h2>

 The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found. The class of objectref must not be an array. If the field is protected, and it is a member of a superclass of the current class, and the field is not declared in the same run-time package (§5.3) as the current class, then the class of objectref must be either the current class or a subclass of the current class.

 <p>
 The referenced field is resolved (§5.4.3.2). The type of a value stored by a putfield instruction must be compatible with the descriptor of the referenced field (§4.3.2). If the field descriptor type is boolean, byte, char, short, or int, then the value must be an int. If the field descriptor type is float, long, or double, then the value must be a float, long, or double, respectively. If the field descriptor type is a reference type, then the value must be of a type that is assignment compatible (JLS §5.2) with the field descriptor type. If the field is final, it must be declared in the current class, and the instruction must occur in an instance initialization method (&lt;init&gt;) of the current class (§2.9).

 <p>
 The value and objectref are popped from the operand stack. The objectref must be of type reference. The value undergoes value set conversion (§2.8.3), resulting in value', and the referenced field in objectref is set to value'.
 <h2 style="font-weight: bold">Linking Exceptions</h2>

 During resolution of the symbolic reference to the field, any of the exceptions pertaining to field resolution (§5.4.3.2) can be thrown.

 <p>
 Otherwise, if the resolved field is a static field, putfield throws an IncompatibleClassChangeError.

 <p>
 Otherwise, if the field is final, it must be declared in the current class, and the instruction must occur in an instance initialization method (&lt;init&gt;) of the current class. Otherwise, an IllegalAccessError is thrown.
 <h2 style="font-weight: bold">Run-time Exception</h2>

 Otherwise, if objectref is null, the putfield instruction throws a NullPointerException.

 * @param op
 * @param owner
 * @param name
 * @param desc
 */
case class MFieldInsn(op:OpCode,owner:String,name:String,desc:TDesc) extends MethCode

/**
 * <a href="https://coderoad.ru/25109942/%D0%A7%D1%82%D0%BE-%D1%82%D0%B0%D0%BA%D0%BE%D0%B5-%D1%84%D1%80%D0%B5%D0%B9%D0%BC-%D0%BA%D0%B0%D1%80%D1%82%D1%8B-%D1%81%D1%82%D0%B5%D0%BA%D0%B0">Java требует проверки всех загруженных классов, чтобы обеспечить безопасность песочницы и обеспечить безопасность кода для оптимизации. Обратите внимание, что это делается на уровне байт-кода , поэтому проверка не проверяет инварианты языка Java, она просто проверяет, что байт-код имеет смысл в соответствии с правилами байт-кода.</a>
 * <p> Помимо прочего, проверка байт-кода гарантирует, что инструкции хорошо сформированы, что все переходы являются допустимыми инструкциями в методе и что все инструкции работают со значениями правильного типа. В последнем случае используется карта стека.
 * <p> Дело в том, что байт-код сам по себе не содержит явной информации о типе. Типы определяются неявно с помощью анализа потоков данных. Например, инструкция iconst создает целочисленное значение. Если вы храните его в слоте 1, этот слот теперь имеет int. Если поток управления сливается из кода, в котором вместо этого хранится float, слот теперь считается недопустимым типом, что означает, что вы больше ничего не можете сделать с этим значением, пока не перезапишете его.
 * <p> Исторически верификатор байт-кода выводил все типы, используя эти правила потока данных. К сожалению, невозможно вывести все типы за один линейный проход через байт-код, поскольку обратный переход может привести к недействительности уже выведенных типов. Классический верификатор решил эту проблему, повторяя код до тех пор, пока все не перестанет меняться, что потенциально потребует нескольких проходов.
 * <p> Однако проверка замедляет загрузку класса в Java. Oracle решил решить эту проблему, добавив новый, более быстрый верификатор, который может проверять байт-код за один проход. Для этого им потребовалось, чтобы все новые классы, начиная с Java 7 (с Java 6 в переходном состоянии), несли метаданные об их типах, чтобы байт-код можно было проверить за один проход. Поскольку сам формат байт-кода не может быть изменен, эта информация о типе хранится отдельно в атрибуте с именем StackMapTable .
 * <p> Простое хранение типа для каждого отдельного значения в каждой отдельной точке кода, очевидно, заняло бы много места и было бы очень расточительным. Чтобы сделать метаданные меньше и эффективнее, они решили, что в них будут перечислены только типы в позициях, которые являются целями прыжков . Если вы подумаете об этом, это единственный раз, когда вам нужна дополнительная информация для проверки одного прохода. В промежутках между целями прыжка весь поток управления является линейным, поэтому вы можете выводить типы в промежуточных позициях, используя старые правила вывода.
 * <p> Каждая позиция, в которой явно перечислены типы, называется фреймом карты стека. Атрибут StackMapTable содержит список кадров по порядку, хотя они обычно выражаются как отличие от предыдущего кадра, чтобы уменьшить размер данных. Если в методе нет фреймов, что происходит, когда поток управления никогда не соединяется (т. Е. CFG-это дерево), то атрибут StackMapTable может быть полностью опущен.
 * <p> Итак, это основная идея о том, как работает StackMapTable и почему он был добавлен. Последний вопрос заключается в том, как создается неявный начальный фрейм. Ответ, конечно, заключается в том, что в начале метода стек операндов пуст, а слоты локальных переменных имеют типы, заданные типами параметров метода, которые определяются из декриптора метода.
 * <p> Если вы привыкли к Java, есть несколько незначительных различий в том, как типы параметров метода работают на уровне байт-кода. Во-первых, виртуальные методы имеют неявный this в качестве первого параметра. Во - вторых, boolean , byte , char и short не существуют на уровне байт-кода. Вместо этого все они реализуются как внутренние за кулисами.
 * 
 * <hr>
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.6">Фрейм используется для хранения данных и частичных результатов, а также для выполнения динамического связывания, возврата значений для методов и отправки исключений</a>.
 * <p> Новый фрейм создается каждый раз при вызове метода. Кадр уничтожается, когда завершается вызов его метода, независимо от того, является ли это завершение нормальным или внезапным (он вызывает неперехваченное исключение). Фреймы выделяются из стека виртуальной машины Java (§2.5.2) потока, создающего фрейм. Каждый фрейм имеет свой собственный массив локальных переменных (§2.6.1), свой собственный стек операндов (§2.6.2) и ссылку на пул констант времени выполнения (§2.5.5) класса текущего метода. .
 * <p> Кадр может быть расширен дополнительной информацией, зависящей от реализации, например, отладочной информацией.
 * <p> Размеры массива локальных переменных и стека операндов определяются во время компиляции и предоставляются вместе с кодом для метода, связанного с кадром (§4.7.3). Таким образом, размер структуры данных кадра зависит только от реализации виртуальной машины Java, и память для этих структур может быть выделена одновременно с вызовом метода.
 * <p> Только один фрейм, фрейм для выполняемого метода, активен в любой точке данного потока управления. Этот кадр называется текущим кадром, а его метод известен как текущий метод. Класс, в котором определен текущий метод, является текущим классом. Операции с локальными переменными и стеком операндов обычно относятся к текущему кадру.
 * <p> Фрейм перестает быть текущим, если его метод вызывает другой метод или его метод завершается. Когда вызывается метод, создается новый фрейм, который становится текущим, когда управление передается новому методу. При возврате метода текущий фрейм передает результат своего вызова метода, если таковой имеется, в предыдущий фрейм. Затем текущий кадр отбрасывается, так как предыдущий кадр становится текущим.
 * <p> Обратите внимание, что кадр, созданный потоком, является локальным для этого потока и не может ссылаться на какой-либо другой поток.
 * 
 * <p>
 * Этот инструкция вставляется непосредственно перед любой инструкцией i, 
 * которая следует за инструкцией безусловного перехода, 
 * такой как GOTO или THROW, 
 * 
 * <hr>
 * 
 * которая является целью инструкции перехода или запускает блок обработчика исключений. 
 * Посещаемые типы должны описывать значения локальных переменных и элементов 
 * стека операндов непосредственно перед выполнением i. (*) 
 * 
 * это обязательно только для классов, версия которых больше или равна Opcodes.V1_6. 
 * Кадры метода должны быть указаны либо в развернутой форме, либо в сжатом виде 
 * (все кадры должны использовать один и тот же формат, 
 * т.е. вы не должны смешивать развернутые и сжатые кадры в одном методе)
 * 
 * <p>
 * Visits the current state of the local variables and operand stack elements. This method must(*)
 * be called <i>just before</i> any instruction <b>i</b> that follows an unconditional branch
 * instruction such as GOTO or THROW, that is the target of a jump instruction, or that starts an
 * exception handler block. The visited types must describe the values of the local variables and
 * of the operand stack elements <i>just before</i> <b>i</b> is executed.<br>
 * <br>
 * (*) this is mandatory only for classes whose version is greater than or equal to {@link
 * Opcodes#V1_6}. <br>
 * <br>
 * The frames of a method must be given either in expanded form, or in compressed form (all frames
 * must use the same format, i.e. you must not mix expanded and compressed frames within a single
 * method):
 *
 * <ul>
 *   <li>In expanded form, all frames must have the F_NEW type.
 *   <li>In compressed form, frames are basically "deltas" from the state of the previous frame:
 *       <ul>
 *         <li>{@link Opcodes#F_SAME} representing frame with exactly the same locals as the
 *             previous frame and with the empty stack.
 *         <li>{@link Opcodes#F_SAME1} representing frame with exactly the same locals as the
 *             previous frame and with single value on the stack ( <code>numStack</code> is 1 and
 *             <code>stack[0]</code> contains value for the type of the stack item).
 *         <li>{@link Opcodes#F_APPEND} representing frame with current locals are the same as the
 *             locals in the previous frame, except that additional locals are defined (<code>
 *             numLocal</code> is 1, 2 or 3 and <code>local</code> elements contains values
 *             representing added types).
 *         <li>{@link Opcodes#F_CHOP} representing frame with current locals are the same as the
 *             locals in the previous frame, except that the last 1-3 locals are absent and with
 *             the empty stack (<code>numLocal</code> is 1, 2 or 3).
 *         <li>{@link Opcodes#F_FULL} representing complete frame data.
 *       </ul>
 * </ul>
 *
 * <br>
 * In both cases the first frame, corresponding to the method's parameters and access flags, is
 * implicit and must not be visited. Also, it is illegal to visit two or more frames for the same
 * code location (i.e., at least one instruction must be visited between two calls to visitFrame).
 *
 * <ul>
 *     <li>
 *         {@link #type}
 *
 *         the type of this stack map frame. Must be {@link Opcodes#F_NEW} for expanded
 *         frames, or {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link
 *         Opcodes#F_SAME} or {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
 *     </li>
 *     <li>
 *         {@link #numLocal}
 *         the number of local variables in the visited frame.
 *     </li>
 *     <li>
 *         {@link #local}
 *         the local variable types in this frame. This array must not be modified. Primitive
 *         types are represented by {@link Opcodes#TOP}, {@link Opcodes#INTEGER}, {@link
 *         Opcodes#FLOAT}, {@link Opcodes#LONG}, {@link Opcodes#DOUBLE}, {@link Opcodes#NULL} or
 *         {@link Opcodes#UNINITIALIZED_THIS} (long and double are represented by a single element).
 *         Reference types are represented by String objects (representing internal names), and
 *         uninitialized types by Label objects (this label designates the NEW instruction that
 *         created this uninitialized value).
 *     </li>
 *     <li>
 *         {@link #numStack}
 *         the number of operand stack elements in the visited frame.
 *     </li>
 *     <li>
 *         {@link #stack}
 *         the operand stack types in this frame. This array must not be modified. Its
 *         content has the same format as the "local" array.
 *     </li>
 * </ul>
 *
 * <b>throws IllegalStateException</b>
 * if a frame is visited just after another one, without any
 * instruction between the two (unless this frame is a Opcodes#F_SAME frame, in which case it
 * is silently ignored).
 * @param kind
 * @param numLocal
 * @param local
 * @param numStack
 * @param stack
 */
case class MFrame(kind:MFrameType,numLocal:Int,local:Seq[AnyRef],numStack:Int,stack:Seq[AnyRef]) extends MethCode
case class MFrameType(kind:Int)

/**
- **F_NEW**
  An expanded frame. See {@link ClassReader#EXPAND_FRAMES}

- **F_FULL**
  A compressed frame with complete frame data.

- **F_APPEND**
  A compressed frame where locals are the same as the locals in the previous frame, except that
  additional 1-3 locals are defined, and with an empty stack.

- **F_CHOP**
  A compressed frame where locals are the same as the locals in the previous frame, except that
  the last 1-3 locals are absent and with an empty stack.

- **F_SAME**
  A compressed frame with exactly the same locals as the previous frame and with an empty stack.

- **F_SAME1**
  A compressed frame with exactly the same locals as the previous frame and with a single value
  on the stack.
*/
enum MFrameKind(kind:Int):
  case F_NEW    extends MFrameKind(-1)
  case F_FULL   extends MFrameKind(0)
  case F_APPEND extends MFrameKind(1)
  case F_CHOP   extends MFrameKind(2)
  case F_SAME   extends MFrameKind(3)
  case F_SAME1  extends MFrameKind(4)

case class MFrameElem(kind:Int)
enum MFrameElemKind(kind:Int):
  case ITEM_TOP extends MFrameElemKind(0)
  case ITEM_INTEGER extends MFrameElemKind(1)
  case ITEM_FLOAT extends MFrameElemKind(2)
  case ITEM_DOUBLE extends MFrameElemKind(3)
  case ITEM_LONG extends MFrameElemKind(4)
  case ITEM_NULL extends MFrameElemKind(5)
  case ITEM_UNINITIALIZED_THIS extends MFrameElemKind(6)
  case ITEM_OBJECT extends MFrameElemKind(7)
  case ITEM_UNINITIALIZED extends MFrameElemKind(8)

/**
 * Increment local variable by constant (<a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html">jvm spec</a>).
 *
 * <h2 style="font-weight: bold">Operation</h2>
 * Increment local variable by constant
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * iinc
 * index
 * const
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * iinc = 132 (0x84)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 * No change
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The const is an immediate signed byte.
 * The local variable at index must contain an int.
 * The value const is first sign-extended to an int, and then the local variable at index is incremented by that amount.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The iinc opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index and to increment it by a two-byte immediate signed value.
 * @param variable
 * @param inc
 */
case class MIincInsn(variable:Int,inc:Int) extends MethCode

/** 
 * @param typeRef
 * @param typePath
 * @param desc
 * @param visible
 * @param annotations
 */
case class MInsnAnnotation(
  typeRef:MTypeInsnRef,
  typePath:Option[String],
  desc:TDesc,
  visible:Boolean,
  annotations:Seq[AnnCode]
  ) extends MethCode

/**
 * Операции {@link OpCode#BIPUSH}, {@link OpCode#SIPUSH}, {@link OpCode#NEWARRAY}
 * 
 * <h2>BIPUSH</h2>
 * Непосредственный байт расширяется знаком до значения типа int. Это значение помещается в стек операндов.
 * 
 * <h2>SIPUSH</h2>
 * Непосредственные значения без знака byte1 и byte2 собираются в промежуточное короткое замыкание, 
 * где значение короткого замыкания равно (byte1 &lt;&lt; 8) | байт2. 
 * Затем промежуточное значение расширяется знаком до значения типа int. 
 * Это значение помещается в стек операндов.
 * 
 * <h2>SIPUSH</h2>
 * A new array whose components are of type atype and of length count is allocated from 
 * the garbage-collected heap. 
 * A reference arrayref to this new array object is pushed into the operand stack. 
 * Each of the elements of the new array is initialized to the default initial value (§2.3, §2.4) 
 * for the element type of the array type.
 * 
 * <p>
 * Новый массив, компоненты которого имеют тип atype и счетчик длины, 
 * выделяется из кучи со сборкой мусора. Ссылка arrayref на этот новый объект 
 * массива помещается в стек операндов. Каждый из элементов нового массива 
 * инициализируется начальным значением по умолчанию (§2.3, §2.4) для типа элемента типа массива.
 * 
 * <pre>
 * ..., count → ..., arrayref
 * </pre>
 * 
 * Run-time Exception
 * <p>If count is less than zero, newarray throws a NegativeArraySizeException.
 * 
 * <p>Notes
 * <p>
 * In Oracle's Java Virtual Machine implementation, arrays of type boolean (atype is T_BOOLEAN) 
 * are stored as arrays of 8-bit values and are manipulated using the baload and bastore instructions 
 * (§baload, §bastore) which also access arrays of type byte. 
 * Other implementations may implement packed boolean arrays; the baload and bastore 
 * instructions must still be used to access those arrays.
 * 
 * <p>
 * В реализации виртуальной машины Java Oracle массивы типа boolean (тип - T_BOOLEAN) 
 * хранятся как массивы 8-битных значений и управляются с помощью инструкций baload и bastore 
 * (§baload, §bastore), которые также обращаются к массивам типа byte. Другие реализации могут 
 * реализовывать упакованные логические массивы; инструкции baload и bastore по-прежнему 
 * должны использоваться для доступа к этим массивам.
 * 
 * <p>
 * Visits an instruction with a single int operand.
 *
 * <br>{@link #opcode}  the opcode of the instruction to be visited. This opcode is either BIPUSH, SIPUSH
 *                or NEWARRAY.
 * <br>{@link #operand} the operand of the instruction to be visited.<br>
 *                When opcode is BIPUSH, operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.
 *                <br>
 *                When opcode is SIPUSH, operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.
 *                <br>
 *                When opcode is NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link
 *                Opcodes#T_CHAR}, {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE},
 *                {@link Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
 * @param op
 * @param operand
 */
case class MIntInsn(op:OpCode,operand:Int) extends MethCode

/**
 * <a href="https://habr.com/ru/post/328240/">Хорошая статья о Что там с JEP-303 или изобретаем invokedynamic</a>
 * 
 * <hr>
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.invokedynamic">Инструкция invokedynamic</a>
 * 
 * <p>
 * Operation
 * 
 * <p>
 * Invoke dynamic method
 * 
 * <p>
 * Format
<pre>
invokedynamic
indexbyte1
indexbyte2
0
0
</pre>
* 
* Forms
* <p> invokedynamic = 186 (0xba)
* 
* <p> Operand Stack
<pre>..., [arg1, [arg2 ...]] → ...</pre>
* 
* <p> Description
* <p> Each specific lexical occurrence of an invokedynamic instruction is called a dynamic call site.
* <p> Каждое конкретное лексическое вхождение вызванной динамической инструкции называется динамическим сайтом вызова.
* 
* <p> First, the unsigned indexbyte1 and indexbyte2 are used to construct an index into 
* the run-time constant pool of the current class (§2.6), where the value of the index is 
* (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at that 
* index must be a symbolic reference to a call site specifier (§5.1). 
* The values of the third and fourth operand bytes must always be zero.
* <p> Во-первых, беззнаковые indexbyte1 и indexbyte2 используются для создания 
* индекса в пуле постоянных времени выполнения текущего класса (§2.6), где значение индекса 
* (indexbyte1 &lt;&lt; 8) | indexbyte2. Элемент пула констант времени 
* выполнения в этом индексе должен быть символьной ссылкой на спецификатор сайта вызова (§5.1). 
* Значения третьего и четвертого байтов операнда всегда должны быть нулевыми.
* 
* <p> The call site specifier is resolved (§5.4.3.6) for this specific dynamic 
* call site to obtain a reference to a java.lang.invoke.MethodHandle instance, 
* a reference to a java.lang.invoke.MethodType instance, and references to static arguments.
* <p> Спецификатор сайта вызова разрешен (§5.4.3.6) для этого конкретного 
* сайта динамического вызова, чтобы получить ссылку на экземпляр 
* java.lang.invoke.MethodHandle, ссылку на экземпляр java.lang.invoke.MethodType 
* и ссылки на статический аргументы.
* 
* <p> Next, as part of the continuing resolution of the call site specifier, 
* the bootstrap method is invoked as if by execution of an invokevirtual instruction 
* (§invokevirtual) that contains a run-time constant pool index to a symbolic reference 
* to a method (§5.1) with the following properties:
* <p>
* Затем, как часть продолжающегося разрешения спецификатора сайта вызова, 
* метод начальной загрузки вызывается, как если бы путем выполнения инструкции 
* invokevirtual (§invokevirtual), которая содержит индекс пула констант времени 
* выполнения для символической ссылки на метод (§5.1) ) со следующими свойствами:
* 
* <ul>
* <li> The method's name is invoke;
*      <br> Вызывается имя метода;
* 
* <li> The method's descriptor has a return type of java.lang.invoke.CallSite;
*       <br> Дескриптор метода имеет возвращаемый тип java.lang.invoke.CallSite;
* 
* <li> The method's descriptor has parameter types derived from the items pushed on to the operand stack, as follows.
*       <br> Дескриптор метода имеет типы параметров, производные от элементов, помещенных в стек операндов, как показано ниже.
* 
* <li> The first four parameter types in the descriptor are java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandles.Lookup, String, and java.lang.invoke.MethodType, in that order.
*       <br> Первые четыре типа параметров в дескрипторе - это java.lang.invoke.MethodHandle, java.lang.invoke.MethodHandles.Lookup, String и java.lang.invoke.MethodType в указанном порядке.
* 
* <li> If the call site specifier has any static arguments, then a parameter type for each argument is appended to the parameter types of the method descriptor in the order that the arguments were pushed on to the operand stack. These parameter types may be Class, java.lang.invoke.MethodHandle, java.lang.invoke.MethodType, String, int, long, float, or double.
*       <br> Если спецификатор сайта вызова имеет какие-либо статические аргументы, то тип параметра для каждого аргумента добавляется к типам параметров дескриптора метода в том порядке, в котором аргументы были помещены в стек операндов. Эти типы параметров могут быть Class, java.lang.invoke.MethodHandle, java.lang.invoke.MethodType, String, int, long, float или double.
* 
* <li> The method's symbolic reference to the class in which the method is to be found indicates the class java.lang.invoke.MethodHandle.
*       <br> Символьная ссылка метода на класс, в котором должен быть найден метод, указывает на класс java.lang.invoke.MethodHandle.
* </ul>
* 
* <p> where it is as if the following items were pushed, in order, onto the operand stack:
* <br> где это как если бы следующие элементы были помещены в стек операндов по порядку:
* 
* <ul>
* <li> the reference to the java.lang.invoke.MethodHandle object for the bootstrap method;
*   <br> ссылка на объект java.lang.invoke.MethodHandle для метода начальной загрузки;
* 
* <li> a reference to a java.lang.invoke.MethodHandles.Lookup object for the class in which this dynamic call site occurs;
* <br> ссылка на объект java.lang.invoke.MethodHandles.Lookup для класса, в котором происходит этот сайт динамического вызова;
* 
* <li> a reference to the String for the method name in the call site specifier;
* <br> ссылка на String для имени метода в спецификаторе сайта вызова;
* 
* <li> the reference to the java.lang.invoke.MethodType object obtained for the method descriptor in the call site specifier;
* <br> ссылка на объект java.lang.invoke.MethodType, полученная для дескриптора метода в спецификаторе сайта вызова;
* 
* <li> references to classes, method types, method handles, and string literals denoted as static arguments in the call site specifier, and numeric values (§2.3.1, §2.3.2) denoted as static arguments in the call site specifier, in the order in which they appear in the call site specifier. (That is, no boxing occurs for primitive values.)
* <br> ссылки на классы, типы методов, дескрипторы методов и строковые литералы, обозначенные как статические аргументы в спецификаторе сайта вызова, и числовые значения (§2.3.1, §2.3.2), обозначенные как статические аргументы в спецификаторе места вызова, в порядке в котором они появляются в указателе места вызова. (То есть для примитивных значений упаковка не выполняется.)
* </ul>
* 
* <p> As long as the bootstrap method can be correctly invoked by the invoke method, its descriptor is arbitrary. For example, the first parameter type could be Object instead of java.lang.invoke.MethodHandles.Lookup, and the return type could also be Object instead of java.lang.invoke.CallSite.
* <br> Пока метод начальной загрузки может быть правильно вызван методом invoke, его дескриптор является произвольным. Например, первым типом параметра может быть Object вместо java.lang.invoke.MethodHandles.Lookup, а типом возвращаемого значения также может быть Object вместо java.lang.invoke.CallSite.
* 
* <p> If the bootstrap method is a variable arity method, then some or all of the arguments on the operand stack specified above may be collected into a trailing array parameter.
* <br> Если метод начальной загрузки является методом переменной арности, то некоторые или все аргументы в стеке операндов, указанные выше, могут быть собраны в завершающий параметр массива.
* 
* <p> The invocation of a bootstrap method occurs within a thread that is attempting resolution of the symbolic reference to the call site specifier of this dynamic call site. If there are several such threads, the bootstrap method may be invoked in several threads concurrently. Therefore, bootstrap methods which access global application data must take the usual precautions against race conditions.
* <br> Вызов метода начальной загрузки происходит в потоке, который пытается разрешить символическую ссылку на спецификатор сайта вызова этого сайта динамического вызова. Если таких потоков несколько, метод начальной загрузки может быть вызван в нескольких потоках одновременно. Следовательно, методы начальной загрузки, которые обращаются к глобальным данным приложения, должны принимать обычные меры предосторожности против условий гонки.
* 
* <p> The result returned by the bootstrap method must be a reference to an object whose class is java.lang.invoke.CallSite or a subclass of java.lang.invoke.CallSite. This object is known as the call site object. The reference is popped from the operand stack used as if in the execution of an invokevirtual instruction.
* <br> Результат, возвращаемый методом начальной загрузки, должен быть ссылкой на объект, класс которого java.lang.invoke.CallSite или подкласс java.lang.invoke.CallSite. Этот объект известен как объект сайта вызова. Ссылка извлекается из стека операндов, используемого, как если бы при выполнении команды invokevirtual.
* 
* <p> If several threads simultaneously execute the bootstrap method for the same dynamic call site, the Java Virtual Machine must choose one returned call site object and install it visibly to all threads. Any other bootstrap methods executing for the dynamic call site are allowed to complete, but their results are ignored, and the threads' execution of the dynamic call site proceeds with the chosen call site object.
* <br> Если несколько потоков одновременно выполняют метод начальной загрузки для одного и того же сайта динамического вызова, виртуальная машина Java должна выбрать один возвращенный объект сайта вызова и установить его видимым образом для всех потоков. Любые другие методы начальной загрузки, выполняемые для сайта динамического вызова, могут завершиться, но их результаты игнорируются, и выполнение потоков на сайте динамического вызова продолжается с выбранным объектом сайта вызова.
* 
* <p> The call site object has a type descriptor (an instance of java.lang.invoke.MethodType) which must be semantically equal to the java.lang.invoke.MethodType object obtained for the method descriptor in the call site specifier.
* <br> Объект сайта вызова имеет дескриптор типа (экземпляр java.lang.invoke.MethodType), который должен быть семантически равен объекту java.lang.invoke.MethodType, полученному для дескриптора метода в спецификаторе сайта вызова.
* 
* <p> The result of successful call site specifier resolution is a call site object which is permanently bound to the dynamic call site.
* <br> Результатом успешного разрешения спецификатора сайта вызова является объект сайта вызова, который постоянно привязан к динамическому сайту вызова.
* 
* <p> The method handle represented by the target of the bound call site object is invoked. The invocation occurs as if by execution of an invokevirtual instruction (§invokevirtual) that indicates a run-time constant pool index to a symbolic reference to a method (§5.1) with the following properties:
* <br> Вызывается дескриптор метода, представленный целью привязанного объекта сайта вызова. Вызов происходит, как если бы при выполнении инструкции invokevirtual (§invokevirtual), которая указывает индекс пула констант времени выполнения на символическую ссылку на метод (§5.1) со следующими свойствами:
* 
* <ul>
* <li> The method's name is invokeExact;
* <br> Имя метода - invokeExact;
* 
* <li> The method's descriptor is the method descriptor in the call site specifier; and
* <br> Дескриптор метода - это дескриптор метода в спецификаторе сайта вызова; а также
* 
* <li> The method's symbolic reference to the class in which the method is to be found indicates the class java.lang.invoke.MethodHandle.
* <br> Символьная ссылка метода на класс, в котором должен быть найден метод, указывает на класс java.lang.invoke.MethodHandle.
* </ul>
* 
* <p> The operand stack will be interpreted as containing a reference to the target of the call site object, followed by nargs argument values, where the number, type, and order of the values must be consistent with the method descriptor in the call site specifier.
* <br> Стек операндов будет интерпретироваться как содержащий ссылку на цель объекта сайта вызова, за которой следуют значения аргумента nargs, где число, тип и порядок значений должны соответствовать дескриптору метода в спецификаторе сайта вызова.
* 
* <p><b>Linking Exceptions</b>
* <p> If resolution of the symbolic reference to the call site specifier throws an exception E, the invokedynamic instruction throws a BootstrapMethodError that wraps E.
* <br> Если разрешение символьной ссылки на спецификатор сайта вызова вызывает исключение E, инструкция invokedynamic генерирует ошибку BootstrapMethodError, которая обертывает E.
* 
* <p> Otherwise, during the continuing resolution of the call site specifier, if invocation of the bootstrap method completes abruptly (§2.6.5) because of a throw of exception E, the invokedynamic instruction throws a BootstrapMethodError that wraps E. (This can occur if the bootstrap method has the wrong arity, parameter type, or return type, causing java.lang.invoke.MethodHandle . invoke to throw java.lang.invoke.WrongMethodTypeException.)
* <br> В противном случае, во время продолжающегося разрешения спецификатора сайта вызова, если вызов метода начальной загрузки завершается внезапно (§2.6.5) из-за выброса исключения E, инструкция invokedynamic выдает ошибку BootstrapMethodError, которая обертывает E. (Это может произойти, если Метод начальной загрузки имеет неправильную арность, тип параметра или возвращаемый тип, в результате чего java.lang.invoke.MethodHandle. invoke вызывает исключение java.lang.invoke.WrongMethodTypeException.)
* 
* <p> Otherwise, during the continuing resolution of the call site specifier, if the result from the bootstrap method invocation is not a reference to an instance of java.lang.invoke.CallSite, the invokedynamic instruction throws a BootstrapMethodError.
* <br> В противном случае, во время продолжающегося разрешения спецификатора сайта вызова, если результат вызова метода начальной загрузки не является ссылкой на экземпляр java.lang.invoke.CallSite, инструкция invokedynamic выдает ошибку BootstrapMethodError.
* 
* <p> Otherwise, during the continuing resolution of the call site specifier, if the type descriptor of the target of the call site object is not semantically equal to the method descriptor in the call site specifier, the invokedynamic instruction throws a BootstrapMethodError.
* <br> В противном случае, во время продолжающегося разрешения спецификатора сайта вызова, если дескриптор типа цели объекта сайта вызова семантически не равен дескриптору метода в описателе сайта вызова, инструкция invokedynamic выдает ошибку BootstrapMethodError.
* 
* <p><b>Run-time Exceptions</b>
* <p> If this specific dynamic call site completed resolution of its call site specifier, it implies that a non-null reference to an instance of java.lang.invoke.CallSite is bound to this dynamic call site. Therefore, the operand stack item which represents a reference to the target of the call site object is never null. Similarly, it implies that the method descriptor in the call site specifier is semantically equal to the type descriptor of the method handle to be invoked as if by execution of an invokevirtual instruction.
* <br> Если этот конкретный сайт динамического вызова завершил разрешение своего спецификатора сайта вызова, это означает, что ненулевая ссылка на экземпляр java.lang.invoke.CallSite привязана к этому сайту динамического вызова. Следовательно, элемент стека операндов, который представляет ссылку на цель объекта сайта вызова, никогда не имеет значения NULL. Точно так же это подразумевает, что дескриптор метода в спецификаторе сайта вызова семантически равен дескриптору типа дескриптора метода, который должен быть вызван, как если бы при выполнении команды invokevirtual.
* 
* <p> These invariants mean that an invokedynamic instruction which is bound to a call site object never throws a NullPointerException or a java.lang.invoke.WrongMethodTypeException.
* <br> Эти инварианты означают, что invokedynamic инструкция, которая привязана к объекту сайта вызова, никогда не вызывает исключение NullPointerException или java.lang.invoke.WrongMethodTypeException.
 * @param name
 * @param desc
 * @param args
 */
case class MInvokeDynamicInsn(
  name:String,
  desc:MDesc,
  bootstrapMethod:bm.Handle,
  args:Seq[BootstrapArg]
) extends MethCode

/** 
 * Инструкция перехода
 * 
 * <p>
 * Возможные OpCode:
 * 
 * {@link OpCode#IFEQ},
 * {@link OpCode#IFNE},
 * {@link OpCode#IFLT},
 * {@link OpCode#IFGE},
 * {@link OpCode#IFGT},
 * {@link OpCode#IFLE},
 * {@link OpCode#IF_ICMPEQ},
 * {@link OpCode#IF_ICMPNE},
 * {@link OpCode#IF_ICMPLT},
 * {@link OpCode#IF_ICMPGE},
 * {@link OpCode#IF_ICMPGT},
 * {@link OpCode#IF_ICMPLE},
 * {@link OpCode#IF_ACMPEQ},
 * {@link OpCode#IF_ACMPNE},
 *
 * {@link OpCode#GOTO},
 *
 * {@link OpCode#JSR},
 *
 * {@link OpCode#IFNULL} or 
 * {@link OpCode#IFNONNULL}.
 * @param op
 * @param label
 */
case class MJumpInsn(op:OpCode,label:String) extends MethCode

/** 
 * Метка в исходном коде/точка перехода
 * @param name
 */
case class MLabel(name:String) extends MethCode

/**
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.ldc">Push item from run-time constant pool</a>
 * <p> Описание
 * <p> The index is an unsigned byte that must be a valid index into the run-time constant pool of the current class (§2.6). The run-time constant pool entry at index either must be a run-time constant of type int or float, or a reference to a string literal, or a symbolic reference to a class, method type, or method handle (§5.1).
 * <p> If the run-time constant pool entry is a run-time constant of type int or float, the numeric value of that run-time constant is pushed onto the operand stack as an int or float, respectively.
 * <p> Otherwise, if the run-time constant pool entry is a reference to an instance of class String representing a string literal (§5.1), then a reference to that instance, value, is pushed onto the operand stack.
 * <p> Otherwise, if the run-time constant pool entry is a symbolic reference to a class (§5.1), then the named class is resolved (§5.4.3.1) and a reference to the Class object representing that class, value, is pushed onto the operand stack.
 * <p> Otherwise, the run-time constant pool entry must be a symbolic reference to a method type or a method handle (§5.1). The method type or method handle is resolved (§5.4.3.5) and a reference to the resulting instance of java.lang.invoke.MethodType or java.lang.invoke.MethodHandle, value, is pushed onto the operand stack.
 * <hr>
 * Индекс - это беззнаковый байт(или слово), который должен быть допустимым индексом в пуле констант времени выполнения текущего класса (§2.6). Запись пула констант времени выполнения по индексу должна быть либо константой времени выполнения типа int или float, либо ссылкой на строковый литерал, либо символьной ссылкой на класс, тип метода или дескриптор метода (§5.1).
 * <p> Если запись пула констант времени выполнения является константой времени выполнения типа int или float, числовое значение этой константы времени выполнения помещается в стек операндов как int или float соответственно.
 * <p> В противном случае, если запись пула констант времени выполнения является ссылкой на экземпляр класса String, представляющий строковый литерал (§5.1), то ссылка на этот экземпляр value помещается в стек операндов.
 * <p> В противном случае, если запись пула констант времени выполнения является символической ссылкой на класс (§5.1), то именованный класс разрешается (§5.4.3.1) и ссылка на объект Class, представляющий этот класс, значение, помещается в стек операндов.
 * <p> В противном случае запись пула констант времени выполнения должна быть символьной ссылкой на тип метода или дескриптор метода (§5.1). Тип метода или дескриптор метода разрешается (§5.4.3.5), и ссылка на результирующий экземпляр java.lang.invoke.MethodType или java.lang.invoke.MethodHandle, value, помещается в стек операндов.
 * <hr>
 * 
 * LDC instruction. Note that new constant types may be added in future versions of the
 * Java Virtual Machine. To easily detect new constant types, implementations of this method
 * should check for unexpected constant types, like this:
 * 
* <pre>
* if (cst instanceof Integer) {
*     // ... {@link LdcType#Integer}
* } else if (cst instanceof Float) {
*     // ... {@link LdcType#Float}
* } else if (cst instanceof Long) {
*     // ... {@link LdcType#Long}
* } else if (cst instanceof Double) {
*     // ... {@link LdcType#Double}
* } else if (cst instanceof String) {
*     // ... {@link LdcType#String}
* } else if (cst instanceof org.objectweb.asm.Type) {
*     int sort = ((org.objectweb.asm.Type) cst).getSort();
*     if (sort == org.objectweb.asm.Type.OBJECT) {
*         // ... {@link LdcType#Object}
*     } else if (sort == org.objectweb.asm.Type.ARRAY) {
*         // ... {@link LdcType#Array}
*     } else if (sort == org.objectweb.asm.Type.METHOD) {
*         // ... {@link LdcType#Method}
*     } else {
*         // throw an exception 
*     }
* } else if (cst instanceof Handle) {
*     // ... {@link LdcType#Handle}
* } else if (cst instanceof ConstantDynamic) {
*     // ... {@link LdcType#ConstantDynamic}
* } else {
*     // throw an exception
* }
* </pre>
 * @param value
 * @param ldcType
 */
case class MLdcInsn(value:AnyRef) extends MethCode {
    //lazy val ldcType:LdcType
}

/** 
 * Номер строки в исходном коде
 * @param line a line number. This number refers to the source file from which the class was compiled.
 * @param label the first instruction corresponding to this line number.
 */
case class MLineNumber(line:Int,label:String) extends MethCode

/** 
 * Определение локальной переменной
 * @param name
 * @param desc
 * @param sign
 * @param labelStart
 * @param labelEnd
 * @param index
 */
case class MLocalVariable(name:String,desc:TDesc,sign:Option[Sign],labelStart:String,labelEnd:String,index:Int) extends MethCode

/** 
 * @param typeRef
 * @param typePath
 * @param startLabels
 * @param endLabels
 * @param index
 * @param desc
 * @param visible
 * @param annotations
 */
case class MLocalVariableAnnotation(
  typeRef:MTypeLocalVarRef,
  typePath:Option[String],
  startLabels:Seq[String],
  endLabels:Seq[String],
  index:Seq[Int],
  desc:TDesc,
  visible:Boolean,
  annotations:Seq[AnnCode]
  ) extends MethCode

/**
 * lookupswitch
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Access jump table by key match and jump
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * lookupswitch
 * &lt;0-3 byte pad&gt;
 * defaultbyte1
 * defaultbyte2
 * defaultbyte3
 * defaultbyte4
 * npairs1
 * npairs2
 * npairs3
 * npairs4
 * match-offset pairs...
 * </pre>
 * 
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * lookupswitch = 171 (0xab)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * ..., key →
 *
 * ...
 * <h2 style="font-weight: bold">Description</h2>
 *
 * A lookupswitch is a variable-length instruction. Immediately after the 
 * lookupswitch opcode, between zero and three bytes must act as padding, such 
 * that defaultbyte1 begins at an address that is a multiple of four bytes from 
 * the start of the current method (the opcode of its first instruction). 
 * Immediately after the padding follow a series of signed 32-bit values: 
 * default, npairs, and then npairs pairs of signed 32-bit values. The npairs 
 * must be greater than or equal to 0. Each of the npairs pairs consists of an 
 * int match and a signed 32-bit offset. Each of these signed 32-bit values is 
 * constructed from four unsigned bytes as (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | byte4.
 *
 * <p>
 * The table match-offset pairs of the lookupswitch instruction must be sorted 
 * in increasing numerical order by match.
 *
 * <p>
 * The key must be of type int and is popped from the operand stack. The key is 
 * compared against the match values. If it is equal to one of them, then a target 
 * address is calculated by adding the corresponding offset to the address of the 
 * opcode of this lookupswitch instruction. If the key does not match any of the 
 * match values, the target address is calculated by adding default to the address 
 * of the opcode of this lookupswitch instruction. Execution then continues at 
 * the target address.
 *
 * <p>
 * The target address that can be calculated from the offset of each match-offset 
 * pair, as well as the one calculated from default, must be the address of an 
 * opcode of an instruction within the method that contains this lookupswitch instruction.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * The alignment required of the 4-byte operands of the lookupswitch instruction 
 * guarantees 4-byte alignment of those operands if and only if the method that 
 * contains the lookupswitch is positioned on a 4-byte boundary.
 *
 * <p>
 * The match-offset pairs are sorted to support lookup routines that are quicker than linear search.
 * 
 * <hr>
 * Поисковый переключатель - это инструкция переменной длины. 
 * Сразу после кода операции lookupswitch от нуля до трех байтов должны действовать 
 * как заполнители, так что defaultbyte1 начинается с адреса, кратного четырем байтам 
 * от начала текущего метода (кода операции его первой инструкции). Сразу после заполнения следует 
 * ряд 32-битных значений со знаком: default, npairs, а затем npairs пары 32-битных значений со 
 * знаком. Число npairs должно быть больше или равно 0. Каждая из пар npairs состоит из 
 * соответствия int и 32-битного смещения со знаком. Каждое из этих 32-битных значений со 
 * знаком состоит из четырех байтов без знака 
 * как (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | байт4.
 * 
 * <p> Пары совпадения-смещения таблицы инструкции lookupswitch должны 
 * быть отсортированы в возрастающем числовом порядке по совпадению.
 * 
 * <p> Ключ должен иметь тип int и извлекается из стека операндов. 
 * Ключ сравнивается со значениями соответствия. Если он равен одному из них, 
 * то целевой адрес вычисляется путем добавления соответствующего смещения к адресу 
 * кода операции этой инструкции lookupswitch. Если ключ не соответствует ни одному 
 * из значений соответствия, целевой адрес вычисляется путем добавления значения 
 * по умолчанию к адресу кода операции этой инструкции lookupswitch. Затем 
 * выполнение продолжается по целевому адресу.
 * 
 * <p> Целевой адрес, который может быть вычислен по смещению каждой пары 
 * совпадение-смещение, а также адрес, вычисленный по умолчанию, должен быть 
 * адресом кода операции инструкции в методе, который содержит эту инструкцию 
 * переключателя поиска.
 * 
 * <h2 style = "font-weight: bold"> Примечания </h2>
 * Выравнивание, необходимое для 4-байтовых операндов инструкции lookupwitch, 
 * гарантирует 4-байтовое выравнивание этих операндов тогда и только тогда, когда 
 * метод, который содержит lookupswitch, расположен на 4-байтовой границе.
 * <p> Пары совпадения-смещения сортируются для поддержки процедур поиска, 
 * которые работают быстрее, чем линейный поиск.
 * @param defaultHandle
 * @param keys
 * @param labels
 */
case class MLookupSwitchInsn(defaultHandle:String,keys:Seq[Int],labels:Seq[String]) extends MethCode

/** 
 * максимальный размер стека и максимальное количество локальных переменных метода.
 * @param maxStack  максимальный размер стека метода.
 * @param maxLocal  максимальное количество локальных переменных для метода.
 */
case class MMaxs(maxStack:Int,maxLocal:Int) extends MethCode

/**
 * This opcode is either
 * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
 * <hr>
 * <a href="https://coderoad.ru/13764238/%D0%97%D0%B0%D1%87%D0%B5%D0%BC-%D0%BD%D1%83%D0%B6%D0%B5%D0%BD-invokeSpecial-%D0%BA%D0%BE%D0%B3%D0%B4%D0%B0-%D1%81%D1%83%D1%89%D0%B5%D1%81%D1%82%D0%B2%D1%83%D0%B5%D1%82-invokeVirtual">Зачем нужен invokeSpecial, когда существует invokeVirtual</a>
 * <br> Ответ можно легко найти, если внимательно прочитать спецификацию Java VM:
 * Разница между инструкциями invokespecial и invokevirtual заключается в том, что invokevirtual вызывает метод, основанный на классе объекта. Инструкция invokespecial используется для вызова методов инициализации экземпляра, а также частных методов и методов суперкласса текущего класса.
 * Другими словами, invokespecial используется для вызова методов, не заботясь о динамической привязке, чтобы вызвать версию метода конкретного класса.
 * 
 * <hr>
 * <a href="">В чем смысл invokeinterface?</a> <br>
 * Каждый класс Java связан с таблицей виртуальных методов , содержащей "links" байт-кода каждого метода класса. 
 * Эта таблица наследуется от суперкласса определенного класса и расширяется в отношении новых методов подкласса. E.g.,
<pre>
class BaseClass {
    public void method1() { }
    public void method2() { }
    public void method3() { }
}

class NextClass extends BaseClass {
    public void method2() { } // overridden from BaseClass
    public void method4() { }
}
</pre>
* результаты в таблицах
<pre>
BaseClass
1. BaseClass/method1()
2. BaseClass/method2()
3. BaseClass/method3()

NextClass
1. BaseClass/method1()
2. NextClass/method2()
3. BaseClass/method3()
4. NextClass/method4()
</pre>
* Обратите внимание, как таблица виртуальных методов NextClass сохраняет порядок записей таблицы BaseClass и просто перезаписывает "link" из method2() , который она переопределяет.
* <p> Таким образом, реализация JVM может оптимизировать вызов invokevirtual , помня, что BaseClass/method3() всегда будет третьей записью в таблице виртуальных методов любого объекта, на котором когда-либо будет вызван этот метод.
* <p> С invokeinterface такая оптимизация невозможна. E.g.,
<pre>
interface MyInterface {
    void ifaceMethod();
}

class AnotherClass extends NextClass implements MyInterface {
    public void method4() { } // overridden from NextClass
    public void ifaceMethod() { }
}

class MyClass implements MyInterface {
    public void method5() { }
    public void ifaceMethod() { }
}
</pre>
* Эта иерархия классов приводит к таблицам виртуальных методов
<pre>
AnotherClass
1. BaseClass/method1()
2. NextClass/method2()
3. BaseClass/method3()
4. AnotherClass/method4()
5. MyInterface/ifaceMethod()

MyClass
1. MyClass/method5()
2. MyInterface/ifaceMethod()
</pre>
* Как вы можете видеть, AnotherClass содержит метод интерфейса в его пятой записи, 
* а MyClass содержит его во второй записи. Чтобы на самом деле найти правильную запись 
* в таблице виртуальных методов, вызов метода с invokeinterface всегда должен будет 
* искать полную таблицу, не имея возможности для стиля оптимизации, который делает invokevirtual.
* 
* <p> Существуют дополнительные различия, такие как тот факт, что invokeinterface 
* может использоваться вместе со ссылками на объекты, которые на самом деле не 
* реализуют интерфейс. Поэтому invokeinterface должен будет проверить во время 
* выполнения, существует ли метод в таблице, и потенциально вызвать исключение.
 * @param op
 * @param owner
 * @param name
 * @param desc
 * @param iface
 */
case class MMethodInsn(op:OpCode,owner:String,name:String,desc:MDesc,iface:Boolean) extends MethCode

/**
 * multianewarray
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Create new multidimensional array
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * multianewarray
 * indexbyte1
 * indexbyte2
 * dimensions
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * multianewarray = 197 (0xc5)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., count1, [count2, ...] →
 * ..., arrayref
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * The dimensions operand is an unsigned byte that must be greater than or equal to 1. It represents the number of dimensions of the array to be created. The operand stack must contain dimensions values. Each such value represents the number of components in a dimension of the array to be created, must be of type int, and must be non-negative. The count1 is the desired length in the first dimension, count2 in the second, etc.
 *
 * <p>
 * All of the count values are popped off the operand stack. The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 &lt;&lt; 8) | indexbyte2. The run-time constant pool item at the index must be a symbolic reference to a class, array, or interface type. The named class, array, or interface type is resolved (§5.4.3.1). The resulting entry must be an array class type of dimensionality greater than or equal to dimensions.
 *
 * <p>
 * A new multidimensional array of the array type is allocated from the garbage-collected heap. If any count value is zero, no subsequent dimensions are allocated. The components of the array in the first dimension are initialized to subarrays of the type of the second dimension, and so on. The components of the last allocated dimension of the array are initialized to the default initial value (§2.3, §2.4) for the element type of the array type. A reference arrayref to the new array is pushed onto the operand stack.
 * <h2 style="font-weight: bold">Linking Exceptions</h2>
 *
 * During resolution of the symbolic reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be thrown.
 *
 * <p>
 * Otherwise, if the current class does not have permission to access the element type of the resolved array class, multianewarray throws an IllegalAccessError.
 *
 * <h2 style="font-weight: bold">Run-time Exception</h2>
 *
 * Otherwise, if any of the dimensions values on the operand stack are less than zero, the multianewarray instruction throws a NegativeArraySizeException.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * It may be more efficient to use newarray or anewarray (§newarray, §anewarray) when creating an array of a single dimension.
 *
 * <p>
 * The array class referenced via the run-time constant pool may have more dimensions than the dimensions operand of the multianewarray instruction. In that case, only the first dimensions of the dimensions of the array are created.
 * @param desc
 * @param numDimensions
 */
case class MMultiANewArrayInsn(desc:TDesc,numDimensions:Int) extends MethCode

/** 
 * Visits a parameter of this method.
 * @param name parameter name or {@literal null} if none is provided.
 * @param access the parameter's access flags, only {@code ACC_FINAL}, {@code ACC_SYNTHETIC} or/and {@code ACC_MANDATED} are allowed (see {@link Opcodes}).
 */
case class MParameter(name:Option[String],access:MParameterAccess) extends MethCode
case class MParameterAccess(raw:Int)

/** 
 * @param param
 * @param desc
 * @param visible
 * @param annotations
 */
case class MParameterAnnotation(param:Int,desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode

/**
 * tableswitch
 * <h2 style="font-weight: bold">Operation</h2>
 *
 * Access jump table by index and jump
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * tableswitch
 * &lt;0-3 byte pad&gt;
 * defaultbyte1
 * defaultbyte2
 * defaultbyte3
 * defaultbyte4
 * lowbyte1
 * lowbyte2
 * lowbyte3
 * lowbyte4
 * highbyte1
 * highbyte2
 * highbyte3
 * highbyte4
 * jump offsets...
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 *
 * tableswitch = 170 (0xaa)
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ..., index →
 * ...
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 *
 * A tableswitch is a variable-length instruction. Immediately after the tableswitch opcode, between zero and three bytes must act as padding, such that defaultbyte1 begins at an address that is a multiple of four bytes from the start of the current method (the opcode of its first instruction). Immediately after the padding are bytes constituting three signed 32-bit values: default, low, and high. Immediately following are bytes constituting a series of high - low + 1 signed 32-bit offsets. The value low must be less than or equal to high. The high - low + 1 signed 32-bit offsets are treated as a 0-based jump table. Each of these signed 32-bit values is constructed as (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | byte4.
 *
 * <p>
 * The index must be of type int and is popped from the operand stack. If index is less than low or index is greater than high, then a target address is calculated by adding default to the address of the opcode of this tableswitch instruction. Otherwise, the offset at position index - low of the jump table is extracted. The target address is calculated by adding that offset to the address of the opcode of this tableswitch instruction. Execution then continues at the target address.
 *
 * <p>
 * The target address that can be calculated from each jump table offset, as well as the one that can be calculated from default, must be the address of an opcode of an instruction within the method that contains this tableswitch instruction.
 * <h2 style="font-weight: bold">Notes</h2>
 *
 * The alignment required of the 4-byte operands of the tableswitch instruction guarantees 4-byte alignment of those operands if and only if the method that contains the tableswitch starts on a 4-byte boundary.
 * 
 * <hr>
 * Tablewitch - это инструкция переменной длины. 
 * Сразу после кода операции tablewitch от нуля до трех байтов должны действовать как заполнители, 
 * так что defaultbyte1 начинается с адреса, кратного четырем байтам от начала текущего метода 
 * (кода операции его первой инструкции). 
 * Сразу после заполнения идут байты, составляющие три 32-битных значения со знаком: 
 * по умолчанию, младший и высокий. Сразу после этого следуют байты, составляющие серию 
 * 32-битных смещений со знаком старшего и младшего + 1 со знаком. 
 * Значение low должно быть меньше или равно high. 
 * 32-битные смещения со знаком + 1 (high-low + 1) обрабатываются как таблица переходов с отсчетом от 0. 
 * Каждое из этих 32-битных значений со знаком строится 
 * как (byte1 &lt;&lt; 24) | (byte2 &lt;&lt; 16) | (byte3 &lt;&lt; 8) | байт4.
 * 
 * <p> Индекс должен иметь тип int и извлекается из стека операндов. 
 * Если index меньше low или index больше high, то целевой адрес вычисляется 
 * путем добавления значения по умолчанию к адресу кода операции этой инструкции tablewitch. 
 * В противном случае извлекается смещение в позиции index - low таблицы переходов. 
 * Целевой адрес вычисляется путем добавления этого смещения к адресу кода операции этой инструкции tablewitch. 
 * Затем выполнение продолжается по целевому адресу.
 * 
 * <p> Целевой адрес, который может быть вычислен из каждого смещения таблицы переходов, 
 * а также адрес, который может быть вычислен по умолчанию, должен быть адресом кода 
 * операции инструкции внутри метода, который содержит эту инструкцию tablewitch.
 * @param min
 * @param max
 * @param defaultLabel
 * @param labels
 */
case class MTableSwitchInsn(min:Int,max:Int,defaultLabel:String,labels:Seq[String]) extends MethCode

/** 
 * @param typeRef
 * @param typePath
 * @param desc
 * @param visible
 */
case class MTryCatchAnnotation(typeRef:MTypeTryCatchRef,typePath:Option[String],desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode

/** 
 * try catch block
 * @param startLabel
 * @param endLabel
 * @param handlerLabel
 * @param type
 */
case class MTryCatchBlock(startLabel:String,endLabel:String,handlerLabel:String,typeName:Option[String]) extends MethCode

/** 
 * @param typeRef
 * @param typePath
 * @param desc 
 * @param visible
 */
case class MTypeAnnotation(typeRef:MTypeRef,typePath:Option[String],desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode

/**
 * the opcode of the type instruction to be visited. This opcode is either NEW,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.anewarray">ANEWARRAY</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.checkcast">CHECKCAST</a> or
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.instanceof">INSTANCEOF</a>.
 * 
 * см {@link OpCode#ANEWARRAY}, {@link OpCode#CHECKCAST}, {@link OpCode#INSTANCEOF}
 * @param op
 * @param type
 */
case class MTypeInsn(op:OpCode,`type`:String) extends MethCode

/**
 * the opcode of the local variable instruction to be visited. This opcode is either
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.iload">ILOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.lload">LLOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.fload">FLOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.dload">DLOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.aload">ALOAD</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.istore">ISTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.lstore">LSTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.fstore">FSTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.dstore">DSTORE</a>,
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.astore">ASTORE</a> or
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.ret">RET</a>.
 *
 * <hr>
 *
 * <h1>iload</h1>{@link OpCode#ILOAD}
 *
 * <h2 style="font-weight: bold">Operation</h2>
 * Load int from local variable
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * iload
 * index
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * iload = 21 (0x15)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 * ... → <br>
 * ..., value <br>
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the
 * current frame (§2.6). The local variable at index must contain an int.
 * The value of the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The iload opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 *
 * lload
 * <h2 style="font-weight: bold">Operation</h2> {@link OpCode#LLOAD}
 * Load long from local variable
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * lload
 * index
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * lload = 22 (0x16)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array
 * of the current frame (§2.6). The local variable at index must contain a long.
 * The value of the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The lload opcode can be used in conjunction with the wide instruction (§wide) to
 * access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * fload {@link OpCode#FLOAD}
 * <h2 style="font-weight: bold">Operation</h2>
 * Load float from local variable
 *
 * <h2 style="font-weight: bold">Format</h2>
 *
 * <pre>
 * fload
 * index
 * </pre>
 *
 * <h2 style="font-weight: bold">Forms</h2>
 * fload = 23 (0x17)
 *
 * <h2 style="font-weight: bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight: bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The local variable at index must contain a float. The value of the local variable at index is pushed onto
 * the operand stack.
 *
 * <h2 style="font-weight: bold">Notes</h2>
 * The fload opcode can be used in conjunction with the wide instruction (§wide) to access a local
 * variable using a two-byte unsigned index.
 * <hr>
 *
 * fload_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 * Load float from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * fload_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * fload_0 = 34 (0x22)
 * fload_1 = 35 (0x23)
 * fload_2 = 36 (0x24)
 * fload_3 = 37 (0x25)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The local variable at &lt;n&gt; must contain a float.
 * The value of the local variable at &lt;n&gt; is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * Each of the fload_&lt;n&gt; instructions is the same as fload with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * dload {@link OpCode#DLOAD}
 * <h2 style="font-weight:bold">Operation</h2>
 * Load double from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * dload
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 * dload = 24 (0x18)
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array
 * of the current frame (§2.6). The local variable at index must contain a double.
 * The value of the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * The dload opcode can be used in conjunction with the wide instruction (§wide) to access a local
 * variable using a two-byte unsigned index.
 *
 * <hr>
 * dload_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 * Load double from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * dload_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 * <pre>
 * dload_0 = 38 (0x26)
 * dload_1 = 39 (0x27)
 * dload_2 = 40 (0x28)
 * dload_3 = 41 (0x29)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 * <pre>
 * ... →
 * ..., value
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * Both &lt;n&gt; and &lt;n&gt;+1 must be indices into the local variable array of the current frame (§2.6).
 * The local variable at &lt;n&gt; must contain a double. The value of the local variable at &lt;n&gt;
 * is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * Each of the dload_&lt;n&gt; instructions is the same as dload with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * aload {@link OpCode#ALOAD}
 * <h2 style="font-weight:bold">Operation</h2>
 * Load reference from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * aload
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 * aload = 25 (0x19)
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., objectref
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The local variable at index must contain a reference.
 * The objectref in the local variable at index is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * The aload instruction cannot be used to load a value of type returnAddress from a
 * local variable onto the operand stack. This asymmetry with the astore instruction (§astore) is intentional.
 *
 * <p>
 * The aload opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * aload_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 * Load reference from local variable
 *
 * <h2 style="font-weight:bold">Format</h2>
 *
 * aload_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * aload_0 = 42 (0x2a)
 * aload_1 = 43 (0x2b)
 * aload_2 = 44 (0x2c)
 * aload_3 = 45 (0x2d)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ... →
 * ..., objectref
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The local variable at &lt;n&gt; must contain a reference.
 * The objectref in the local variable at &lt;n&gt; is pushed onto the operand stack.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 * An aload_&lt;n&gt; instruction cannot be used to load a value of type returnAddress
 * from a local variable onto the operand stack.
 * This asymmetry with the corresponding astore_&lt;n&gt; instruction (§astore_&lt;n&gt;) is intentional.
 *
 * <p>
 * Each of the aload_&lt;n&gt; instructions is
 * the same as aload with an index of &lt;n&gt;, except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * istore {@link OpCode#ISTORE}
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store int into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * istore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * istore = 54 (0x36)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte that must be an index into the local variable array of the current
 * frame (§2.6). The value on the top of the operand stack must be of type int.
 * It is popped from the operand stack, and the value of the local variable at index is set to value.
 * Notes
 *
 * <p>
 * The istore opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * istore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store int into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * istore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * istore_0 = 59 (0x3b)
 * istore_1 = 60 (0x3c)
 * istore_2 = 61 (0x3d)
 * istore_3 = 62 (0x3e)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type int.
 * It is popped from the operand stack, and the value of the local variable at &lt;n&gt; is set to value.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the istore_&lt;n&gt; instructions is the same as istore with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * lstore {@link OpCode#LSTORE}
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store long into local variable
 *<h2 style="font-weight:bold"> Format</h2>
 *
 * <pre>
 * lstore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * lstore = 55 (0x37)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type long.
 * It is popped from the operand stack, and the local variables at index and index+1 are set to value.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The lstore opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * lstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store long into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * lstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * lstore_0 = 63 (0x3f)
 * lstore_1 = 64 (0x40)
 * lstore_2 = 65 (0x41)
 * lstore_3 = 66 (0x42)
 * </pre>
 * 
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 * 
 * <h2 style="font-weight:bold">Description</h2>
 *
 * Both &lt;n&gt; and &lt;n&gt;+1 must be indices into the local variable array of the 
 * current frame (§2.6). The value on the top of the operand stack must be of type long. 
 * It is popped from the operand stack, and the local variables at &lt;n&gt; and &lt;n&gt;+1 are set to value.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the lstore_&lt;n&gt; instructions is the same as lstore with an index of &lt;n&gt;, except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * fstore {@link OpCode#FSTORE}
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store float into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * fstore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * fstore = 56 (0x38)
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type float.
 * It is popped from the operand stack and undergoes value set conversion (§2.8.3),
 * resulting in value'. The value of the local variable at index is set to value'.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The fstore opcode can be used in conjunction with the wide instruction (§wide)
 * to access a local variable using a two-byte unsigned index.
 *
 * <hr>
 * fstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store float into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * fstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * fstore_0 = 67 (0x43)
 * fstore_1 = 68 (0x44)
 * fstore_2 = 69 (0x45)
 * fstore_3 = 70 (0x46)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type float.
 * It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'.
 * The value of the local variable at &lt;n&gt; is set to value'.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the fstore_&lt;n&gt; instructions is the same as fstore
 * with an index of &lt;n&gt;, except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * dstore {@link OpCode#DSTORE}
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store double into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * dstore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * dstore = 57 (0x39)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current
 * frame (§2.6). The value on the top of the operand stack must be of type double. It is popped from the operand
 * stack and undergoes value set conversion (§2.8.3), resulting in value'.
 * The local variables at index and index+1 are set to value'.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The dstore opcode can be used in conjunction with the wide instruction (§wide) to access a local
 * variable using a two-byte unsigned index.
 *
 * <hr>
 * dstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store double into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * dstore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * dstore_0 = 71 (0x47)
 * dstore_1 = 72 (0x48)
 * dstore_2 = 73 (0x49)
 * dstore_3 = 74 (0x4a)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., value →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * Both &lt;n&gt; and &lt;n&gt;+1 must be indices into the local variable array of the current frame (§2.6).
 * The value on the top of the operand stack must be of type double. It is popped from the operand
 * stack and undergoes value set conversion (§2.8.3), resulting in value'.
 * The local variables at &lt;n&gt; and &lt;n&gt;+1 are set to value'.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Each of the dstore_&lt;n&gt; instructions is the same as dstore with an index of &lt;n&gt;,
 * except that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * astore {@link OpCode#ASTORE}
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store reference into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 * <pre>
 * astore
 * index
 * </pre>
 *
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * astore = 58 (0x3a)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., objectref →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6).
 * The objectref on the top of the operand stack must be of type returnAddress or of type reference.
 * It is popped from the operand stack, and the value of the local variable at index is set to objectref.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * The astore instruction is used with an objectref of type returnAddress when implementing the finally
 * clause of the Java programming language (§3.13).
 *
 * <p>
 * The aload instruction (§aload) cannot be used to load a value of type returnAddress from a
 * local variable onto the operand stack. This asymmetry with the astore instruction is intentional.
 *
 * <p>
 * The astore opcode can be used in conjunction with the wide instruction (§wide) to access a
 * local variable using a two-byte unsigned index.
 *
 * <hr>
 * astore_&lt;n&gt;
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Store reference into local variable
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * astore_&lt;n&gt;
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * <pre>
 * astore_0 = 75 (0x4b)
 * astore_1 = 76 (0x4c)
 * astore_2 = 77 (0x4d)
 * astore_3 = 78 (0x4e)
 * </pre>
 *
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * <pre>
 * ..., objectref →
 * ...
 * </pre>
 *
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The &lt;n&gt; must be an index into the local variable array of the current frame (§2.6).
 * The objectref on the top of the operand stack must be of type returnAddress or of type reference.
 * It is popped from the operand stack, and the value of the local variable at &lt;n&gt; is set to objectref.
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * An astore_&lt;n&gt; instruction is used with an objectref of type returnAddress when implementing
 * the finally clauses of the Java programming language (§3.13).
 *
 * <p>
 * An aload_&lt;n&gt; instruction (§aload_&lt;n&gt;) cannot be used to load a value of type
 * returnAddress from a local variable onto the operand stack. This asymmetry with the
 * corresponding astore_&lt;n&gt; instruction is intentional.
 *
 * <p>
 * Each of the astore_&lt;n&gt; instructions is the same as astore with an index of &lt;n&gt;, except
 * that the operand &lt;n&gt; is implicit.
 *
 * <hr>
 * ret {@link OpCode#RET}
 * <h2 style="font-weight:bold">Operation</h2>
 *
 * Return from subroutine
 * <h2 style="font-weight:bold">Format</h2>
 *
 *
 * ret
 * index
 * <h2 style="font-weight:bold">Forms</h2>
 *
 * ret = 169 (0xa9)
 * <h2 style="font-weight:bold">Operand Stack</h2>
 *
 * No change
 * <h2 style="font-weight:bold">Description</h2>
 *
 * The index is an unsigned byte between 0 and 255, inclusive.
 * The local variable at index in the current frame (§2.6) must contain a value of type returnAddress.
 * The contents of the local variable are written into
 * the Java Virtual Machine's pc register, and execution continues there.
 *
 * <h2 style="font-weight:bold">Notes</h2>
 *
 * Note that jsr (§jsr) pushes the address onto the operand stack and ret gets it out of a local variable.
 * This asymmetry is intentional.
 *
 * <p>
 * In Oracle's implementation of a compiler for the Java programming language prior to Java SE 6,
 * the ret instruction was used with the jsr and jsr_w instructions (§jsr, §jsr_w)
 * in the implementation of the finally clause (§3.13, §4.10.2.5).
 *
 * <p>
 * The ret instruction should not be confused with the return instruction (§return).
 * A return instruction returns control from a method to its invoker,
 * without passing any value back to the invoker.
 *
 * <p>
 * The ret opcode can be used in conjunction with the wide instruction (§wide) to
 * access a local variable using a two-byte unsigned index.
 * 
 * <hr>
 * Обратите внимание, что jsr (§jsr) помещает адрес в стек операндов, а ret получает его из локальной переменной. Эта асимметрия преднамеренная.
 * <p> В реализации Oracle компилятора для языка программирования Java до Java SE 6 инструкция ret использовалась с инструкциями jsr и jsr_w (§jsr, §jsr_w) в реализации предложения finally (§3.13, §4.10.2.5) ).
 * <p> Инструкцию ret не следует путать с инструкцией return (§return). Команда возврата возвращает управление от метода вызывающей стороне, не передавая никакого значения обратно вызывающей стороне.
 * <p> Код операции ret может использоваться вместе с инструкцией wide (§wide) для доступа к локальной переменной с использованием двухбайтового беззнакового индекса.
 * @param op
 * @param variable
 */
case class MVarInsn(op:OpCode,variable:Int) extends MethCode
