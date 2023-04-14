package xyz.cofe.jvmbc
package bm

import xyz.cofe.jvmbc.parse.desc.{Method => MDesc}

/**
  * Байт-код аргументов INVOKE Dynamic
  */
sealed trait BootstrapArg:
  def toAsm:Object

case class Handle(tag:Int,desc:TDesc|MDesc,name:String,owner:String,iface:Boolean) extends BootstrapArg:
  override def toAsm: org.objectweb.asm.Handle = org.objectweb.asm.Handle(
    tag, 
    owner, 
    name, 
    {
      desc match
        case m:MDesc => m.raw
        case t:TDesc => t.raw
    },
    iface
  )

object Handle:
  def unsafe( h:org.objectweb.asm.Handle ):Handle =
    val desc1 : Either[String,TDesc|MDesc] = MDesc.parse(h.getDesc());
    val desc2 : Either[String,TDesc|MDesc] = TDesc.parse(h.getDesc());     
    val descEt : Either[String,TDesc|MDesc] = desc1.orElse( desc2 )
    val desc = descEt.getOrElse( throw new Error(s"can't parse Handle from ${h.getDesc()}") )

    Handle(h.getTag, desc, h.getName, h.getOwner, h.isInterface)

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
      }.map( lst => new ConstDynamic(a.getName, a.getDescriptor, Handle.unsafe(a.getBootstrapMethod),lst)
    )


object BootstrapArg:
  def apply(arg:AnyRef):Either[String,BootstrapArg] = arg match
    case a: java.lang.Integer => Right(IntArg(a))
    case a: java.lang.Float => Right(FloatArg(a))
    case a: java.lang.Long => Right(LongArg(a))
    case a: java.lang.Double => Right(DoubleArg(a))
    case a: String => Right(StringArg(a))
    case a: org.objectweb.asm.Type => Right(TypeArg(a.toString))
    case a: org.objectweb.asm.Handle => Right(bm.Handle.unsafe(a))
    case a: org.objectweb.asm.ConstantDynamic => bm.ConstDynamic(a)
    case a: AnyRef => Left(s"(visitInvokeDynamicInsn) unsupported bootstrap method arg ${a} : ${if a != null then a.getClass else null}")
