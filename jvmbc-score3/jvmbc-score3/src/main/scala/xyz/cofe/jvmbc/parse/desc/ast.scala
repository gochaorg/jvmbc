package xyz.cofe.jvmbc.parse.desc

import xyz.cofe.jvmbc.sparse.SPtr

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

    def filedTypeRaw(ft:FieldType) =
      ft match
        case b:BaseType => b.letter.toString()
        case v:ObjectType => s"L${v.rawClassName};"
        case a:ArrayType => arrayTypeRaw(a)

    def retTypeRaw() =
      returns match
        case Void() => "V"
        case b:BaseType => b.letter.toString()
        case v:ObjectType => s"L${v.rawClassName};"
        case a:ArrayType => arrayTypeRaw(a)

    "(" + parameters.map(filedTypeRaw).mkString + ")" + retTypeRaw()

object Method {
  /** Парсинг сигнатуры */
  def parse(str:String):Either[String,Method] =
    DescParser.method.apply(SPtr(str,0)).map((a,b)=>a)
}

/** Тип возвращаемого значения */
sealed trait Return

/** Тип поля/параметра */
sealed trait FieldType extends Return with Ast

/** Нет возвращаемого значения */
case class Void() extends Return with Ast

/** Примитивные типы */
sealed trait BaseType extends Ast with FieldType with ArrayComponent:
  def letter:Char

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