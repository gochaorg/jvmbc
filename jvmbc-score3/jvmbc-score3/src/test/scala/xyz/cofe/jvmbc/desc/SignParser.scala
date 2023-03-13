package xyz.cofe.jvmbc.desc

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
object SignParser
