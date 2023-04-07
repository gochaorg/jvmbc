package xyz.cofe.jvmbc.io

import org.objectweb.asm.MethodVisitor
import xyz.cofe.jvmbc.mth._
import xyz.cofe.jvmbc.ann.AnnCode
import org.objectweb.asm.Label
import xyz.cofe.jvmbc.raw

/** Контекст генерации метода класса, используется для генерации/получения метки [[xyz.cofe.jvmbc.mth.MLabel]] */
trait MthOutCtx:
  def label(name:String):org.objectweb.asm.Label

object MthOutCtx:
  def newCtx:MthOutCtx = new MthOutCtx {
    var labels = Map[String,org.objectweb.asm.Label]()
    override def label(name: String): Label = 
      val lb0 = labels.get(name)
      if lb0.isEmpty then
        val lb1 = new org.objectweb.asm.Label()
        labels = labels + ( name -> lb1 )
        lb1
      else
        lb0.get
  }

/** Генерация байт-кода метода класса */
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
        code.kind.raw,
        code.numLocal,
        code.local.map(el => el.map(_.value).orNull).toArray,
        code.numStack,
        code.stack.map(el => el.map(_.value).orNull).toArray
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

  given MthOut[MethCode] with
    def write(out: MethodVisitor, code: MethCode)(using ctx: MthOutCtx): Unit = 
      code match
        case c:MCode => summon[MthOut[MCode]].write(out,c)
        case c:MEnd => summon[MthOut[MEnd]].write(out,c)
        case c:MInst =>  summon[MthOut[MInst]].write(out,c)
        case c:MAnnotableParameterCount =>  summon[MthOut[MAnnotableParameterCount]].write(out,c)
        case c:MAnnotation =>  summon[MthOut[MAnnotation]].write(out,c)
        case c:MAnnotationDefault =>  summon[MthOut[MAnnotationDefault]].write(out,c)
        case c:MFieldInsn =>  summon[MthOut[MFieldInsn]].write(out,c)
        case c:MFrame =>  summon[MthOut[MFrame]].write(out,c)
        case c:MIincInsn =>  summon[MthOut[MIincInsn]].write(out,c)
        case c:MInsnAnnotation =>  summon[MthOut[MInsnAnnotation]].write(out,c)
        case c:MIntInsn =>  summon[MthOut[MIntInsn]].write(out,c)
        case c:MInvokeDynamicInsn =>  summon[MthOut[MInvokeDynamicInsn]].write(out,c)
        case c:MJumpInsn =>  summon[MthOut[MJumpInsn]].write(out,c)
        case c:MLabel =>  summon[MthOut[MLabel]].write(out,c)
        case c:MLdcInsn =>  summon[MthOut[MLdcInsn]].write(out,c)
        case c:MLineNumber =>  summon[MthOut[MLineNumber]].write(out,c)
        case c:MLocalVariable =>  summon[MthOut[MLocalVariable]].write(out,c)
        case c:MLocalVariableAnnotation =>  summon[MthOut[MLocalVariableAnnotation]].write(out,c)
        case c:MLookupSwitchInsn =>  summon[MthOut[MLookupSwitchInsn]].write(out,c)
        case c:MMaxs =>  summon[MthOut[MMaxs]].write(out,c)
        case c:MMethodInsn =>  summon[MthOut[MMethodInsn]].write(out,c)
        case c:MMultiANewArrayInsn =>  summon[MthOut[MMultiANewArrayInsn]].write(out,c)
        case c:MParameter =>  summon[MthOut[MParameter]].write(out,c)
        case c:MParameterAnnotation =>  summon[MthOut[MParameterAnnotation]].write(out,c)
        case c:MTableSwitchInsn =>  summon[MthOut[MTableSwitchInsn]].write(out,c)
        case c:MTryCatchAnnotation =>  summon[MthOut[MTryCatchAnnotation]].write(out,c)
        case c:MTryCatchBlock =>  summon[MthOut[MTryCatchBlock]].write(out,c)
        case c:MTypeAnnotation =>  summon[MthOut[MTypeAnnotation]].write(out,c)
        case c:MTypeInsn =>  summon[MthOut[MTypeInsn]].write(out,c)
        case c:MVarInsn =>  summon[MthOut[MVarInsn]].write(out,c)

  given [V:MthOut]:MthOut[Seq[V]] with
    def write(out: MethodVisitor, code: Seq[V])(using ctx: MthOutCtx): Unit = 
      code.foreach { c => summon[MthOut[V]].write(out,c) }
      