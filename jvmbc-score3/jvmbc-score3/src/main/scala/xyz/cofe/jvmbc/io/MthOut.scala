package xyz.cofe.jvmbc.io

import org.objectweb.asm.MethodVisitor
import xyz.cofe.jvmbc.mth._
import xyz.cofe.jvmbc.ann.AnnCode

trait MthOutCtx:
  def label(name:String):org.objectweb.asm.Label

trait MthOut[V]:
  def write(out: MethodVisitor, code:V)(using ctx:MthOutCtx):Unit

object MthOut:
  given MthOut[MCode] with
    def write(out: MethodVisitor, code: MCode)(using ctx: MthOutCtx): Unit = 
      out.visitCode()

  given MthOut[MEnd] with
    def write(out: MethodVisitor, code: MEnd)(using ctx: MthOutCtx): Unit = 
      out.visitEnd()

  given MthOut[MInst] with
    def write(out: MethodVisitor, code: MInst)(using ctx: MthOutCtx): Unit = 
      out.visitInsn(code.op.code)

  given MthOut[MAnnotableParameterCount] with
    def write(out: MethodVisitor, code: MAnnotableParameterCount)(using ctx: MthOutCtx): Unit = 
      out.visitAnnotableParameterCount(code.parameterCount, code.visible)

  given MthOut[MAnnotation] with
    def write(out: MethodVisitor, code: MAnnotation)(using ctx: MthOutCtx): Unit = 
      val annVisit = out.visitAnnotation(code.desc.raw, code.visible)
      summon[AnnOut[Seq[AnnCode]]].write(annVisit, code.annotations)

  given MthOut[MAnnotationDefault] with
    def write(out: MethodVisitor, code: MAnnotationDefault)(using ctx: MthOutCtx): Unit = 
      val annOut = out.visitAnnotationDefault()
      summon[AnnOut[Seq[AnnCode]]].write(annOut, code.annotations)

  given MthOut[MFieldInsn] with
    def write(out: MethodVisitor, code: MFieldInsn)(using ctx: MthOutCtx): Unit = 
      out.visitFieldInsn(
        code.op.code,
        code.owner,
        code.name,
        code.desc.raw
      )

  given MthOut[MFrame] with
    def write(out: MethodVisitor, code: MFrame)(using ctx: MthOutCtx): Unit = 
      out.visitFrame(
        code.kind.value,
        code.numLocal,
        code.local.toArray,
        code.numStack,
        code.stack.toArray
      )

  given MthOut[MIincInsn] with
    def write(out: MethodVisitor, code: MIincInsn)(using ctx: MthOutCtx): Unit = 
      out.visitIincInsn(code.variable, code.inc)

  given MthOut[MInsnAnnotation] with
    def write(out: MethodVisitor, code: MInsnAnnotation)(using ctx: MthOutCtx): Unit = 
      val annVis = out.visitInsnAnnotation(
        code.typeRef.raw,
        code.typePath.map(s => org.objectweb.asm.TypePath.fromString(s)).orNull,
        code.desc.raw,
        code.visible
      )
      summon[AnnOut[Seq[AnnCode]]].write(annVis,code.annotations)

  given MthOut[MIntInsn] with
    def write(out: MethodVisitor, code: MIntInsn)(using ctx: MthOutCtx): Unit = 
      out.visitIntInsn(
        code.op.code,
        code.operand
      )

  given MthOut[MInvokeDynamicInsn] with
    def write(out: MethodVisitor, code: MInvokeDynamicInsn)(using ctx: MthOutCtx): Unit = 
      out.visitInvokeDynamicInsn(
        code.name,
        code.desc.raw,
        code.bootstrapMethod.toAsm,
        code.args.map(_.toAsm):_*
      )

  given MthOut[MJumpInsn] with
    def write(out: MethodVisitor, code: MJumpInsn)(using ctx: MthOutCtx): Unit = 
      out.visitJumpInsn(
        code.op.code,
        ctx.label(code.label)
      )

  given MthOut[MLabel] with
    def write(out: MethodVisitor, code: MLabel)(using ctx: MthOutCtx): Unit = 
      out.visitLabel(ctx.label(code.name))

  given MthOut[MLdcInsn] with
    def write(out: MethodVisitor, code: MLdcInsn)(using ctx: MthOutCtx): Unit = 
      out.visitLdcInsn{ code.value match
        case LdcValue.INT(value) => Int.box(value)
        case LdcValue.FLOAT(value) => Float.box(value)
        case LdcValue.LONG(value) => Long.box(value)
        case LdcValue.DOUBLE(value) => Double.box(value)
        case LdcValue.STRING(value) => value
        case LdcValue.OBJ(value) => value.toAsm
        case LdcValue.ARR(value) => value.toAsm
        case LdcValue.METHOD(value) => value.toAsm
        case LdcValue.HANDLE(value) => value.toAsm
        case LdcValue.CONST_DYNAMIC(value) => value.toAsm
       }

  given MthOut[MLineNumber] with
    def write(out: MethodVisitor, code: MLineNumber)(using ctx: MthOutCtx): Unit = 
      out.visitLineNumber(code.line, ctx.label(code.label))

  given MthOut[MLocalVariable] with
    def write(out: MethodVisitor, code: MLocalVariable)(using ctx: MthOutCtx): Unit = 
      out.visitLocalVariable(
        code.name,
        code.desc.raw,
        code.sign.map(_.raw).orNull,
        ctx.label(code.labelStart),
        ctx.label(code.labelEnd),
        code.index
      )

  given MthOut[MLocalVariableAnnotation] with
    def write(out: MethodVisitor, code: MLocalVariableAnnotation)(using ctx: MthOutCtx): Unit = 
      val annOut = out.visitLocalVariableAnnotation(
        code.typeRef.raw,
        code.typePath.map(t=>org.objectweb.asm.TypePath.fromString(t)).orNull,
        code.startLabels.map(n =>
          if n!=null then ctx.label(n) else null
        ).toArray,
        code.endLabels.map(n =>
          if n!=null then ctx.label(n) else null
        ).toArray,
        code.index.toArray,
        code.desc.raw,
        code.visible        
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, code.annotations)

  given MthOut[MLookupSwitchInsn] with
    def write(out: MethodVisitor, code: MLookupSwitchInsn)(using ctx: MthOutCtx): Unit = 
      out.visitLookupSwitchInsn(
        ctx.label(code.defaultHandle),
        code.keys.toArray,
        code.labels.map(n=>ctx.label(n)).toArray
      )

  given MthOut[MMaxs] with
    def write(out: MethodVisitor, code: MMaxs)(using ctx: MthOutCtx): Unit = 
      out.visitMaxs(code.maxStack, code.maxLocal)

  given MthOut[MMethodInsn] with
    def write(out: MethodVisitor, code: MMethodInsn)(using ctx: MthOutCtx): Unit = 
      out.visitMethodInsn(
        code.op.code,
        code.owner,
        code.name,
        code.desc.raw,
        code.iface
      )

  given MthOut[MMultiANewArrayInsn] with
    def write(out: MethodVisitor, code: MMultiANewArrayInsn)(using ctx: MthOutCtx): Unit = 
      out.visitMultiANewArrayInsn(
        code.desc.raw,
        code.numDimensions
      )

  given MthOut[MParameter] with
    def write(out: MethodVisitor, code: MParameter)(using ctx: MthOutCtx): Unit = 
      out.visitParameter(code.name.orNull, code.access.raw)

  given MthOut[MParameterAnnotation] with
    def write(out: MethodVisitor, code: MParameterAnnotation)(using ctx: MthOutCtx): Unit = 
      val annOut = out.visitParameterAnnotation(
        code.param,
        code.desc.raw,
        code.visible
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, code.annotations)

  given MthOut[MTableSwitchInsn] with
    def write(out: MethodVisitor, code: MTableSwitchInsn)(using ctx: MthOutCtx): Unit = 
      out.visitTableSwitchInsn(
        code.min,
        code.max,
        ctx.label(code.defaultLabel),
        code.labels.map{n=>ctx.label(n)}.toArray:_*
      )

  given MthOut[MTryCatchAnnotation] with
    def write(out: MethodVisitor, code: MTryCatchAnnotation)(using ctx: MthOutCtx): Unit = 
      val annOut = out.visitTryCatchAnnotation(
        code.typeRef.raw,
        code.typePath.map { t => org.objectweb.asm.TypePath.fromString(t) }.orNull,
        code.desc.raw,
        code.visible
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, code.annotations)

  given MthOut[MTryCatchBlock] with
    def write(out: MethodVisitor, code: MTryCatchBlock)(using ctx: MthOutCtx): Unit = 
      out.visitTryCatchBlock(
        ctx.label(code.startLabel),
        ctx.label(code.endLabel),
        ctx.label(code.handlerLabel),
        code.typeName.orNull
      )

  given MthOut[MTypeAnnotation] with
    def write(out: MethodVisitor, code: MTypeAnnotation)(using ctx: MthOutCtx): Unit = 
      val annOut = out.visitTypeAnnotation(
        code.typeRef.raw,
        code.typePath.map { t => org.objectweb.asm.TypePath.fromString(t) }.orNull,
        code.desc.raw,
        code.visible
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, code.annotations)

  given MthOut[MTypeInsn] with
    def write(out: MethodVisitor, code: MTypeInsn)(using ctx: MthOutCtx): Unit = 
      out.visitTypeInsn(code.op.code, code.typeName)

  given MthOut[MVarInsn] with
    def write(out: MethodVisitor, code: MVarInsn)(using ctx: MthOutCtx): Unit = 
      out.visitVarInsn(
        code.op.code,
        code.variable
      )

  