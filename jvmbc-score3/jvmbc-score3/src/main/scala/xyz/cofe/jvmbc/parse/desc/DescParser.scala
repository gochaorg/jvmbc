package xyz.cofe.jvmbc.parse.desc

import xyz.cofe.jvmbc.sparse.*

/**
  * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
  * 
  * Грамматика, нотация eBNF
  * 
  * Дескриптор метода
  * 
  *     MethodDescriptor ::= '(' { ParameterDescriptor } ')'  ReturnDescriptor
  * 
  *     ParameterDescriptor ::= FieldType
  * 
  *     ReturnDescriptor ::= FieldType | VoidDescriptor
  * 
  *     VoidDescriptor ::= 'V'
  * 
  * Дескриптор поля
  * 
  * FieldDescriptor ::= FieldType
  * 
  * Тип поля/параметра
  * 
  *     FieldType ::= BaseType | ObjectType | ArrayType
  * 
  *     BaseType ::= 'B' # byte
  *                | 'C' # char
  *                | 'D' # double
  *                | 'F' # float
  *                | 'I' # int
  *                | 'J' # long
  *                | 'S' # short
  *                | 'Z' # boolean
  * 
  *     ObjectType ::= 'L' ClassName ';'
  *                  
  * 
  *     ArrayType ::= '[' ComponentType
  * 
  *     ComponentType ::= FieldType
  */
object DescParser:
  import Pattern.*
  val baseType:Pattern[BaseType] = textMatch[BaseType]("B")(_ => ByteType)
                                 | textMatch[BaseType]("C")(_ => CharType)
                                 | textMatch[BaseType]("D")(_ => DoubleType)
                                 | textMatch[BaseType]("F")(_ => FloatType)
                                 | textMatch[BaseType]("I")(_ => IntType)
                                 | textMatch[BaseType]("J")(_ => LongType)
                                 | textMatch[BaseType]("S")(_ => ShortType)
                                 | textMatch[BaseType]("Z")(_ => BoolType)

  val objectType:Pattern[ObjectType] = new Pattern[ObjectType] {
    override def apply(ptr: SPtr): Either[String, (ObjectType, SPtr)] = {
      ptr.fetch(1) match
        case "L" => 
          var p = ptr + 1
          val sb = new StringBuilder()
          var stop = false
          while p.available > 0 && !stop do
            val c = p.fetch(1)
            c match
              case ";" => 
                p += 1
                stop = true
              case _ =>
                p += 1
                sb.append(c)
          if stop
          then Right((ObjectType(sb.toString),p))
          else Left("expect ;")
        case _ => Left("expect L")
    }
  }

  val arrayComponent : Pattern[ArrayComponent] =
    objectType.map( r => r:ArrayComponent) |
    baseType.map( r => r:ArrayComponent)

  val arrayType : Pattern[ArrayType] = 
    ( Pattern.textMatch[Unit]("[")(_ => ()).repeat(1,1000) { _.size } +
      arrayComponent
    ).map( (cnt,cmpt) => ArrayType(cnt,cmpt) )

  val voidType : Pattern[Void] = textMatch[Void]("V")( _ => Void())

  val fieldType : Pattern[FieldType] = baseType.map(r => r:FieldType)
                                     | arrayType.map(r => r:FieldType)
                                     | objectType.map(r => r:FieldType)

  val returnType : Pattern[Return] = voidType.map(r => r:Return)
                                   | fieldType.map(r => r:Return)

  val method : Pattern[Method] =
    (
      textMatch("(")(_ => ()) +
      fieldType.repeat(0,1000)(lst=>lst) +
      textMatch(")")(_ => ()) +
      returnType
    ).tmap( (a,b,c,d) => Method(b,d) )
