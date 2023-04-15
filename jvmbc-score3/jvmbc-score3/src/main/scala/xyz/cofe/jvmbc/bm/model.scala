package xyz.cofe.jvmbc
package bm

import xyz.cofe.jvmbc.parse.desc.{Method => MDesc}
import xyz.cofe.jvmbc.parse.desc.{ObjectType => JavaName}

/**
  * Байт-код аргументов INVOKE Dynamic
  */
sealed trait BootstrapArg:
  def toAsm:Object

enum Handle extends BootstrapArg:
  case GetField ( owner:JavaName, name:String, desc:TDesc )
  case GetStatic( owner:JavaName, name:String, desc:TDesc )
  case PutField ( owner:JavaName, name:String, desc:TDesc )
  case PutStatic( owner:JavaName, name:String, desc:TDesc )
  case InvokeVirtual   ( owner:JavaName, name:String, desc:MDesc, iface:Boolean = false )
  case InvokeStatic    ( owner:JavaName, name:String, desc:MDesc, iface:Boolean = false )
  case InvokeSpecial   ( owner:JavaName, name:String, desc:MDesc, iface:Boolean = false )
  case InvokeNewSpecial( owner:JavaName, name:String, desc:MDesc, iface:Boolean = false )
  case InvokeInterface ( owner:JavaName, name:String, desc:MDesc, iface:Boolean = true )
  def toAsm: org.objectweb.asm.Handle =
    import org.objectweb.asm.Opcodes.*
    this match
      case GetField(owner, name, desc) =>                org.objectweb.asm.Handle(H_GETFIELD,         owner.rawClassName, name, desc.raw, false)
      case GetStatic(owner, name, desc) =>               org.objectweb.asm.Handle(H_GETSTATIC,        owner.rawClassName, name, desc.raw, false) 
      case PutField(owner, name, desc) =>                org.objectweb.asm.Handle(H_PUTFIELD,         owner.rawClassName, name, desc.raw, false) 
      case PutStatic(owner, name, desc) =>               org.objectweb.asm.Handle(H_PUTSTATIC,        owner.rawClassName, name, desc.raw, false) 
      case InvokeVirtual(owner, name, desc, iface) =>    org.objectweb.asm.Handle(H_INVOKEVIRTUAL,    owner.rawClassName, name, desc.raw, iface) 
      case InvokeStatic(owner, name, desc, iface) =>     org.objectweb.asm.Handle(H_INVOKESTATIC,     owner.rawClassName, name, desc.raw, iface) 
      case InvokeSpecial(owner, name, desc, iface) =>    org.objectweb.asm.Handle(H_INVOKESPECIAL,    owner.rawClassName, name, desc.raw, iface) 
      case InvokeNewSpecial(owner, name, desc, iface) => org.objectweb.asm.Handle(H_NEWINVOKESPECIAL, owner.rawClassName, name, desc.raw, iface) 
      case InvokeInterface(owner, name, desc, iface) =>  org.objectweb.asm.Handle(H_INVOKEINTERFACE,  owner.rawClassName, name, desc.raw, iface) 

object Handle:
  def parse( h:org.objectweb.asm.Handle ):Either[String,Handle] =
    import org.objectweb.asm.Opcodes.*

    val mdesc : Either[String,MDesc] = MDesc.parse(h.getDesc());
    val tdesc : Either[String,TDesc] = TDesc.parse(h.getDesc());     

    h.getTag() match
      case H_GETFIELD => tdesc.map( d => Handle.GetField( JavaName.raw(h.getOwner()), h.getName(), d ))
      case H_PUTFIELD => tdesc.map( d => Handle.PutField( JavaName.raw(h.getOwner()), h.getName(), d ))
      case H_GETSTATIC => tdesc.map( d => Handle.GetStatic( JavaName.raw(h.getOwner()), h.getName(), d ))
      case H_PUTSTATIC => tdesc.map( d => Handle.PutStatic( JavaName.raw(h.getOwner()), h.getName(), d ))
      case H_INVOKEVIRTUAL =>    mdesc.map( d => Handle.InvokeVirtual   (JavaName.raw(h.getOwner()), h.getName(), d))
      case H_INVOKESTATIC =>     mdesc.map( d => Handle.InvokeStatic    (JavaName.raw(h.getOwner()), h.getName(), d))
      case H_INVOKESPECIAL =>    mdesc.map( d => Handle.InvokeSpecial   (JavaName.raw(h.getOwner()), h.getName(), d))
      case H_NEWINVOKESPECIAL => mdesc.map( d => Handle.InvokeNewSpecial(JavaName.raw(h.getOwner()), h.getName(), d))
      case H_INVOKEINTERFACE =>  mdesc.map( d => Handle.InvokeInterface (JavaName.raw(h.getOwner()), h.getName(), d))
      case _ => Left(s"unexpected Handle.getTag() = ${h.getTag()}, expect="+List(
        H_GETFIELD, H_PUTFIELD, H_GETSTATIC, H_PUTSTATIC, H_INVOKEVIRTUAL, 
        H_INVOKESTATIC, H_INVOKESPECIAL, H_NEWINVOKESPECIAL, H_INVOKEINTERFACE
      ))

  def unsafe( h:org.objectweb.asm.Handle ):Handle = parse(h).getOrElse(throw new Error(s"can't parse org.objectweb.asm.Handle"))

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
