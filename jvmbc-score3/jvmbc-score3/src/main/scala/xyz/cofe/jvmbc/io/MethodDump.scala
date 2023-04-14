package xyz.cofe.jvmbc
package io

import mth._
import mth.{Label => LBL}
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ModuleVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.RecordComponentVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.TypePath
import org.objectweb.asm.Attribute
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import xyz.cofe.jvmbc.parse.desc.{Method => MDesc}
import xyz.cofe.jvmbc.parse.desc.{ObjectType => JavaName}

/**
 * Парсинг метода класса
 * 
 * <pre>
 * order: 
 *   
 * ( {@code visitParameter} )* 
 * [ {@code visitAnnotationDefault} ] 
 * ( {@code visitAnnotation} 
 * | {@code visitAnnotableParameterCount}
 * | {@code visitParameterAnnotation} {@code visitTypeAnnotation} 
 * | {@code visitAttribute} 
 * )* 
 * [ {@code visitCode} 
 *   ( {@code visitFrame} 
 *   | {@code visit<i>X</i>Insn} 
 *   | {@code visitLabel} 
 *   | {@code visitInsnAnnotation} 
 *   | {@code visitTryCatchBlock} 
 *   | {@code visitTryCatchAnnotation} 
 *   | {@code visitLocalVariable} 
 *   | {@code visitLocalVariableAnnotation} 
 *   | {@code visitLineNumber} 
 *   )* 
 *   {@code visitMaxs} 
 * ] 
 * {@code visitEnd}
 * </pre>
 * .
 * 
 * In addition, the {@code visit<i>X</i>Insn} and {@code visitLabel} methods must be called in the
 * sequential order of the bytecode instructions of the visited code, 
 * 
 * {@code visitInsnAnnotation} must be called <i>after</i> the annotated instruction, 
 * 
 * {@code visitTryCatchBlock} must be called <i>before</i> the labels passed as arguments have been visited, 
 * 
 * {@code visitTryCatchBlockAnnotation} must be called <i>after</i> the corresponding try catch block has
 * been visited, 
 * 
 * and the {@code visitLocalVariable}, 
 * {@code visitLocalVariableAnnotation} and 
 * {@code visitLineNumber} 
 * methods must be called <i>after</i> the labels passed as arguments have been visited.
 * 
 */
class MethodDump(
  private val _api:Int,
  atEnd:Option[Either[String,Seq[MethCode]]=>Unit]=None
) extends MethodVisitor(_api) {
  var body:List[Either[String,MethCode]] = List()

  /**
   * Visits a parameter of this method.
   *
   * @param name parameter name or {@literal null} if none is provided.
   * @param access the parameter's access flags, only {@code ACC_FINAL}, {@code ACC_SYNTHETIC}
   *     or/and {@code ACC_MANDATED} are allowed (see {@link Opcodes}).
   */
  override def visitParameter(name:String, access:Int):Unit =
    body = Right(
      MParameter(
        Option(name),
        MParameterAccess(access)
      )
    ) +: body

  /**
   * Visits the default value of this annotation interface method.
   *
   * @return a visitor to the visit the actual default value of this annotation interface method, or
   *     {@literal null} if this visitor is not interested in visiting this default value. The
   *     'name' parameters passed to the methods of this annotation visitor are ignored. Moreover,
   *     exacly one visit method must be called on this annotation visitor, followed by visitEnd.
   */
  override def visitAnnotationDefault():AnnotationVisitor = 
    AnnotationDump(_api,Some(abodyEt => {
      body = abodyEt.map { body => 
        MAnnotationDefault(body)
      } +: body
    }))

  /**
   * Visits an annotation of this method.
   *
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitAnnotation(descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api,Some(abodyEt => {
      body = abodyEt.map { body => 
        MAnnotation(TDesc.unsafe(descriptor),visible,body)
      } +: body
    }))

  /**
   * Visits an annotation on a type in the method signature.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#METHOD_TYPE_PARAMETER}, {@link
   *     TypeReference#METHOD_TYPE_PARAMETER_BOUND}, {@link TypeReference#METHOD_RETURN}, {@link
   *     TypeReference#METHOD_RECEIVER}, {@link TypeReference#METHOD_FORMAL_PARAMETER} or {@link
   *     TypeReference#THROWS}. See {@link TypeReference}.
   * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
   *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
   *     'typeRef' as a whole.
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitTypeAnnotation(typeRef:Int, typePath:TypePath, descriptor:String, visible:Boolean):AnnotationVisitor =
    AnnotationDump(_api, Some(abodyEt => {
      body =
        abodyEt.map { body => 
          MTypeAnnotation(
            MTypeRef(typeRef),
            if typePath!=null then Some(typePath.toString) else None,
            TDesc.unsafe(descriptor),
            visible,
            body
          )
        } +: body
    }))

  /**
   * Visits the number of method parameters that can have annotations. By default (i.e. when this
   * method is not called), all the method parameters defined by the method descriptor can have
   * annotations.
   *
   * @param parameterCount the number of method parameters than can have annotations. This number
   *     must be less or equal than the number of parameter types in the method descriptor. It can
   *     be strictly less when a method has synthetic parameters and when these parameters are
   *     ignored when computing parameter indices for the purpose of parameter annotations (see
   *     https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
   * @param visible {@literal true} to define the number of method parameters that can have
   *     annotations visible at runtime, {@literal false} to define the number of method parameters
   *     that can have annotations invisible at runtime.
   */
  override def visitAnnotableParameterCount(parameterCount:Int, visible:Boolean):Unit =
    body = Right(
      MAnnotableParameterCount(parameterCount,visible)
    ) +: body

  /**
   * Visits an annotation of a parameter this method.
   *
   * @param parameter the parameter index. This index must be strictly smaller than the number of
   *     parameters in the method descriptor, and strictly smaller than the parameter count
   *     specified in {@link #visitAnnotableParameterCount}. Important note: <i>a parameter index i
   *     is not required to correspond to the i'th parameter descriptor in the method
   *     descriptor</i>, in particular in case of synthetic parameters (see
   *     https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitParameterAnnotation(parameter:Int, descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api, Some(abodyEt => {
      body =
        abodyEt.map { body => 
          MParameterAnnotation(
            parameter,
            TDesc.unsafe(descriptor),
            visible,
            body
          )
        } +: body
    }))

  /**
   * Visits a non standard attribute of this method.
   *
   * @param attribute an attribute.
   */
  override def visitAttribute(attribute:Attribute):Unit = {}

  /** Starts the visit of the method's code, if any (i.e. non abstract method). */
  override def visitCode():Unit = 
    body = Right(MCode()) +: body

  /**
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
   * @param type the type of this stack map frame. Must be {@link Opcodes#F_NEW} for expanded
   *     frames, or {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link
   *     Opcodes#F_SAME} or {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
   * @param numLocal the number of local variables in the visited frame.
   * @param local the local variable types in this frame. This array must not be modified. Primitive
   *     types are represented by {@link Opcodes#TOP}, {@link Opcodes#INTEGER}, {@link
   *     Opcodes#FLOAT}, {@link Opcodes#LONG}, {@link Opcodes#DOUBLE}, {@link Opcodes#NULL} or
   *     {@link Opcodes#UNINITIALIZED_THIS} (long and double are represented by a single element).
   *     Reference types are represented by String objects (representing internal names), and
   *     uninitialized types by Label objects (this label designates the NEW instruction that
   *     created this uninitialized value).
   * @param numStack the number of operand stack elements in the visited frame.
   * @param stack the operand stack types in this frame. This array must not be modified. Its
   *     content has the same format as the "local" array.
   * @throws IllegalStateException if a frame is visited just after another one, without any
   *     instruction between the two (unless this frame is a Opcodes#F_SAME frame, in which case it
   *     is silently ignored).
   */
  override def visitFrame(frameType:Int, numLocal:Int, local:Array[AnyRef] , numStack:Int, stack:Array[AnyRef]):Unit = 
    body = Right(MFrame(
      MFrameType(frameType),
      numLocal,
      local.map { e => 
        if e!=null then
          Some(MFrameElem(e).getOrElse(
            throw new Error("can't Dump method, please make issue https://github.com/gochaorg/jvmbc")
          ))
        else
          None
      },
      numStack,
      stack.map { e => 
        if e!=null then
          Some(MFrameElem(e).getOrElse(
            throw new Error("can't Dump method, please make issue https://github.com/gochaorg/jvmbc")
          ))
        else
          None
      }
    )) +: body

  /**
   * Visits a zero operand instruction.
   *
   * @param opcode the opcode of the instruction to be visited. This opcode is either NOP,
   *     ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
   *     LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD,
   *     FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE,
   *     AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2,
   *     SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV,
   *     FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR,
   *     LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I,
   *     D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
   *     DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
   */
  override def visitInsn(opcode:Int):Unit =
    body = Right(MInst(OpCode.find(opcode).get)) +: body

  /**
   * Visits an instruction with a single int operand.
   *
   * @param opcode the opcode of the instruction to be visited. This opcode is either BIPUSH, SIPUSH
   *     or NEWARRAY.
   * @param operand the operand of the instruction to be visited.<br>
   *     When opcode is BIPUSH, operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.
   *     <br>
   *     When opcode is SIPUSH, operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.
   *     <br>
   *     When opcode is NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link
   *     Opcodes#T_CHAR}, {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE},
   *     {@link Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
   */
  override def visitIntInsn(opcode:Int, operand:Int):Unit = 
    body = Right(MIntInsn(OpCode.find(opcode).get,operand)) +: body

  /**
   * Visits a local variable instruction. A local variable instruction is an instruction that loads
   * or stores the value of a local variable.
   *
   * @param opcode the opcode of the local variable instruction to be visited. This opcode is either
   *     ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
   * @param var the operand of the instruction to be visited. This operand is the index of a local
   *     variable.
   */
  override def visitVarInsn(opcode:Int, variable:Int):Unit =
    body = Right(MVarInsn(OpCode.find(opcode).get, Variable(variable))) +: body

  /**
   * Visits a type instruction. A type instruction is an instruction that takes the internal name of
   * a class as parameter.
   *
   * @param opcode the opcode of the type instruction to be visited. This opcode is either NEW,
   *     ANEWARRAY, CHECKCAST or INSTANCEOF.
   * @param type the operand of the instruction to be visited. This operand must be the internal
   *     name of an object or array class (see {@link Type#getInternalName()}).
   */
  override def visitTypeInsn(opcode:Int, typeName:String):Unit = 
    val code = OpCode.find(opcode).flatMap {
      case OpCode.NEW        => Some(MNew(JavaName.raw(typeName)))
      case OpCode.ANEWARRAY  => Some(MArrayNew(JavaName.raw(typeName)))
      case OpCode.CHECKCAST  => Some(MCheckCast(JavaName.raw(typeName)))
      case OpCode.INSTANCEOF => Some(MInstanceOf(JavaName.raw(typeName)))
      case _ => None
    }

    body = code.toRight( s"visitTypeInsn: opcode $opcode not matched, expected "+List(
      OpCode.NEW.code, OpCode.ANEWARRAY.code, OpCode.CHECKCAST.code, OpCode.INSTANCEOF.code
    ))  +: body

    //body = Right(MTypeInsn(OpCode.find(opcode).get, JavaName.raw(typeName))) +: body

  /**
   * Visits a field instruction. A field instruction is an instruction that loads or stores the
   * value of a field of an object.
   *
   * @param opcode the opcode of the type instruction to be visited. This opcode is either
   *     GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
   * @param owner the internal name of the field's owner class (see {@link Type#getInternalName()}).
   * @param name the field's name.
   * @param descriptor the field's descriptor (see {@link Type}).
   */
  override def visitFieldInsn(opcode:Int, owner:String, name:String, descriptor:String):Unit = 
    body = Right(MFieldInsn(
      OpCode.find(opcode).get,
      JavaName.raw(owner),
      name,
      TDesc.unsafe(descriptor)
    )) +: body

  /**
   * Visits a method instruction. A method instruction is an instruction that invokes a method.
   *
   * @param opcode the opcode of the type instruction to be visited. This opcode is either
   *     INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
   * @param owner the internal name of the method's owner class (see {@link
   *     Type#getInternalName()}).
   * @param name the method's name.
   * @param descriptor the method's descriptor (see {@link Type}).
   * @param isInterface if the method's owner class is an interface.
   */
  override def visitMethodInsn(opcode:Int, owner:String, name:String, descriptor:String, isInterface:Boolean):Unit =
    val inv:Option[MMethodInsn] = OpCode.find(opcode).flatMap {
      case OpCode.INVOKEVIRTUAL => 
        Some(MMethodInsn.InvokeVirtual(JavaName.raw(owner), name, MDesc.unsafe(descriptor), isInterface))
      case OpCode.INVOKESPECIAL => 
        Some(MMethodInsn.InvokeSpecial(JavaName.raw(owner), name, MDesc.unsafe(descriptor), isInterface))
      case OpCode.INVOKESTATIC => 
        Some(MMethodInsn.InvokeStatic(JavaName.raw(owner), name, MDesc.unsafe(descriptor), isInterface))
      case OpCode.INVOKEINTERFACE => 
        Some(MMethodInsn.InvokeIterface(JavaName.raw(owner), name, MDesc.unsafe(descriptor), isInterface))
      case _ => None
    }

    body =
      inv.toRight(
        s"opcode (${opcode}) not matched, expect one of: "+
        List( 
          OpCode.INVOKEVIRTUAL.code, 
          OpCode.INVOKESPECIAL.code, 
          OpCode.INVOKESTATIC.code, 
          OpCode.INVOKEINTERFACE.code, 
        )
      ) +: body

  /**
   * Visits an invokedynamic instruction.
   *
   * @param name the method's name.
   * @param descriptor the method's descriptor (see {@link Type}).
   * @param bootstrapMethodHandle the bootstrap method.
   * @param bootstrapMethodArguments the bootstrap method constant arguments. Each argument must be
   *     an {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link
   *     Type}, {@link Handle} or {@link ConstantDynamic} value. This method is allowed to modify
   *     the content of the array so a caller should expect that this array may change.
   */
  override def visitInvokeDynamicInsn(name:String, descriptor:String, bootstrapMethodHandle:Handle, bootstrapMethodArguments:AnyRef*):Unit = 
    import FirstErr.firstErr
    import bm._
    val bmArgs:Seq[Either[String,bm.BootstrapArg]] = bootstrapMethodArguments.map { bm0 => 
      bm.BootstrapArg(bm0)
    }
    val bmHdl = bm.Handle.unsafe(bootstrapMethodHandle)
    
    val e = for {
      args <- firstErr(bmArgs)      
    } yield MInvokeDynamicInsn(name,MDesc.unsafe(descriptor),bmHdl,args)

    body = e +: body

  /**
   * Visits a jump instruction. A jump instruction is an instruction that may jump to another
   * instruction.
   *
   * @param opcode the opcode of the type instruction to be visited. This opcode is either IFEQ,
   *     IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT,
   *     IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
   * @param label the operand of the instruction to be visited. This operand is a label that
   *     designates the instruction to which the jump instruction may jump.
   */
  override def visitJumpInsn(opcode:Int, label:Label):Unit = 
    body = Right(MJumpInsn(OpCode.find(opcode).get, label.toString)) +: body

  /**
   * Visits a label. A label designates the instruction that will be visited just after it.
   *
   * @param label a {@link Label} object.
   */
  override def visitLabel(label:Label):Unit = 
    body = Right(MLabel(label.toString)) +: body

  /**
   * Visits a LDC instruction. Note that new constant types may be added in future versions of the
   * Java Virtual Machine. To easily detect new constant types, implementations of this method
   * should check for unexpected constant types, like this:
   *
   * <pre>
   * if (cst instanceof Integer) {
   *     // ...
   * } else if (cst instanceof Float) {
   *     // ...
   * } else if (cst instanceof Long) {
   *     // ...
   * } else if (cst instanceof Double) {
   *     // ...
   * } else if (cst instanceof String) {
   *     // ...
   * } else if (cst instanceof org.objectweb.asm.Type) {
   *     int sort = ((org.objectweb.asm.Type) cst).getSort();
   *     if (sort == org.objectweb.asm.Type.OBJECT) {
   *         // ...
   *     } else if (sort == org.objectweb.asm.Type.ARRAY) {
   *         // ...
   *     } else if (sort == org.objectweb.asm.Type.METHOD) {
   *         // ...
   *     } else {
   *         // throw an exception
   *     }
   * } else if (cst instanceof Handle) {
   *     // ...
   * } else if (cst instanceof ConstantDynamic) {
   *     // ...
   * } else {
   *     // throw an exception
   * }
   * </pre>
   *
   * @param value the constant to be loaded on the stack. This parameter must be a non null {@link
   *              Integer}, a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link
   *              org.objectweb.asm.Type} of OBJECT or ARRAY sort for {@code .class} constants, for classes whose version is
   *              49, a {@link org.objectweb.asm.Type} of METHOD sort for MethodType, a {@link Handle} for MethodHandle
   *              constants, for classes whose version is 51 or a {@link org.objectweb.asm.ConstantDynamic} for a constant
   *              dynamic for classes whose version is 55.
   */
  override def visitLdcInsn(value:AnyRef):Unit =
    body = MLdcInsn(value) +: body

  /**
   * Visits an IINC instruction.
   *
   * @param var index of the local variable to be incremented.
   * @param increment amount to increment the local variable by.
   */
  override def visitIincInsn(variable:Int, increment:Int):Unit = 
    body = Right(MIincInsn(Variable(variable),increment)) +: body

  /**
   * Visits a TABLESWITCH instruction.
   *
   * @param min the minimum key value.
   * @param max the maximum key value.
   * @param dflt beginning of the default handler block.
   * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
   *     handler block for the {@code min + i} key.
   */
  override def visitTableSwitchInsn(min:Int, max:Int, dflt:Label, labels:Array[? <: Label]):Unit = 
    body =
      Right(MTableSwitchInsn(
        min,
        max,
        LBL(dflt.toString),
        labels.map( l => if l!=null then Some(LBL(l.toString)) else None )
      )) +: body

  /**
   * Visits a LOOKUPSWITCH instruction.
   *
   * @param dflt beginning of the default handler block.
   * @param keys the values of the keys.
   * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
   *     handler block for the {@code keys[i]} key.
   */
  override def visitLookupSwitchInsn(dflt:Label, keys:Array[Int], labels:Array[Label]):Unit = 
    body =
      Right(MLookupSwitchInsn(
        LBL(dflt.toString),
        keys,
        labels.map(l => if l!=null then Some(LBL(l.toString)) else None )
      )) +: body

  /**
   * Visits a MULTIANEWARRAY instruction.
   *
   * @param descriptor an array type descriptor (see {@link Type}).
   * @param numDimensions the number of dimensions of the array to allocate.
   */
  override def visitMultiANewArrayInsn(descriptor:String, numDimensions:Int):Unit =
    body =
      Right(MMultiANewArrayInsn(
        TDesc.unsafe(descriptor),
        numDimensions
      )) +: body

  /**
   * Visits an annotation on an instruction. This method must be called just <i>after</i> the
   * annotated instruction. It can be called several times for the same instruction.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#INSTANCEOF}, {@link TypeReference#NEW}, {@link
   *     TypeReference#CONSTRUCTOR_REFERENCE}, {@link TypeReference#METHOD_REFERENCE}, {@link
   *     TypeReference#CAST}, {@link TypeReference#CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT}, {@link
   *     TypeReference#METHOD_INVOCATION_TYPE_ARGUMENT}, {@link
   *     TypeReference#CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or {@link
   *     TypeReference#METHOD_REFERENCE_TYPE_ARGUMENT}. See {@link TypeReference}.
   * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
   *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
   *     'typeRef' as a whole.
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitInsnAnnotation(typeRef:Int, typePath:TypePath, descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api, Some(abodyEt => {
      body =
        abodyEt.map { body => 
          MInsnAnnotation(
            MTypeInsnRef(typeRef),
            if typePath!=null then Some(typePath.toString) else None,
            TDesc.unsafe(descriptor),
            visible,
            body
          )
        } +: body
    }))

  // -----------------------------------------------------------------------------------------------
  // Exceptions table entries, debug information, max stack and max locals
  // -----------------------------------------------------------------------------------------------

  /**
   * Visits a try catch block.
   *
   * @param start the beginning of the exception handler's scope (inclusive).
   * @param end the end of the exception handler's scope (exclusive).
   * @param handler the beginning of the exception handler's code.
   * @param type the internal name of the type of exceptions handled by the handler, or {@literal
   *     null} to catch any exceptions (for "finally" blocks).
   * @throws IllegalArgumentException if one of the labels has already been visited by this visitor
   *     (by the {@link #visitLabel} method).
   */
  override def visitTryCatchBlock(start:Label, end:Label, handler:Label, typeName:String):Unit = 
    body =
      Right(MTryCatchBlock(
        LBL(start.toString),
        LBL(end.toString),
        LBL(handler.toString),
        if typeName!=null then Some(JavaName.raw(typeName)) else None
      )) +: body

  /**
   * Visits an annotation on an exception handler type. This method must be called <i>after</i> the
   * {@link #visitTryCatchBlock} for the annotated exception handler. It can be called several times
   * for the same exception handler.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#EXCEPTION_PARAMETER}. See {@link TypeReference}.
   * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
   *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
   *     'typeRef' as a whole.
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitTryCatchAnnotation(typeRef:Int, typePath:TypePath, descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api, Some(abodyEt => {
      body =
        abodyEt.map { body => 
          MTryCatchAnnotation(
            MTypeTryCatchRef(typeRef),
            if typePath!=null then Some(typePath.toString) else None,
            TDesc.unsafe(descriptor),
            visible,
            body
          )
        } +: body
    }))

  /**
   * Visits a local variable declaration.
   *
   * @param name the name of a local variable.
   * @param descriptor the type descriptor of this local variable.
   * @param signature the type signature of this local variable. May be {@literal null} if the local
   *     variable type does not use generic types.
   * @param start the first instruction corresponding to the scope of this local variable
   *     (inclusive).
   * @param end the last instruction corresponding to the scope of this local variable (exclusive).
   * @param index the local variable's index.
   * @throws IllegalArgumentException if one of the labels has not already been visited by this
   *     visitor (by the {@link #visitLabel} method).
   */
  override def visitLocalVariable(name:String, descriptor:String, signature:String, start:Label, end:Label, index:Int):Unit = 
    body = 
      Right(MLocalVariable(
        name,
        TDesc.unsafe(descriptor),
        if signature!=null then Some(Sign(signature)) else None,
        start.toString,
        end.toString,
        index
      )) +: body

  /**
   * Visits an annotation on a local variable type.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#LOCAL_VARIABLE} or {@link TypeReference#RESOURCE_VARIABLE}. See {@link
   *     TypeReference}.
   * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
   *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
   *     'typeRef' as a whole.
   * @param start the fist instructions corresponding to the continuous ranges that make the scope
   *     of this local variable (inclusive).
   * @param end the last instructions corresponding to the continuous ranges that make the scope of
   *     this local variable (exclusive). This array must have the same size as the 'start' array.
   * @param index the local variable's index in each range. This array must have the same size as
   *     the 'start' array.
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitLocalVariableAnnotation(
    typeRef:Int, 
    typePath:TypePath, 
    start:Array[Label], 
    end:Array[Label], 
    index:Array[Int], 
    descriptor:String, 
    visible:Boolean
    ):AnnotationVisitor = 
    AnnotationDump(_api, Some(abodyEt => {
      body =
        abodyEt.map { body => 
          MLocalVariableAnnotation(
            MTypeLocalVarRef(typeRef),
            if typePath!=null then Some(typePath.toString) else None,
            start.map(l => LBL(l.toString)),
            end.map(l => LBL(l.toString)),
            index,
            TDesc.unsafe(descriptor),
            visible,
            body
          )
        } +: body
    }))

  /**
   * Visits a line number declaration.
   *
   * @param line a line number. This number refers to the source file from which the class was
   *     compiled.
   * @param start the first instruction corresponding to this line number.
   * @throws IllegalArgumentException if {@code start} has not already been visited by this visitor
   *     (by the {@link #visitLabel} method).
   */
  override def visitLineNumber(line:Int, start:Label):Unit =
    body = Right(MLineNumber(line,LBL(start.toString))) +: body

  /**
   * Visits the maximum stack size and the maximum number of local variables of the method.
   *
   * @param maxStack maximum stack size of the method.
   * @param maxLocals maximum number of local variables for the method.
   */
  override def visitMaxs(maxStack:Int, maxLocals:Int):Unit = 
    body = Right(MMaxs(maxStack,maxLocals)) +: body

  /**
   * Visits the end of the method. This method, which is the last one to be called, is used to
   * inform the visitor that all the annotations and attributes of the method have been visited.
   */
  override def visitEnd():Unit = 
    body = Right(MEnd()) +: body
    atEnd match
      case None => 
      case Some(call) => call(build)    

  def build:Either[String,Seq[MethCode]] = 
    import FirstErr.firstErr
    firstErr(body)
}
