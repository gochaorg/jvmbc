package xyz.cofe.jvmbc.parse.sign

trait Show[A]:
  def show(a:A):String

object Show:
  extension (str:String)
    def blockIdent(prefix:String="\n"):String =
      val lines = str.split("\\r?\\n")
      if lines.length>1 then
        prefix + lines.map(l => "  "+l).mkString("\n")
      else
        str

  given Show[BaseType] with
    override def show(a:BaseType): String = 
      a match
        case ByteType => "byte"
        case CharType => "char"
        case DoubleType => "double"
        case FloatType => "float"
        case IntType => "int"
        case LongType => "long"
        case ShortType => "short"
        case BoolType => "bool"

  given Show[VoidDescriptor.type] with
    override def show(a: VoidDescriptor.type): String = 
      "void"

  given Show[FieldSignature] with
    override def show(a: FieldSignature): String = 
      a match
        case v:ClassTypeSignature => summon[Show[ClassTypeSignature]].show(v)
        case v:TypeVariableSignature => summon[Show[TypeVariableSignature]].show(v)
        case v:ArrayTypeSignature => summon[Show[ArrayTypeSignature]].show(v)

  given Show[TypeVariableSignature] with
    override def show(a: TypeVariableSignature): String = 
      ???

  given Show[ArrayTypeSignature] with
    override def show(a: ArrayTypeSignature): String = 
      "[" + summon[Show[JavaTypeSignature]].show(a.javaTypeSign).blockIdent()

  given Show[ClassTypeSignature] with
    override def show(a: ClassTypeSignature): String = 
      ???

  given Show[JavaTypeSignature] with
    override def show(a: JavaTypeSignature): String = 
      a match
        case b:BaseType => summon[Show[BaseType]].show(b)
        case v:ClassTypeSignature => summon[Show[ClassTypeSignature]].show(v)
        case v:TypeVariableSignature => summon[Show[TypeVariableSignature]].show(v)
        case v:ArrayTypeSignature => summon[Show[ArrayTypeSignature]].show(v)

  given Show[ThrowsSignature] with
    override def show(a: ThrowsSignature): String = 
      a match
        case v:TypeVariableSignature => summon[Show[TypeVariableSignature]].show(v)
        case v:ClassTypeSignature => summon[Show[ClassTypeSignature]].show(v)

  given Show[Result] with
    override def show(a: Result): String = 
      a match
        case v:VoidDescriptor.type => summon[Show[VoidDescriptor.type]].show(v)
        case v:JavaTypeSignature => summon[Show[JavaTypeSignature]].show(v)

  given Show[MethodSignature] with
    override def show(a: MethodSignature): String = 
      ???

  given Show[SuperinterfaceSignature] with
    override def show(a: SuperinterfaceSignature): String = 
      a match
        case v:ClassTypeSignature => summon[Show[ClassTypeSignature]].show(v)

  given Show[SuperclassSignature] with
    override def show(a: SuperclassSignature): String = 
      a match
        case v:ClassTypeSignature => summon[Show[ClassTypeSignature]].show(v)

  given Show[InterfaceBound] with
    override def show(a: InterfaceBound): String = ???

  given Show[ClassBound] with
    override def show(a: ClassBound): String = ???

  given Show[TypeParameter] with
    override def show(a: TypeParameter): String = ???

  given Show[TypeParameters] with
    override def show(a: TypeParameters): String = ???
      
  given Show[ClassSignature] with
    override def show(a: ClassSignature): String = ???

  given Show[ClassTypeSignatureSuffix] with
    override def show(a: ClassTypeSignatureSuffix): String = ???

  given Show[WildcardIndicator] with
    override def show(a: WildcardIndicator): String = ???

  given Show[TypeArgument] with
    override def show(a: TypeArgument): String = 
      a match
        case TypeArgumentAny => summon[Show[TypeArgumentAny.type]].show(TypeArgumentAny)
        case v:TypeArgumentRef => summon[Show[TypeArgumentRef]].show(v)

  given Show[TypeArgumentAny.type] with
    override def show(a: TypeArgumentAny.type): String =
      ???

  given Show[TypeArgumentRef] with
    override def show(a: TypeArgumentRef): String = 
      ???

  given Show[TypeArguments] with
    override def show(a: TypeArguments): String = ???

  given Show[SimpleClassTypeSignature]  with
    override def show(a: SimpleClassTypeSignature): String = 
      "SimpleClassTypeSignature\n"

  given Show[ReferenceTypeSignature] with
    override def show(a: ReferenceTypeSignature): String =
      a match
        case v:ClassTypeSignature => summon[Show[ClassTypeSignature]].show(v)
        case v:TypeVariableSignature => summon[Show[TypeVariableSignature]].show(v)
        case v:ArrayTypeSignature => summon[Show[ArrayTypeSignature]].show(v)
      