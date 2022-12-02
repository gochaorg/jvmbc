package xyz.cofe.jvmbc.io

import org.objectweb.asm.RecordComponentVisitor
import xyz.cofe.jvmbc.rec._
import xyz.cofe.jvmbc.ann.AnnCode

trait RecOut[V]:
  def write(out:RecordComponentVisitor, v:V):Unit

object RecOut:
  given RecOut[RecAnnotation] with
    def write(out: RecordComponentVisitor, v: RecAnnotation): Unit = 
      val annOut = out.visitAnnotation(v.desc.raw, v.visible)
      summon[AnnOut[Seq[AnnCode]]].write(annOut, v.annotations)

  given RecOut[RecTypeAnnotation] with
    def write(out: RecordComponentVisitor, v: RecTypeAnnotation): Unit = 
      val annOut = out.visitTypeAnnotation(
        v.typeRef.raw,
        v.typePath.map { str => org.objectweb.asm.TypePath.fromString(str) }.orNull,
        v.desc.raw,
        v.visible
      )
      summon[AnnOut[Seq[AnnCode]]].write(annOut, v.annotations)

  given RecOut[RecEnd] with
    def write(out: RecordComponentVisitor, v: RecEnd): Unit = 
      out.visitEnd()

  given RecOut[RecordCode] with
    def write(out: RecordComponentVisitor, v: RecordCode): Unit = 
      v match
        case r:RecAnnotation => summon[RecOut[RecAnnotation]].write(out,r)
        case r:RecTypeAnnotation => summon[RecOut[RecTypeAnnotation]].write(out,r)
        case r:RecEnd => summon[RecOut[RecEnd]].write(out,r)
      