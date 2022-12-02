package xyz.cofe.jvmbc.io

import org.objectweb.asm.FieldVisitor
import xyz.cofe.jvmbc.fld._
import xyz.cofe.jvmbc.ann.AnnCode

trait FldOut[V]:
  def write(out:FieldVisitor, v:V):Unit

object FldOut:
  given FldOut[FAnnotation] with
    def write(out: FieldVisitor, v: FAnnotation): Unit = 
      val annOut = out.visitAnnotation(v.desc.raw, v.visible)
      summon[AnnOut[Seq[AnnCode]]].write(annOut, v.annotations)

  given FldOut[FieldEnd] with
    def write(out: FieldVisitor, v: FieldEnd): Unit = 
      out.visitEnd()

  given FldOut[FTypeAnnotation] with
    def write(out: FieldVisitor, v: FTypeAnnotation): Unit = 
      val annOut = out.visitTypeAnnotation(
        v.typeRef.raw,
        v.typePath.map { v => 
          org.objectweb.asm.TypePath.fromString(v)
        }.orNull,
        v.desc.raw,
        v.visible,
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, v.annotations)

  given FldOut[FieldCode] with
    def write(out: FieldVisitor, v: FieldCode): Unit = 
      v match
        case f:FAnnotation => summon[FldOut[FAnnotation]].write(out, f)
        case f:FieldEnd => summon[FldOut[FieldEnd]].write(out, f)
        case f:FTypeAnnotation => summon[FldOut[FTypeAnnotation]].write(out, f)

  given [V:FldOut]:FldOut[Seq[V]] with
    def write(out: FieldVisitor, v: Seq[V]): Unit = 
      v.foreach { v => summon[FldOut[V]].write(out,v) }
      