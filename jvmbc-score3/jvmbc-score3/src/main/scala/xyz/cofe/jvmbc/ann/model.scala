package xyz.cofe.jvmbc
package ann

/**
  * Байт-код аннотаций
  */
sealed trait AnnCode extends ByteCode

enum APair extends AnnCode:
  case Undef(n:Option[String],v:Serializable) extends APair
  case Str(n:Option[String],v:String) extends APair
  case BoolV(n:Option[String],v:Boolean) extends APair
  case ByteV(n:Option[String],v:Byte) extends APair
  case CharV(n:Option[String],v:Char) extends APair
  case ShortV(n:Option[String],v:Short) extends APair
  case IntV(n:Option[String],v:Int) extends APair
  case LongV(n:Option[String],v:Long) extends APair
  case FloatV(n:Option[String],v:Float) extends APair
  case DoubleV(n:Option[String],v:Double) extends APair
  case StrArr(n:Option[String],v:Array[String]) extends APair
  case BoolArr(n:Option[String],v:Array[Boolean]) extends APair
  case ByteArr(n:Option[String],v:Array[Byte]) extends APair
  case CharArr(n:Option[String],v:Array[Char]) extends APair
  case ShortArr(n:Option[String],v:Array[Short]) extends APair
  case IntArr(n:Option[String],v:Array[Int]) extends APair
  case LongArr(n:Option[String],v:Array[Long]) extends APair
  case FloatArr(n:Option[String],v:Array[Float]) extends APair
  case DoubleArr(n:Option[String],v:Array[Double]) extends APair
  def name:Option[String] = this match
    case Undef(n, v) => n
    case Str(n, v) =>  n
    case BoolV(n, v) => n
    case ByteV(n, v) => n
    case CharV(n, v) => n
    case ShortV(n, v) => n
    case IntV(n, v) => n
    case LongV(n, v) => n
    case FloatV(n, v) => n
    case DoubleV(n, v) => n
    case StrArr(n, v) => n
    case BoolArr(n, v) => n
    case ByteArr(n, v) => n
    case CharArr(n, v) => n
    case ShortArr(n, v) => n
    case IntArr(n, v) => n
    case LongArr(n, v) => n
    case FloatArr(n, v) => n
    case DoubleArr(n, v) => n
  
  def value:Any = this match
    case Undef(n, v) =>  v
    case Str(n, v) => v
    case BoolV(n, v) => v
    case ByteV(n, v) => v
    case CharV(n, v) => v
    case ShortV(n, v) => v
    case IntV(n, v) => v
    case LongV(n, v) => v
    case FloatV(n, v) => v
    case DoubleV(n, v) => v
    case StrArr(n, v) => v
    case BoolArr(n, v) => v
    case ByteArr(n, v) => v
    case CharArr(n, v) => v
    case ShortArr(n, v) => v
    case IntArr(n, v) => v
    case LongArr(n, v) => v
    case FloatArr(n, v) => v
    case DoubleArr(n, v) => v
  

object APair:
  def apply(value:AnyRef): Either[String, APair] =apply0(null,value)
  def apply(name:String,value:AnyRef): Either[String, APair] =
    require(name!=null)
    apply0(name,value)

  private def apply0(name:String,value:AnyRef):Either[String,APair]=
    require(value!=null)
    val name0 = Option(name)
    value match
      case _:java.lang.Float => Right(FloatV(name0,value.asInstanceOf[java.lang.Float]))
      //case _:Float => Right(FloatV(name0,value.asInstanceOf[Float]))
      case _:java.lang.Double => Right(DoubleV(name0,value.asInstanceOf[java.lang.Double]))
      //case _:Double => Right(DoubleV(name0,value.asInstanceOf[Double]))
      case _:java.lang.Byte => Right(ByteV(name0,value.asInstanceOf[java.lang.Byte]))
      //case _:Byte => Right(ByteV(name0,value.asInstanceOf[Byte]))
      case _:java.lang.Short => Right(ShortV(name0,value.asInstanceOf[java.lang.Short]))
      //case _:Short => Right(ShortV(name0,value.asInstanceOf[Short]))
      case _:java.lang.Integer => Right(IntV(name0,value.asInstanceOf[java.lang.Integer]))
      //case _:Int => Right(IntV(name0,value.asInstanceOf[Int]))
      case _:java.lang.Long => Right(LongV(name0,value.asInstanceOf[java.lang.Long]))
      //case _:Long => Right(LongV(name0,value.asInstanceOf[Long]))
      case _:java.lang.Boolean => Right(BoolV(name0,value.asInstanceOf[java.lang.Boolean]))
      //case _:Boolean => Right(BoolV(name0,value.asInstanceOf[Boolean]))
      case _:java.lang.Character => Right(CharV(name0,value.asInstanceOf[java.lang.Character]))
      //case _:Char => Right(CharV(name0,value.asInstanceOf[Char]))
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
      case v:Serializable => Right(Undef(name0,v))
      case _ => Left(s"not implemented for value=$value : ${value.getClass}")

case class AEnum(name:String,desc:TDesc,value:String) extends AnnCode

case class EmAArray(name:String,annotations:Seq[AnnCode]) 
  extends AnnCode 
  with NestedThey("annotations")

case class EmANameDesc(name:String,desc:TDesc,annotations:Seq[AnnCode]) 
  extends AnnCode 
  with NestedThey("annotations")

case class AEnd() extends AnnCode
