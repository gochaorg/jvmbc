package xyz.cofe.jvmbc.io

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.TypePath
import xyz.cofe.jvmbc.cls._
import xyz.cofe.jvmbc.ann.AnnCode
import xyz.cofe.jvmbc.mdl
import xyz.cofe.jvmbc.fld
import xyz.cofe.jvmbc.mth
import xyz.cofe.jvmbc.rec
import xyz.cofe.jvmbc.mth.MethCode
import xyz.cofe.jvmbc.io.MthOutCtx

/**
  * Генерация байт-кода класса
  */
trait COut[V]:
  def write(out:ClassVisitor, v:V):Unit

object COut:
  given COut[CAnnotation] with
    def write(out: ClassVisitor, v: CAnnotation): Unit = 
      val annOut = out.visitAnnotation(v.desc.raw, v.visible)
      summon[AnnOut[Seq[AnnCode]]].write(annOut, v.annotations)

  given COut[CTypeAnnotation] with
    def write(out: ClassVisitor, v: CTypeAnnotation): Unit = 
      val annOut = out.visitTypeAnnotation(
        v.typeRef.raw,
        v.typePath.map { v => TypePath.fromString(v) }.orNull,
        v.desc.raw,
        v.visible
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, v.annotations)

  given COut[CSource] with
    def write(out: ClassVisitor, v: CSource): Unit = 
      out.visitSource(v.source.orNull, v.debug.orNull)

  given COut[CModule] with
    def write(out: ClassVisitor, v: CModule): Unit = 
      val vOut = out.visitModule(
        v.name,
        v.access.raw,
        v.version.orNull
      )
      summon[MdlOut[mdl.Modulo]].write(vOut, v.body)

  given COut[CPermittedSubclass] with
    def write(out: ClassVisitor, v: CPermittedSubclass): Unit = 
      out.visitPermittedSubclass(v.permittedSubclass)

  given COut[COuterClass] with
    def write(out: ClassVisitor, v: COuterClass): Unit = 
      out.visitOuterClass(v.owner, v.name.orNull, v.desc.map(_.raw).orNull )

  given COut[CNestMember] with
    def write(out: ClassVisitor, v: CNestMember): Unit = 
      out.visitNestMember(v.nestMember)

  given COut[CNestHost] with
    def write(out: ClassVisitor, v: CNestHost): Unit = 
      out.visitNestHost(v.nestHost)

  given COut[CField] with
    def write(out: ClassVisitor, v: CField): Unit = 
      val fldOut = out.visitField(v.access, v.name, v.desc.raw, 
        v.sign.map(_.raw).orNull, 
        v.value.orNull)
      summon[FldOut[Seq[fld.FieldCode]]].write(fldOut, v.body)

  given COut[CMethod] with
    def write(out: ClassVisitor, v: CMethod): Unit = 
      val mthOut = out.visitMethod(
        v.access.raw,
        v.name,
        v.desc.raw,
        v.sign.map(_.raw).orNull,
        v.exceptions.toArray,
      )
      implicit val mthCtx = MthOutCtx.newCtx
      summon[MthOut[Seq[MethCode]]].write(mthOut, v.body)

  given COut[CInnerClass] with
    def write(out: ClassVisitor, v: CInnerClass): Unit = 
      out.visitInnerClass(v.name, v.outerName.orNull, v.innerName.orNull, v.access.raw)

  given COut[CRecordComponent] with
    def write(out: ClassVisitor, v: CRecordComponent): Unit = 
      val recOut = out.visitRecordComponent(
        v.name,
        v.desc.raw,
        v.sign.map(_.raw).orNull
      )
      summon[RecOut[Seq[rec.RecordCode]]].write(recOut, v.body)

  extension [V:COut](v:V)
    def write(out: ClassVisitor):Unit =
      summon[COut[V]].write(out,v)

  extension [V:COut](v:Option[V])
    def write(out: ClassVisitor):Unit =
      v.foreach(v => summon[COut[V]].write(out,v))

  extension [V:COut](v:Seq[V])
    def write(out: ClassVisitor):Unit =
      v.foreach(v => summon[COut[V]].write(out,v))

  given COut[CBegin] with
    def write(out: ClassVisitor, v: CBegin): Unit = 
      out.visit(
        v.version.raw,
        v.access.raw,
        v.name.raw,
        v.sign.map(_.raw).orNull,
        v.superName.map(_.raw).orNull,
        v.interfaces.toArray
      )
      v.source.write(out)
      v.module.write(out)
      v.nestHost.write(out)
      v.outerClass.write(out)
      v.annotations.write(out)
      v.typeAnnotations.write(out)      
      v.nestMembers.write(out) // todo order ?
      v.permittedSubClasses.write(out) // todo order ?
      v.innerClasses.write(out)
      v.recordComponents.write(out)
      v.fields.write(out)
      v.methods.write(out)
      out.visitEnd()

  given COut[ClassCode] with
    def write(out: ClassVisitor, v: ClassCode): Unit = 
      v match
        case c:CAnnotation =>  summon[COut[CAnnotation]].write(out,c)
        case c:CTypeAnnotation => summon[COut[CTypeAnnotation]].write(out,c)
        case c:CSource => summon[COut[CSource]].write(out,c)
        case c:CModule => summon[COut[CModule]].write(out,c)
        case c:CPermittedSubclass => summon[COut[CPermittedSubclass]].write(out,c)
        case c:COuterClass => summon[COut[COuterClass]].write(out,c)
        case c:CNestMember => summon[COut[CNestMember]].write(out,c)
        case c:CNestHost => summon[COut[CNestHost]].write(out,c)
        case c:CField => summon[COut[CField]].write(out,c)
        case c:CMethod => summon[COut[CMethod]].write(out,c)
        case c:CInnerClass => summon[COut[CInnerClass]].write(out,c)
        case c:CRecordComponent => summon[COut[CRecordComponent]].write(out,c)
        case c:CBegin => summon[COut[CBegin]].write(out,c)
        case c:CEnd => ()
      