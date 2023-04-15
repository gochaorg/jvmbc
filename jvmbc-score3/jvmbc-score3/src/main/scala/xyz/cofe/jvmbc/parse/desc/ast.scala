package xyz.cofe.jvmbc.parse.desc

import xyz.cofe.jvmbc.sparse.SPtr
import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.AST
import xyz.cofe.json4s3.derv.errors.DervError
import xyz.cofe.json4s3.derv.errors.TypeCastFail

/** AST дерево описания типа */
sealed trait Ast

/** Сигнатура (без generic) метода */
case class Method( parameters: List[FieldType], returns:Return ) extends Ast:
  lazy val raw:String =
    def arrayTypeRaw(at:ArrayType) =
      ( "[" * at.dimension ) + (
        at.component match
          case b:BaseType => b.letter.toString()
          case v:ObjectType => s"L${v.rawClassName};"
      )

    def retTypeRaw() =
      returns match
        case Void() => "V"
        case b:BaseType => b.letter.toString()
        case v:ObjectType => s"L${v.rawClassName};"
        case a:ArrayType => arrayTypeRaw(a)

    "(" + parameters.map(_.raw).mkString + ")" + retTypeRaw()

object Method {
  /** Парсинг сигнатуры */
  def parse(str:String):Either[String,Method] =
    DescParser.method.apply(SPtr(str,0)).map((a,b)=>a)

  /** Не безопасная попытка парсинга, если не успешно, то выкидывается Exception */
  def unsafe(str:String):Method = parse(str) match
    case Left(error)  => throw new Error(s"can't parse method descriptor ${error}")
    case Right(value) => value  
}

/** Тип возвращаемого значения */
sealed trait Return

/** Тип поля/параметра */
sealed trait FieldType extends Return with Ast:
  def raw:String =
    def arrayTypeRaw(at:ArrayType) =
      ( "[" * at.dimension ) + (
        at.component match
          case b:BaseType => b.letter.toString()
          case v:ObjectType => s"L${v.rawClassName};"
      )

    def filedTypeRaw(ft:FieldType) =
      ft match
        case b:BaseType => b.letter.toString()
        case v:ObjectType => s"L${v.rawClassName};"
        case a:ArrayType => arrayTypeRaw(a)

    filedTypeRaw(this)

object FieldType {
  def parse( raw:String ):Either[String,FieldType] =
    DescParser.fieldType.apply(SPtr(raw,0)).map((a,b)=>a)

  def unsafe( raw:String ):FieldType =
    parse(raw).getOrElse(throw new Error(s"can't parse as FieldType from $raw"))
}

/** Нет возвращаемого значения */
case class Void() extends Return with Ast

/** Примитивные типы */
sealed trait BaseType extends Ast with FieldType with ArrayComponent:
  def letter:Char

object BaseType:
  given FromJson[BaseType] with
    override def fromJson(j: AST): Either[DervError, BaseType] = 
      summon[FromJson[String]].fromJson(j).flatMap {
        case "byte"   => Right(ByteType)
        case "char"   => Right(CharType)
        case "double" => Right(DoubleType)
        case "float"  => Right(FloatType)
        case "int"    => Right(IntType)
        case "long"   => Right(LongType)
        case "short"  => Right(ShortType)
        case "bool"   => Right(BoolType)
        case s => Left(TypeCastFail(s"can't cast from '$s' to BaseType"))
      }
      
  given ToJson[BaseType] with
    override def toJson(t: BaseType): Option[AST] = 
      t match
        case ByteType =>   Some(AST.JsStr("byte"))
        case CharType =>   Some(AST.JsStr("char"))
        case DoubleType => Some(AST.JsStr("double"))
        case FloatType =>  Some(AST.JsStr("float"))
        case IntType =>    Some(AST.JsStr("int"))
        case LongType =>   Some(AST.JsStr("long"))
        case ShortType =>  Some(AST.JsStr("short"))
        case BoolType =>   Some(AST.JsStr("bool"))

case object ByteType extends BaseType:
  def letter: Char = 'B'

case object CharType extends BaseType:
  def letter: Char = 'C'

case object DoubleType extends BaseType:
  def letter: Char = 'D'

case object FloatType extends BaseType:
  def letter: Char = 'F'

case object IntType extends BaseType:
  def letter: Char = 'I'

case object LongType extends BaseType:
  def letter: Char = 'J'

case object ShortType extends BaseType:
  def letter: Char = 'S'

case object BoolType extends BaseType:
  def letter: Char = 'Z'

/** 
 * Объектный тип (возможно класс или интерфейс)
 * 
 * Имя класса представленное в байт-коде, например: java/lang/String
 */
case class ObjectType(rawClassName:String) extends FieldType with ArrayComponent:
  /** имя класса включая пакет, имена разделены точкой (`java.lang.String`) */
  lazy val name:String = rawClassName.replace("/",".")

  /** имя класса */
  lazy val nameList:List[String] = rawClassName.split("\\.").toList

  /** имя класса без названия пакета */
  lazy val simpleName:String = nameList.last

  /** название пакета */
  lazy val packaje:List[String] = nameList.dropRight(1)

  /** Переименование */
  object rename {
    def simpleName(newSimpleName:String):ObjectType =
      new ObjectType( (ObjectType.this.packaje ++ List(newSimpleName)).mkString("/") )

    def packaje( pkg:List[String] ):ObjectType =
      new ObjectType( (pkg ++ List(ObjectType.this.simpleName)).mkString("/"))

    def apply( javaNameString:String ):ObjectType = 
      new ObjectType( javaNameString.replace(".","/") )
  }

object ObjectType:
  def java(name:String): ObjectType = new ObjectType(name.replace(".","/"))
  def raw(rawName:String): ObjectType = new ObjectType(rawName)
  def unapply( jn:ObjectType ):Option[String] = Some(jn.rawClassName)

/** Тип-массив */
sealed trait ArrayComponent

case class ArrayType(dimension:Int, component:ArrayComponent) extends FieldType