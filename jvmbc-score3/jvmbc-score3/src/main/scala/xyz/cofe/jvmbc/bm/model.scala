package xyz.cofe.jvmbc
package bm

sealed trait BootstrapArg

case class Handle(tag:Int,desc:TDesc,name:String,owner:String,iface:Boolean) extends BootstrapArg
object Handle:
  def apply( h:org.objectweb.asm.Handle ):Handle =
    Handle(h.getTag, TDesc(h.getDesc), h.getName, h.getOwner, h.isInterface)

case class TypeArg(value:String) extends BootstrapArg
case class StringArg(value:String) extends BootstrapArg
case class LongArg(value:Long) extends BootstrapArg
case class IntArg(value:Int) extends BootstrapArg
case class FloatArg(value:Float) extends BootstrapArg
case class DoubleArg(value:Double) extends BootstrapArg
case class ConstDynamic(name:String,desc:String, handle: Handle, args:List[BootstrapArg]) extends BootstrapArg
object ConstDynamic:
  def apply(a:org.objectweb.asm.ConstantDynamic):Either[String,ConstDynamic] =
    (0 until a.getBootstrapMethodArgumentCount)
      .map(idx => BootstrapArg(a.getBootstrapMethodArgument(idx)))
      .toList
      .foldLeft(
        Right(List()): Either[String, List[BootstrapArg]]
      ) { case (lst_e, itm) =>
        lst_e.flatMap(lst => itm.map(arg => lst :+ arg))
      }.map( lst => new ConstDynamic(a.getName, a.getDescriptor, Handle(a.getBootstrapMethod),lst)
    )


enum LdcType:
  case INT, FLOAT, LONG, DOUBLE, STRING, OBJECT, ARRAY, METHOD, HANDLE, CONST_DYNAMIC

object BootstrapArg:
  def apply(arg:AnyRef):Either[String,BootstrapArg] = arg match
    case a: Int => Right(IntArg(a))
    case a: Float => Right(FloatArg(a))
    case a: Long => Right(LongArg(a))
    case a: Double => Right(DoubleArg(a))
    case a: String => Right(StringArg(a))
    case a: org.objectweb.asm.Type => Right(TypeArg(a.toString))
    case a: org.objectweb.asm.Handle => Right(bm.Handle(a))
    case a: org.objectweb.asm.ConstantDynamic => bm.ConstDynamic(a)
    case a: AnyRef => Left(s"(visitInvokeDynamicInsn) unsupported bootstrap method arg ${a} : ${if a != null then a.getClass else null}")
