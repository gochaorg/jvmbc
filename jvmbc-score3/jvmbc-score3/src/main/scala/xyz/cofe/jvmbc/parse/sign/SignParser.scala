package xyz.cofe.jvmbc.parse.sign

import xyz.cofe.jvmbc.sparse.*

/**
 * Сигнатура типа с Generic 
 * 
 *  - https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 *  - https://docs.oracle.com/javase/specs/jvms/se18/html/jvms-4.html#jvms-4.7.9.1
 * 
 * Сигнатура(Sign) типа Java представляет либо ссылочный тип, либо примитивный тип языка программирования Java.
 * 
 *     JavaTypeSignature ::= ReferenceTypeSignature
 *                         | BaseType
 *    
 *     BaseType ::= 'B' | 'C' | 'D' | 'F' | 'I' | 'J' | 'S' | 'Z'
 * 
 * ReferenceTypeSignature - представляет ссылочный тип языка программирования Java, 
 * то есть тип класса или интерфейса, переменную типа или тип массива.
 * 
 * ClassTypeSignature - представляет (возможно, параметризованный) класс или тип интерфейса. 
 * Сигнатура типа класса должна быть сформулирована таким образом, 
 * чтобы ее можно было надежно сопоставить с двоичным именем класса, который она обозначает, 
 * путем стирания любых аргументов типа и преобразования каждого из них. символа в символ $.
 * 
 * TypeVariableSignature - представляет собой переменную типа.
 * 
 * ArrayTypeSignature - представляет одно измерение типа массива.
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
 * Сигнатура класса кодирует информацию о типе объявления класса (возможно, общего). 
 * Он описывает любые параметры типа класса и перечисляет его (возможно, параметризованный) 
 * прямой суперкласс и прямые суперинтерфейсы, если таковые имеются. 
 * Параметр типа описывается своим именем, за которым следуют любые ограничения класса и интерфейса.
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
 * Сигнатура метода кодирует информацию о типе объявления (возможно, универсального) метода. 
 * Описывает любые типовые параметры метода; (возможно, параметризованные) типы любых формальных параметров; 
 * тип возвращаемого значения (возможно, параметризованный), если таковой имеется; 
 * и типы любых исключений, объявленных в предложении throws метода.
 * 
 *     MethodSignature ::= [TypeParameters] '(' {JavaTypeSignature} ')' Result {ThrowsSignature}
 *    
 *     Result ::= JavaTypeSignature |  VoidDescriptor
 *    
 *     ThrowsSignature ::= '^' ClassTypeSignature | '^' TypeVariableSignature
 * 
 * Следующее производство из §4.3.3 повторяется здесь для удобства:
 * 
 *     VoidDescriptor ::= 'V'
 * 
 * Из-за артефактов, созданных компилятором, сигнатура метода может не соответствовать точно дескриптору метода (§4.3.3). 
 * В частности, количество формальных типов параметров в сигнатуре метода может быть меньше, 
 * чем количество дескрипторов параметров в дескрипторе метода.
 * 
 * Подпись поля кодирует (возможно, параметризованный) тип поля, формальный параметр или объявление локальной переменной.
 * 
 *     FieldSignature ::= ReferenceTypeSignature
 * 
 */
object SignParser:
  import Pattern.*

  //given stdlog:LogWriter = IndentLogWriter(System.out)
  given stdlog:LogWriter = NopLogWriter()

  // JavaTypeSignature ::= ReferenceTypeSignature
  //                     | BaseType
  val javaTypeSign = ProxyPattern[JavaTypeSignature]

  // BaseType ::= 'B' | 'C' | 'D' | 'F' | 'I' | 'J' | 'S' | 'Z'
  val baseType:Pattern[BaseType] 
    = textMatch("B")(_ => ByteType)
    | textMatch("C")(_ => CharType)
    | textMatch("D")(_ => DoubleType)
    | textMatch("F")(_ => FloatType)
    | textMatch("I")(_ => IntType)
    | textMatch("L")(_ => LongType)
    | textMatch("S")(_ => ShortType)
    | textMatch("Z")(_ => BoolType)

  // VoidDescriptor ::= 'V'
  val voidDesc:Pattern[VoidDescriptor.type]
    = textMatch("V")(_ => VoidDescriptor)

  // ReferenceTypeSignature ::= ClassTypeSignature
  //                          | TypeVariableSignature
  //                          | ArrayTypeSignature
  val refTypeSign = Pattern.ProxyPattern[ReferenceTypeSignature]()

  lazy val refTypeSign0:Pattern[ReferenceTypeSignature] 
    = clsTypeSign.map(r=>r:ReferenceTypeSignature)
    | typeVarSign.map(r=>r:ReferenceTypeSignature)
    | arrTypeSign.map(r=>r:ReferenceTypeSignature)

  // 'L'  [PackageSpecifier] SimpleClassTypeSignature {ClassTypeSignatureSuffix} ';'
  //
  // эквивалентно
  //
  // 'L' Identifier { '/' Identifier } [TypeArguments] {ClassTypeSignatureSuffix} ';'
  //
  // эквивалентно
  //
  // 'L' idNameSepSlash [TypeArguments] { '.' Identifier [TypeArguments] } ';'
  // 
  lazy val clsTypeSign:Pattern[ClassTypeSignature] = 
    ( textMatch("L")(_ => ()) 
    + idNameSepSlash 
    + typeArgs.repeat(0,1)(r=>r)
    + clsTypeSignSuff.repeat(0,1000)(r=>r)
    + textMatch(";")(_ => ())
    ).tmap( (_,names,ta,suffs,_) => 
      ClassTypeSignature(
        names.dropRight(1).map( id => PackageSpecifier(id.string) ),
        SimpleClassTypeSignature( names.last.string, ta.headOption ),
        suffs
      )
    )

  // idNameSepSlash ::= id { '/' id }
  lazy val idNameSepSlash:Pattern[List[Id]] = 
    ( id + 
      (textMatch("/")(_ => ()) + id).repeat(0,1000)(_.map((_,id)=>id))
    ).map( (id,lst) => id :: lst )

  // SimpleClassTypeSignature ::= Identifier [TypeArguments]
  val simleClassTypeSign = Pattern.ProxyPattern[SimpleClassTypeSignature]()
  lazy val simleClassTypeSign0:Pattern[SimpleClassTypeSignature] = 
    (id + typeArgs.repeat(0,1)(r=>r)).map( (id,ta) => SimpleClassTypeSignature(id.string, ta.headOption) )

  // TypeArguments ::= '<' TypeArgument {TypeArgument} '>'
  lazy val typeArgs:Pattern[TypeArguments] = 
    (
      textMatch("<")(_ => ()) +
      typeArg.repeat(1,1000)(r=>r) +
      textMatch(">")(_ => ())
    ).tmap((a,ts,b) => TypeArguments(ts.head, ts.tail))

  // TypeArgument ::= [WildcardIndicator] ReferenceTypeSignature | '*'
  lazy val typeArg:Pattern[TypeArgument] = 
    ( wildcard.repeat(0,1)(w=>w) + refTypeSign )
      .map( (ws,reft) => TypeArgumentRef(ws.headOption, reft) )
    | textMatch("*")(_ => TypeArgumentAny)

  // WildcardIndicator ::= '+' | '-'
  lazy val wildcard:Pattern[WildcardIndicator] 
    = textMatch("+")(_ => WildcardPlus)
    | textMatch("-")(_ => WildcardMinus)

  lazy val clsTypeSignSuff:Pattern[ClassTypeSignatureSuffix] =
    (
      textMatch(".")(_ => ()) +
      simleClassTypeSign
    ).map( (_,scts) => ClassTypeSignatureSuffix(scts) )

  case class Id(string:String)
  lazy val id:Pattern[Id] = new Pattern {
    override def apply(from: SPtr): Either[String, (Id, SPtr)] = 
      var ptr = from
      val sb = new StringBuilder()
      var stop = false
      while !stop do
        ptr.fetch(1) match
          case str if str.length()==1 && 
          !( ".;<>[/:".contains(str) ) // 4.7.9.1. Signatures 
            =>
              sb.append(str)
              ptr += 1
          case _ => 
            stop = true
      if sb.length()>0 then
        Right((Id(sb.toString()),ptr))
      else
        Left("not matched")
  }

  // TypeVariableSignature ::= 'T' Identifier ';'
  lazy val typeVarSign:Pattern[TypeVariableSignature] = 
    (
      textMatch("T")(_ => ()) +
      id +
      textMatch(";")(_ => ())
    ).tmap( (_,id,_) => TypeVariableSignature(id.string) )

  // ArrayTypeSignature ::=  '[' JavaTypeSignature
  lazy val arrTypeSign:Pattern[ArrayTypeSignature] = 
    ( textMatch("[")(_ => ()) 
    + javaTypeSign
    ).map( (_,jt) => ArrayTypeSignature(jt) )

  // ClassSignature ::= [TypeParameters] SuperclassSignature {SuperinterfaceSignature}
  lazy val classSign:Pattern[ClassSignature] = 
    ( typeParams.repeat(0,1)(r=>r)
    + superClsSign
    + superItfSign.repeat(0,1000)(r=>r)
    ).tmap( (t,c,i) => ClassSignature(t.headOption, c, i) )

  // TypeParameters ::= '<' TypeParameter {TypeParameter} '>'
  lazy val typeParams:Pattern[TypeParameters] = 
    ( textMatch("<")(_ => ()) 
    + typeParam
    + typeParam.repeat(0,1000)(r=>r)
    + textMatch(">")(_ => ())
    ).tmap( (_,h,t,_) => TypeParameters(h,t) )

  // TypeParameter ::= Identifier ClassBound {InterfaceBound}
  lazy val typeParam:Pattern[TypeParameter] = 
    ( id + classBound + itfBound.repeat(0,1000)(r=>r) )
    .tmap( (id,cb,ibs) => TypeParameter(id.string, cb, ibs) )

  // ClassBound ::= ':' [ReferenceTypeSignature]
  lazy val classBound:Pattern[ClassBound] = 
    ( textMatch(":")(_ => ())
    + refTypeSign.repeat(0,1)(r=>r)
    ).map( (_,rt) => ClassBound(rt.headOption) )

  // InterfaceBound ::= ':' ReferenceTypeSignature
  lazy val itfBound:Pattern[InterfaceBound] = 
    ( textMatch(":")(_ => ()) 
    + refTypeSign
    ).map( (_,rt) => rt:InterfaceBound )

  // SuperclassSignature ::= ClassTypeSignature
  lazy val superClsSign:Pattern[SuperclassSignature] = 
    clsTypeSign.map(r=>r:SuperclassSignature)

  // SuperinterfaceSignature ::= ClassTypeSignature
  lazy val superItfSign:Pattern[SuperinterfaceSignature] = clsTypeSign.map(r=>r:SuperinterfaceSignature)

  // MethodSignature ::= [TypeParameters] '(' {JavaTypeSignature} ')' Result {ThrowsSignature}
  lazy val methodSign:Pattern[MethodSignature] = ???

  // Result ::= JavaTypeSignature |  VoidDescriptor
  lazy val result:Pattern[Result]
    = javaTypeSign.map(r=>r:Result)
    | voidDesc.map(r=>r:Result)

  // ThrowsSignature ::= '^' ClassTypeSignature | '^' TypeVariableSignature
  lazy val throwsSign:Pattern[ThrowsSignature]
    = ( textMatch("^")(_ => ()) + clsTypeSign ).tmap( (_,c)=> c:ThrowsSignature )
    | ( textMatch("^")(_ => ()) + typeVarSign ).tmap( (_,c)=> c:ThrowsSignature )

  // FieldSignature ::= ReferenceTypeSignature
  lazy val fieldSign:Pattern[FieldSignature] =
    refTypeSign.map(r=>r:FieldSignature)

  //////////////////////////////////////////
  // recursive
  refTypeSign.set(refTypeSign0.name("refTypeSign0"))
  simleClassTypeSign.set(simleClassTypeSign0.name("simleClassTypeSign0"))
  javaTypeSign.set( 
      refTypeSign.map(r=>r:JavaTypeSignature)
    | baseType.map(r=>r:JavaTypeSignature)
  )