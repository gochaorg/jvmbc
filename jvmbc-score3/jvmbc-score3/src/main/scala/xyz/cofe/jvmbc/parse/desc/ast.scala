package xyz.cofe.jvmbc.parse.desc

/** AST дерево описания типа */
sealed trait Ast

/** Сигнатура (без generic) метода */
case class Method( parameters: List[FieldType], returns:Return ) extends Ast

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

/** Объектный тип (возможно класс или интерфейс) */
case class ObjectType(rawClassName:String) extends FieldType with ArrayComponent

/** Тип-массив */
sealed trait ArrayComponent

case class ArrayType(dimension:Int, component:ArrayComponent) extends FieldType