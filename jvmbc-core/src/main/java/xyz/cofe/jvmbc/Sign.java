package xyz.cofe.jvmbc;

/**
 * Представление signature для generic типов
 *
 * <p>
 * <ul>
 *     <li>
 *         <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html">4.3.4. Signatures</a>
 *     </li>
 *     <li>
 *         <a href="https://docs.oracle.com/javase/specs/jvms/se18/html/jvms-4.html#jvms-4.7.9.1">4.7.9.1. Signatures</a>
 *     </li>
 * </ul>
 *
 * A reference type signature represents a reference type of the Java programming language,
 * that is, a class or interface type, a type variable, or an array type.
 *
 * <p>
 * A class type signature represents a (possibly parameterized) class or interface type.
 * A class type signature must be formulated such that it can be reliably mapped to
 * the binary name of the class it denotes by erasing any type arguments and converting each . character to a $ character.
 *
 * <p>
 * A type variable signature represents a type variable.
 *
 * <p>
 * An array type signature represents one dimension of an array type.
 *
 * <pre>
 * JavaTypeSignature ::= ReferenceTypeSignature
 *                     | BaseType
 *
 * BaseType ::= 'B' | 'C' | 'D' | 'F' | 'I' | 'J' | 'S' | 'Z'
 *
 * ReferenceTypeSignature ::= ClassTypeSignature
 *                          | TypeVariableSignature
 *                          | ArrayTypeSignature
 *
 * ClassTypeSignature ::= 'L'  [PackageSpecifier] SimpleClassTypeSignature {ClassTypeSignatureSuffix} ';'
 *
 * PackageSpecifier ::= Identifier '/' {PackageSpecifier}
 *
 * SimpleClassTypeSignature ::= Identifier [TypeArguments]
 *
 * TypeArguments ::= '<' TypeArgument {TypeArgument} '>'
 *
 * TypeArgument ::= [WildcardIndicator] ReferenceTypeSignature | '*'
 *
 * WildcardIndicator ::= '+' | '-'
 *
 * ClassTypeSignatureSuffix ::= '.' SimpleClassTypeSignature
 *
 * TypeVariableSignature ::= 'T' Identifier ';'
 *
 * ArrayTypeSignature ::=  '[' JavaTypeSignature
 * </pre>
 *
 * A class signature encodes type information about a (possibly generic) class or interface declaration.
 * It describes any type parameters of the class or interface, and lists its (possibly parameterized)
 * direct superclass and direct superinterfaces, if any. A type parameter is described by its name,
 * followed by any class bound and interface bounds.
 *
 * <pre>
 * ClassSignature ::= [TypeParameters] SuperclassSignature {SuperinterfaceSignature}
 *
 * TypeParameters ::= '<' TypeParameter {TypeParameter} '>'
 *
 * TypeParameter ::= Identifier ClassBound {InterfaceBound}
 *
 * ClassBound ::= ':' [ReferenceTypeSignature]
 *
 * InterfaceBound ::= ':' ReferenceTypeSignature
 *
 * SuperclassSignature ::= ClassTypeSignature
 *
 * SuperinterfaceSignature ::= ClassTypeSignature
 * </pre>
 *
 * A method signature encodes type information about a (possibly generic) method declaration.
 * It describes any type parameters of the method; the (possibly parameterized) types of any formal parameters;
 * the (possibly parameterized) return type,
 * if any; and the types of any exceptions declared in the method's throws clause.
 *
 * <pre>
 * MethodSignature ::= [TypeParameters] '(' {JavaTypeSignature} ')' Result {ThrowsSignature}
 *
 * Result ::= JavaTypeSignature |  VoidDescriptor
 *
 * ThrowsSignature ::= '^' ClassTypeSignature | '^' TypeVariableSignature
 * </pre>
 *
 * The following production from §4.3.3 is repeated here for convenience:
 *
 * <pre>
 * VoidDescriptor ::= 'V'
 * </pre>
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
 * <pre>
 * FieldSignature ::= ReferenceTypeSignature
 * </pre>
 *
 *
 */
public class Sign {
    /**
     * Конструктор
     * @param raw сырое представление
     */
    public Sign(String raw){
        if( raw==null )throw new IllegalArgumentException( "raw==null" );
        this.raw = raw;
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public Sign(Sign sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        raw = sample.raw;
    }

    public Sign clone(){ return new Sign(this); }

    protected String raw;

    /**
     * Возвращает сырое представление
     * @return сырое представление
     */
    public String getRaw(){ return raw; }

    public String toString(){
        return "Sign{raw="+raw+"}";
    }
}
