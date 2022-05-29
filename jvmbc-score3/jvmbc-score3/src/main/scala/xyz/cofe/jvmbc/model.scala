package xyz.cofe.jvmbc

/** Имя класса */
case class JavaName(raw:String)

/** Сигнатура типа с Generic */
case class Sign(raw:String)

/** Сигнатура типа */
case class TDesc( raw:String )

/** Сигнатура метода типа */
case class MDesc(raw:String)

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
