package xyz.cofe.jvmbc
package ann

sealed trait AnnCode extends ByteCode

sealed abstract class APair[V](val name:Option[String],val value:V) extends AnnCode
case class Undef(n:Option[String],v:Serializable) extends APair(n,v)
case class Str(n:Option[String],v:String) extends APair(n,v)
case class BoolV(n:Option[String],v:Boolean) extends APair(n,v)
case class ByteV(n:Option[String],v:Byte) extends APair(n,v)
case class CharV(n:Option[String],v:Char) extends APair(n,v)
case class ShortV(n:Option[String],v:Short) extends APair(n,v)
case class IntV(n:Option[String],v:Int) extends APair(n,v)
case class LongV(n:Option[String],v:Long) extends APair(n,v)
case class FloatV(n:Option[String],v:Float) extends APair(n,v)
case class DoubleV(n:Option[String],v:Double) extends APair(n,v)

case class StrArr(n:Option[String],v:Array[String]) extends APair(n,v)
case class BoolArr(n:Option[String],v:Array[Boolean]) extends APair(n,v)
case class ByteArr(n:Option[String],v:Array[Byte]) extends APair(n,v)
case class CharArr(n:Option[String],v:Array[Char]) extends APair(n,v)
case class ShortArr(n:Option[String],v:Array[Short]) extends APair(n,v)
case class IntArr(n:Option[String],v:Array[Int]) extends APair(n,v)
case class LongArr(n:Option[String],v:Array[Long]) extends APair(n,v)
case class FloatArr(n:Option[String],v:Array[Float]) extends APair(n,v)
case class DoubleArr(n:Option[String],v:Array[Double]) extends APair(n,v)

object APair:
  def apply(value:AnyRef)=apply0(null,value)
  def apply(name:String,value:AnyRef)=
    require(name!=null)
    apply0(name,value)

  private def apply0(name:String,value:AnyRef):Either[String,APair[_]]=
    require(value!=null)
    val name0 = if name==null then None else Some(name)
    value match
      case _:Float => Right(FloatV(name0,value.asInstanceOf[Float]))
      case _:Double => Right(DoubleV(name0,value.asInstanceOf[Double]))
      case _:Byte => Right(ByteV(name0,value.asInstanceOf[Byte]))
      case _:Short => Right(ShortV(name0,value.asInstanceOf[Short]))
      case _:Int => Right(IntV(name0,value.asInstanceOf[Int]))
      case _:Long => Right(LongV(name0,value.asInstanceOf[Long]))
      case _:Boolean => Right(BoolV(name0,value.asInstanceOf[Boolean]))
      case _:Char => Right(CharV(name0,value.asInstanceOf[Char]))
      case _:String  => Right(Str(name0,value.asInstanceOf[String]))
      case _ if value.getClass.isArray => 
        val valueCls = value.getClass
          valueCls.getName match
            case "[Z" => Right(BoolArr(name0,value.asInstanceOf[Array[Boolean]]))
            case "[C" => Right(CharArr(name0,value.asInstanceOf[Array[Char]]))
            case "[B" => Right(ByteArr(name0,value.asInstanceOf[Array[Byte]]))
            case "[S" => Right(ShortArr(name0,value.asInstanceOf[Array[Short]]))
            case "[I" => Right(IntArr(name0,value.asInstanceOf[Array[Int]]))
            case "[F" => Right(FloatArr(name0,value.asInstanceOf[Array[Float]]))
            case "[J" => Right(LongArr(name0,value.asInstanceOf[Array[Long]]))
            case "[D" => Right(DoubleArr(name0,value.asInstanceOf[Array[Double]]))
            case "[Ljava/lang/String;" | "[Ljava.lang.String;" => Right(StrArr(name0,value.asInstanceOf[Array[String]]))
            case _ => Left(s"not implemented for value=$value : ${value.getClass}")
      case _:Serializable => Right(Undef(name0,value.asInstanceOf[String]))
      case _ => Left(s"not implemented for value=$value : ${value.getClass}")

case class AEnum(name:String,desc:TDesc,value:String) extends AnnCode

case class EmAArray(name:String,annotations:Seq[AnnCode]) 
  extends AnnCode 
  with NestedThey("annotations")

case class EmANameDesc(name:String,desc:TDesc,annotations:Seq[AnnCode]) 
  extends AnnCode 
  with NestedThey("annotations")

case class AEnd() extends AnnCode
