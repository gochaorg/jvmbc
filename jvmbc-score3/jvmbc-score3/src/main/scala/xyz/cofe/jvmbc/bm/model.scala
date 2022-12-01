package xyz.cofe.jvmbc
package bm

sealed trait BootstrapArg:
  def toAsm:Object

case class Handle(tag:Int,desc:TDesc,name:String,owner:String,iface:Boolean) extends BootstrapArg:
  override def toAsm: org.objectweb.asm.Handle = org.objectweb.asm.Handle(tag, owner, name, desc.raw, iface)

object Handle:
  def apply( h:org.objectweb.asm.Handle ):Handle =
    Handle(h.getTag, TDesc(h.getDesc), h.getName, h.getOwner, h.isInterface)

case class TypeArg(value:String) extends BootstrapArg:
  override def toAsm: Object = org.objectweb.asm.Type.getType(value)

case class StringArg(value:String) extends BootstrapArg:
  override def toAsm: Object = value

case class LongArg(value:Long) extends BootstrapArg:
  override def toAsm: Object = java.lang.Long.valueOf(value)

case class IntArg(value:Int) extends BootstrapArg:
  override def toAsm: Object = java.lang.Integer.valueOf(value)

case class FloatArg(value:Float) extends BootstrapArg:
  override def toAsm: Object = java.lang.Float.valueOf(value)

case class DoubleArg(value:Double) extends BootstrapArg:
  override def toAsm: Object = java.lang.Double.valueOf(value)

case class ConstDynamic(name:String,desc:String, handle: Handle, args:List[BootstrapArg]) extends BootstrapArg:
  override def toAsm: Object = new org.objectweb.asm.ConstantDynamic(
    name, desc, handle.toAsm, args.map(_.toAsm):_*
  )

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
