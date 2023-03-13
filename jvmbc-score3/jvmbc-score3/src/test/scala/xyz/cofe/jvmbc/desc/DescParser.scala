package xyz.cofe.jvmbc.desc

/**
  * Дескриптор
  * 
  * Описывает java тип либо сигнатуру (тип аргументов и результата) метода
  s*/
sealed trait Descriptor

/**
  * Тип который был до java 1.5 - до generic-ов
  */
sealed trait Java14Type extends Descriptor

enum Primitive(val rawName:String) extends Java14Type:
  case BOOL   extends Primitive("Z")
  case CHAR   extends Primitive("C")
  case BYTE   extends Primitive("B")
  case SHORT  extends Primitive("S")
  case INT    extends Primitive("I")
  case FLOAT  extends Primitive("F")
  case DOUBLE extends Primitive("D")
  case VOID   extends Primitive("V")

case class RefType(rawClassName:String) extends Java14Type

enum TypeDesc(val java14Type: Java14Type) extends Java14Type:
  case UnaryType(java14Type0: Java14Type) extends TypeDesc(java14Type0)
  case ArrayType(java14Type0: Java14Type, val dimension:Int) extends TypeDesc(java14Type0)

case class MethodDesc(arguments:TypeDesc, returns:TypeDesc) extends Descriptor

case class StrPtr( string:String, ptr:Int ):
  def hasTarget:Boolean = ptr>=0 && ptr<string.length()
  def offset(off:Int):StrPtr = copy(ptr=ptr+off)
  def rest:Int = if ptr>=0 then (string.length() - ptr) max 0 else string.length() - ptr
  def fetch(size:Int):String =
    if size<=0 then ""
    else
      if hasTarget then
        val lookupSize = rest min size
        string.substring(ptr, ptr+lookupSize)
      else
        ""
  def apply(str:String):StrPtrApplyStr = StrPtrApplyStr(this,str)
  def +(offsetValue:Int):StrPtr = offset(offsetValue)

class StrPtrApplyStr( ptr:StrPtr, str:String ):
  def equ:Boolean = ptr.fetch(str.length()) == str
  def hasChar:Boolean = 
    val s = ptr.fetch(1)
    if s.length()<1 then false
    else str.contains(s)

/**
  * grammar
  * 
  *     Description ::= MethodDesc | TypeDesc
  *     MethodDesc ::= '(' {TypeDesc} ')' TypeDesc
  *     TypeDesc ::= ArrayType | UnaryType 
  *     UnaryType ::= Primitive | RefType
  *     ArrayType ::= '[' { '[' } RefType | Primitive
  *     RefType ::= 'L' rawName ';'
  *     Primitive ::= 'Z' | 'C' | 'B' | 'S' | 'I' | 'F' | 'J' | 'D' | 'V'
  */
object DescParser:
  def parse(ptr:StrPtr):Either[String,(Descriptor,StrPtr)] = 
    if ptr("(").equ then methodDesc(ptr)
    else typeDesc(ptr)

  def methodDesc(ptr:StrPtr):Either[String,(MethodDesc,StrPtr)] = 
    ???
    // if ! ptr("(").equ then
    //   Left("expect ( at begin of method desc")
    // else
    //   var p = ptr + 1
    //   var args = List[Java14Type]()
    //   var stop = false
    //   var res:Option[Java14Type] = None
    //   while ! stop do
    //     if p(")").equ then 
    //       stop = true
    //     else
    //       typeDesc(p) match
    //         case Left(err) => 
    //           stop = true
    //           Left(err)
    //         case Right((td, nextPtr)) =>
    //           args = args :+ td
    //           p = nextPtr
    //   typeDesc(p+1) match
    //     case Left(err) => Left(err)
    //     case Right((td,nextPtr)) =>
    //       res = Some(td)
    //       p = nextPtr
      

  def typeDesc(ptr:StrPtr):Either[String,(Java14Type,StrPtr)] = 
    if ptr("[").equ then arrayDesc(ptr)
    else unaryDesc(ptr)

  def unaryDesc(ptr:StrPtr):Either[String,(TypeDesc.UnaryType,StrPtr)] = 
    if ptr("L").equ then refType(ptr).map( (t,p) => (TypeDesc.UnaryType(t),p) )
    else primitive(ptr).map( (t,p) => (TypeDesc.UnaryType(t),p) )

  def arrayDesc(ptr:StrPtr):Either[String,(TypeDesc.ArrayType,StrPtr)] = 
    if ! ptr("[").equ then Left("expect [")
    else
      var cnt = 0
      var p = ptr
      while !p("[").equ && p.hasTarget do
        cnt += 1
        p += 1
      if ! p.hasTarget then Left("expect type... after [")
      else
        refType(p).map( t => (TypeDesc.ArrayType(t._1,cnt),t._2) )

  def refType(ptr:StrPtr):Either[String,(RefType,StrPtr)] = 
    if !ptr("L").equ then Left("expect L at begin of ref type")
    else
      var p = ptr + 1
      val sb = new StringBuilder()
      while p.hasTarget && !p(";").equ do
        sb.append(p.fetch(1))
        p = p + 1

      if p(";").equ then
        Right((RefType(sb.toString()), p+1))
      else
        Left("expect ; at end of refType")

  def primitive(ptr:StrPtr):Either[String,(Primitive,StrPtr)] = 
    if ptr(Primitive.BOOL.rawName).equ then Right((Primitive.BOOL,ptr+1))
    else
      if ptr(Primitive.CHAR.rawName).equ then Right((Primitive.CHAR,ptr+1))
      else
        if ptr(Primitive.BYTE.rawName).equ then Right((Primitive.BYTE,ptr+1))
        else
          if ptr(Primitive.SHORT.rawName).equ then Right((Primitive.SHORT,ptr+1))
          else
            if ptr(Primitive.INT.rawName).equ then Right((Primitive.INT,ptr+1))
            else
              if ptr(Primitive.FLOAT.rawName).equ then Right((Primitive.FLOAT,ptr+1))
              else
                if ptr(Primitive.DOUBLE.rawName).equ then Right((Primitive.DOUBLE,ptr+1))
                else
                  if ptr(Primitive.VOID.rawName).equ then Right((Primitive.VOID,ptr+1))
                  else
                    Left("not matched")

