package xyz.cofe.jvmbc.io

import org.objectweb.asm.AnnotationVisitor

trait AnnOut[V]:
  def write(out:AnnotationVisitor, v:V):Unit

object AnnOut:
  import xyz.cofe.jvmbc.ann._

  given AnnOut[Undef] with { def write(out:AnnotationVisitor, v:Undef):Unit = out.visit(v.n.orNull, v.v) }
  given pair[T,P <: APair[T]]:AnnOut[P] with { 
    def write(out: AnnotationVisitor, v: P): Unit = 
      out.visit(v.name.orNull, v.value)
  }
  given AnnOut[AEnum] with
    def write(out: AnnotationVisitor, v: AEnum): Unit = out.visitEnum(v.name, v.desc.raw, v.value)
  given AnnOut[AEnd] with
    def write(out: AnnotationVisitor, v: AEnd): Unit = out.visitEnd()
  given AnnOut[EmANameDesc] with
    def write(out: AnnotationVisitor, v: EmANameDesc): Unit = 
      val visiter = out.visitAnnotation(v.name, v.desc.raw)
      v.annotations.foreach { c => summon[AnnOut[AnnCode]].write(out, c) }
  given AnnOut[EmAArray] with
    def write(out: AnnotationVisitor, v: EmAArray): Unit = 
      out.visitArray(v.name)
      v.annotations.foreach { c => summon[AnnOut[AnnCode]].write(out, c) }
  given AnnOut[AnnCode] with
    def write(out: AnnotationVisitor, v: AnnCode): Unit = 
      v match
        case a:AEnum           => summon[AnnOut[AEnum]].write(out,a)
        case a:EmAArray        => summon[AnnOut[EmAArray]].write(out,a)
        case a:EmANameDesc     => summon[AnnOut[EmANameDesc]].write(out,a)
        case a@AEnd()          => summon[AnnOut[AEnd]].write(out,a)
        case a@Undef(n, v)     => summon[AnnOut[Undef]].write(out,a)
        case a@Str(n, v)       => summon[AnnOut[Str]].write(out,a)
        case a@BoolV(n, v)     => summon[AnnOut[BoolV]].write(out,a)
        case a@ByteV(n, v)     => summon[AnnOut[ByteV]].write(out,a)
        case a@CharV(n, v)     => summon[AnnOut[CharV]].write(out,a)
        case a@ShortV(n, v)    => summon[AnnOut[ShortV]].write(out,a)
        case a@IntV(n, v)      => summon[AnnOut[IntV]].write(out,a)
        case a@LongV(n, v)     => summon[AnnOut[LongV]].write(out,a)
        case a@FloatV(n, v)    => summon[AnnOut[FloatV]].write(out,a)
        case a@DoubleV(n, v)   => summon[AnnOut[DoubleV]].write(out,a)
        case a@StrArr(n, v)    => summon[AnnOut[StrArr]].write(out,a)
        case a@BoolArr(n, v)   => summon[AnnOut[BoolArr]].write(out,a)
        case a@ByteArr(n, v)   => summon[AnnOut[ByteArr]].write(out,a)
        case a@CharArr(n, v)   => summon[AnnOut[CharArr]].write(out,a)
        case a@ShortArr(n, v)  => summon[AnnOut[ShortArr]].write(out,a)
        case a@IntArr(n, v)    => summon[AnnOut[IntArr]].write(out,a)
        case a@LongArr(n, v)   => summon[AnnOut[LongArr]].write(out,a)
        case a@FloatArr(n, v)  => summon[AnnOut[FloatArr]].write(out,a)
        case a@DoubleArr(n, v) => summon[AnnOut[DoubleArr]].write(out,a)
      