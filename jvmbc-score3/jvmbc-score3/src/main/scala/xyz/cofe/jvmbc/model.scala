package xyz.cofe.jvmbc

import xyz.cofe.jvmbc.parse.desc.*

/** 
 * Имя класса 
 * 
 * @param raw Имя класса представленное в байт-коде, например: java/lang/String
 */
// case class JavaName(raw:String) {
//   require( raw!=null )

//   /** имя класса включая пакет, имена разделены точкой (`java.lang.String`) */
//   lazy val name:String = raw.replace("/",".")

//   /** имя класса */
//   lazy val nameList:List[String] = raw.split("\\.").toList

//   /** имя класса без названия пакета */
//   lazy val simpleName:String = nameList.last

//   /** название пакета */
//   lazy val packaje:List[String] = nameList.dropRight(1)

//   object rename {
//     def simpleName(newSimpleName:String):JavaName =
//       JavaName( (JavaName.this.packaje ++ List(newSimpleName)).mkString("/") )

//     def packaje( pkg:List[String] ):JavaName =
//       JavaName( (pkg ++ List(JavaName.this.simpleName)).mkString("/"))

//     def apply( javaNameString:String ):JavaName = 
//       JavaName( javaNameString.replace(".","/") )
//   }

//   def unapply( jn:JavaName ):Option[String] =
//     Some(jn.raw)

//   override def toString():String = name
// }

// object JavaName {
//   def raw(rawName:String):JavaName = new JavaName(rawName)
//   def java(name:String):JavaName = new JavaName(name.replace(".","/"))
// }

/** 
 * Сигнатура типа с Generic 
 * 
 *  - https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 *  - https://docs.oracle.com/javase/specs/jvms/se18/html/jvms-4.html#jvms-4.7.9.1
 * 
 * A reference type signature represents a reference type of the Java programming language,
 * that is, a class or interface type, a type variable, or an array type.
 * 
 * A class type signature represents a (possibly parameterized) class or interface type.
 * A class type signature must be formulated such that it can be reliably mapped to
 * the binary name of the class it denotes by erasing any type arguments and converting each . character to a $ character.
 * 
 * A type variable signature represents a type variable.
 * 
 * An array type signature represents one dimension of an array type.
 * 
 *     JavaTypeSignature ::= ReferenceTypeSignature
 *                         | BaseType
 *    
 *     BaseType ::= 'B' | 'C' | 'D' | 'F' | 'I' | 'J' | 'S' | 'Z'
 *    
 *     ReferenceTypeSignature ::= ClassTypeSignature
 *                              | TypeVariableSignature
 *                              | ArrayTypeSignature
 *    
 *     ClassTypeSignature ::= 'L'  [PackageSpecifier] SimpleClassTypeSignature {ClassTypeSignatureSuffix} ';'
 *    
 *     PackageSpecifier ::= Identifier '/' {PackageSpecifier}
 *    
 *     SimpleClassTypeSignature ::= Identifier [TypeArguments]
 *    
 *     TypeArguments ::= '<' TypeArgument {TypeArgument} '>'
 *    
 *     TypeArgument ::= [WildcardIndicator] ReferenceTypeSignature | '*'
 *    
 *     WildcardIndicator ::= '+' | '-'
 *    
 *     ClassTypeSignatureSuffix ::= '.' SimpleClassTypeSignature
 *    
 *     TypeVariableSignature ::= 'T' Identifier ';'
 *    
 *     ArrayTypeSignature ::=  '[' JavaTypeSignature
 * 
 * A class signature encodes type information about a (possibly generic) class or interface declaration.
 * It describes any type parameters of the class or interface, and lists its (possibly parameterized)
 * direct superclass and direct superinterfaces, if any. A type parameter is described by its name,
 * followed by any class bound and interface bounds.
 * 
 *     ClassSignature ::= [TypeParameters] SuperclassSignature {SuperinterfaceSignature}
 *    
 *     TypeParameters ::= '<' TypeParameter {TypeParameter} '>'
 *    
 *     TypeParameter ::= Identifier ClassBound {InterfaceBound}
 *    
 *     ClassBound ::= ':' [ReferenceTypeSignature]
 *    
 *     InterfaceBound ::= ':' ReferenceTypeSignature
 *    
 *     SuperclassSignature ::= ClassTypeSignature
 *    
 *     SuperinterfaceSignature ::= ClassTypeSignature
 * 
 * A method signature encodes type information about a (possibly generic) method declaration.
 * It describes any type parameters of the method; the (possibly parameterized) types of any formal parameters;
 * the (possibly parameterized) return type,
 * if any; and the types of any exceptions declared in the method's throws clause.
 * 
 *     MethodSignature ::= [TypeParameters] '(' {JavaTypeSignature} ')' Result {ThrowsSignature}
 *    
 *     Result ::= JavaTypeSignature |  VoidDescriptor
 *    
 *     ThrowsSignature ::= '^' ClassTypeSignature | '^' TypeVariableSignature
 * 
 * The following production from §4.3.3 is repeated here for convenience:
 *
 *     VoidDescriptor ::= 'V'
 * 
 * A method signature encoded by the Signature attribute may not correspond exactly to
 * the method descriptor in the method_info structure (§4.3.3).
 * In particular, there is no assurance that the number of formal parameter types in
 * the method signature is the same as the number of parameter descriptors in the method descriptor.
 * The numbers are the same for most methods, but certain constructors in
 * the Java programming language have an implicitly declared parameter which a compiler
 * represents with a parameter descriptor but may omit from the method signature.
 * See the note in §4.7.18 for a similar situation involving parameter annotations.
 *
 * A field signature encodes the (possibly parameterized) type of a field, formal parameter, local variable, or record component declaration.
 * 
 *     FieldSignature ::= ReferenceTypeSignature
 */
case class Sign(raw:String) extends AnyVal

case class TDesc( fieldType: xyz.cofe.jvmbc.parse.desc.FieldType ):
  lazy val raw:String =
    def arrayTypeRaw(at:ArrayType) =
      ( "[" * at.dimension ) + (
        at.component match
          case b:BaseType => b.letter.toString()
          case v:ObjectType => s"L${v.rawClassName};"
      )

    fieldType match
      case b:BaseType => b.letter.toString()
      case o:ObjectType => s"L${o.rawClassName};"
      case a:ArrayType => arrayTypeRaw(a)

object TDesc {
  def unsafe( raw:String ):TDesc =
    xyz.cofe.jvmbc.parse.desc.FieldType.parse(raw).map(TDesc(_)).getOrElse(throw new Error(s"can't parse \"${raw}\" as FieldType"))
}

/** Сигнатура типа */
// case class TDesc(raw:String) {
//   import TDesc._
//   private lazy val decoded = if raw!=null then parse(raw).map(_._1) else None
//   lazy val name:Option[String]     = decoded.map(_.name)  
//   lazy val dimension:Option[Int]   = decoded.map(_.dimension)
//   lazy val isArray:Option[Boolean] = decoded.map(_.isArray)
//   override def toString():String =
//     decoded match
//       case None => s"desc:$raw"
//       case Some(d) =>
//         val sb = new java.lang.StringBuilder()
//         sb.append(d.name)
//         (1 to d.dimension).foreach { _ => sb.append("[]") }
//         sb.toString
// }

// object TDesc {
//   case class Parsed(name:String,dimension:Int=0) {
//     require(dimension>=0)
//     require(name!=null)
//     lazy val isArray = dimension>0
//   }
//   /**
//    * Парсинг
//    * @return результат парсинга и следующий символ (индекс) для парсинга
//    */
//   def parse(raw:String,from:Int=0):Option[(Parsed,Int)] =
//     require(raw!=null)
//     require(from>=0)
//     var ptr=from-1
//     var stop=false
//     var state=0
//     var dim=0
//     var typeName:String = null
//     var typeNameBuff = new java.lang.StringBuilder()
//     while( !stop && ptr<raw.length )
//       ptr+=1
//       val c = raw(ptr)
//       state match
//         case 0 => c match
//           case '[' => dim += 1
//           case 'Z' => typeName = "boolean" ; stop = true
//           case 'C' => typeName = "char" ;    stop = true
//           case 'B' => typeName = "byte" ;    stop = true
//           case 'S' => typeName = "short" ;   stop = true
//           case 'I' => typeName = "int" ;     stop = true
//           case 'F' => typeName = "float" ;   stop = true
//           case 'J' => typeName = "long" ;    stop = true
//           case 'D' => typeName = "double" ;  stop = true
//           case 'V' => typeName = "void" ;    stop = true
//           case 'L' => state = 1
//           case _ => stop
//         case 1 => c match
//           case '/' => typeNameBuff.append('.')
//           case ';' => typeName = typeNameBuff.toString ; stop = true
//           case   _ => typeNameBuff.append(c)
//     if typeName!=null then
//       Some( (Parsed(typeName,dim), ptr+1) )
//     else
//       None
// }

/** Сигнатура метода типа */
case class MDesc(raw:String) // TODO decode

/** 
Маркер модели байт-кода 

В состав входит
- [[xyz.cofe.jmvbc.ann.AnnCode]] Байт-код аннотаций
- [[xyz.cofe.jmvbc.cls.ClassCode]] Байт-код классов
- [[xyz.cofe.jmvbc.ann.FieldCode]] Байт-код полей
- [[xyz.cofe.jmvbc.ann.MethCode]] Байт-код методов
 */
trait ByteCode

sealed trait TypeRef(val raw:Int)
case class CTypeRef(private val _raw:Int) extends TypeRef(_raw)
case class MTypeRef(private val _raw:Int) extends TypeRef(_raw)
case class MTypeInsnRef(private val _raw:Int) extends TypeRef(_raw)
case class MTypeTryCatchRef(private val _raw:Int) extends TypeRef(_raw)
case class MTypeLocalVarRef(private val _raw:Int) extends TypeRef(_raw)
case class ATypeRef(private val _raw:Int) extends TypeRef(_raw)
case class RTypeRef(private val _raw:Int) extends TypeRef(_raw)

/**

 A reference to a type appearing in a class, field or method declaration, or on an instruction.
 Such a reference designates the part of the class where the referenced type is appearing (e.g. an
 'extends', 'implements' or 'throws' clause, a 'new' instruction, a 'catch' clause, a type cast, a
 local variable declaration, etc).

 @author Eric Bruneton

- CLASS_TYPE_PARAMETER 
  The sort of type references that target a type parameter of a generic class. See {@link #getSort}.

- METHOD_TYPE_PARAMETER 
  The sort of type references that target a type parameter of a generic method. See {@link #getSort}.

- CLASS_EXTENDS 
  The sort of type references that target the super class of a class or one of the interfaces it implements. See {@link #getSort}.

- CLASS_TYPE_PARAMETER_BOUND
  The sort of type references that target a bound of a type parameter of a generic class.

- METHOD_TYPE_PARAMETER_BOUND
  The sort of type references that target a bound of a type parameter of a generic method.

- FIELD
  The sort of type references that target the type of a field. See {@link #getSort}

- METHOD_RETURN
  The sort of type references that target the return type of a method. See {@link #getSort}.

- METHOD_RECEIVER
  The sort of type references that target the receiver type of a method. See {@link #getSort}

- METHOD_FORMAL_PARAMETER
  The sort of type references that target the type of a formal parameter of a method. See {@link #getSort}.

- THROWS
  The sort of type references that target the type of an exception declared in the throws clause of a method. See {@link #getSort}.

- LOCAL_VARIABLE
  The sort of type references that target the type of a local variable in a method. See {@link #getSort}.

- RESOURCE_VARIABLE
  The sort of type references that target the type of a resource variable in a method. See {@link #getSort}.

- EXCEPTION_PARAMETER
  The sort of type references that target the type of the exception of a 'catch' clause in a method. See {@link #getSort}.

- INSTANCEOF
  The sort of type references that target the type declared in an 'instanceof' instruction.  

- NEW
  The sort of type references that target the type of the object created by a 'new' instruction.

- CONSTRUCTOR_REFERENCE
  The sort of type references that target the receiver type of a constructor reference.  

- METHOD_REFERENCE
  The sort of type references that target the receiver type of a method reference. See {@link #getSort}.

- CAST
  The sort of type references that target the type declared in an explicit or implicit cast instruction. See {@link #getSort}.

- CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT
  The sort of type references that target a type parameter of a generic constructor in a constructor call. See {@link #getSort}.

- METHOD_INVOCATION_TYPE_ARGUMENT
  The sort of type references that target a type parameter of a generic method in a method call.

- CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT
  The sort of type references that target a type parameter of a generic constructor in a constructor reference. See {@link #getSort}.

- METHOD_REFERENCE_TYPE_ARGUMENT
  The sort of type references that target a type parameter of a generic method in a method reference. See {@link #getSort}.
*/
enum TypeRefKind(raw:Int):
  case CLASS_TYPE_PARAMETER  extends TypeRefKind(0x00)
  case METHOD_TYPE_PARAMETER extends TypeRefKind(0x01)
  case CLASS_EXTENDS extends TypeRefKind(0x10)
  case CLASS_TYPE_PARAMETER_BOUND  extends TypeRefKind(0x11)
  case METHOD_TYPE_PARAMETER_BOUND extends TypeRefKind(0x12)
  case FIELD extends TypeRefKind(0x13)
  case METHOD_RETURN extends TypeRefKind(0x14)
  case METHOD_RECEIVER extends TypeRefKind(0x15)
  case METHOD_FORMAL_PARAMETER extends TypeRefKind(0x16)
  case THROWS extends TypeRefKind(0x17)
  case LOCAL_VARIABLE extends TypeRefKind(0x40)
  case RESOURCE_VARIABLE extends TypeRefKind(0x41)
  case EXCEPTION_PARAMETER extends TypeRefKind(0x42)
  case INSTANCEOF extends TypeRefKind(0x43)
  case NEW extends TypeRefKind(0x44)
  case CONSTRUCTOR_REFERENCE extends TypeRefKind(0x45)
  case METHOD_REFERENCE extends TypeRefKind(0x46)
  case CAST extends TypeRefKind(0x47)
  case CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT extends TypeRefKind(0x48)
  case METHOD_INVOCATION_TYPE_ARGUMENT extends TypeRefKind(0x49)
  case CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT extends TypeRefKind(0x4A)
  case METHOD_REFERENCE_TYPE_ARGUMENT extends TypeRefKind(0x4B)

trait BitMask:
  def bitMask:Int
  def isSet( flags:Int ):Boolean = (flags & bitMask) == bitMask
  def set( flags:Int ):Int = flags | bitMask
  def reset( flags:Int ):Int = flags ^ bitMask
