package xyz.cofe.jvmbc.cls;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.objectweb.asm.*;
import xyz.cofe.jvmbc.*;
import xyz.cofe.jvmbc.ann.AnnotationDump;
import xyz.cofe.jvmbc.fld.FieldByteCode;
import xyz.cofe.jvmbc.fld.FieldDump;
import xyz.cofe.jvmbc.mdl.ModuleDump;
import xyz.cofe.jvmbc.mdl.Modulo;
import xyz.cofe.jvmbc.mth.MethodByteCode;
import xyz.cofe.jvmbc.mth.MethodDump;
import xyz.cofe.jvmbc.rec.RecordByteCode;
import xyz.cofe.jvmbc.rec.RecordDump;

/**
 * Дамп байт-кода класса
 *
 * <p>
 * order:
 * <ol>
 *     <li> visit
 *     <li> [ visitSource ]
 *     <li> [ visitModule ]
 *     <li> [ visitNestHost ]
 *     <li> [ visitPermittedSubclass ]
 *     <li> [ visitOuterClass ]
 *     <li> ( visitAnnotation | visitTypeAnnotation | visitAttribute )*
 *     <li> ( visitNestMember | visitInnerClass | visitRecordComponent | visitField | visitMethod )*
 *     <li> visitEnd
 * </ol>
 */
public class ClassDump<
    CBEGIN extends CBegin<CFIELD,CMETHOD, CM_LIST>,
    CFIELD extends CField,
    CMETHOD extends CMethod<CM_LIST>,
    CM_LIST extends List<MethodByteCode>
    > extends ClassVisitor {
    private void dump(String message,Object...args){
        if( message==null )return;
        if( args==null || args.length==0 ){
            System.out.println(message);
        }else{
            System.out.print(message);
            for( var a : args ){
                System.out.print(" ");
                System.out.print(a);
            }
            System.out.println();
        }
    }

    private final ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> classFactory;
    public static ClassDump<
        CBegin<CField,CMethod<List<MethodByteCode>>,List<MethodByteCode>>,
        CField,
        CMethod<List<MethodByteCode>>,
        List<MethodByteCode>
        > create(){
        return new ClassDump<>(new ClassFactory.Default());
    }

    /**
     * Constructs a new {@link ClassVisitor}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one of {@link
     *            Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     */
    public ClassDump(int api, ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> cf){
        super(api);
        if( cf==null )throw new IllegalArgumentException("cf==null");
        classFactory = cf;
    }

    public ClassDump(ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> cf){
        super(Opcodes.ASM9);
        if( cf==null )throw new IllegalArgumentException("cf==null");
        classFactory = cf;
    }

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public ClassDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    private void emit(ByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
    }

    protected final ThreadLocal<CBEGIN> currentClass = new ThreadLocal<>();
    protected Optional<CBEGIN> currentClass(){
        var v = currentClass.get();
        return v!=null ? Optional.of(v) : Optional.empty();
    }
    protected void currentClass(CBEGIN begin){
        currentClass.set(begin);
    }
    protected void currentClass(Consumer<CBEGIN> c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        var v = currentClass.get();
        if( v==null ){
            throw new IllegalThreadStateException("current class not defined");
        } else {
            c.accept(v);
        }
    }

    protected final ThreadLocal<Integer> currentIndex = new ThreadLocal<>();
    protected int currentIndex(){
        var i = currentIndex.get();
        if( i==null ){
            currentIndex.set(0);
            return 0;
        }
        return i;
    }
    protected int currentIndexGetAndInc(){
        var i = currentIndex.get();
        if( i==null ){
            currentIndex.set(1);
            return 0;
        }
        currentIndex.set(i+1);
        return i;
    }
    protected void currentIndex(int i){
        currentIndex.set(i);
    }

    /**
     * Visits the header of the class.
     *
     * @param version the class version. The minor version is stored in the 16 most significant bits,
     *     and the major version in the 16 least significant bits.
     * @param access the class's access flags (see {@link Opcodes}). This parameter also indicates if
     *     the class is deprecated {@link Opcodes#ACC_DEPRECATED} or a record {@link
     *     Opcodes#ACC_RECORD}.
     * @param name the internal name of the class (see {@link Type#getInternalName()}).
     * @param signature the signature of this class. May be {@literal null} if the class is not a
     *     generic one, and does not extend or implement generic classes or interfaces.
     * @param superName the internal of name of the super class (see {@link Type#getInternalName()}).
     *     For interfaces, the super class is {@link Object}. May be {@literal null}, but only for the
     *     {@link Object} class.
     * @param interfaces the internal names of the class's interfaces (see {@link
     *     Type#getInternalName()}). May be {@literal null}.
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
        var c = classFactory.cbegin(version,access,name,signature,superName,interfaces);
        currentClass(c);
        currentIndex(0);
        emit(c);
    }

    /**
     * Visits the source of the class.
     *
     * @param source the name of the source file from which the class was compiled. May be {@literal
     *     null}.
     * @param debug additional debug information to compute the correspondence between source and
     *     compiled elements of the class. May be {@literal null}.
     */
    @Override
    public void visitSource(String source, String debug){
        int ci = currentIndexGetAndInc();

        var c = new CSource(source,debug);
        currentClass(x -> {
            x.setSource(Optional.of(c));
            x.getOrder().put(c,ci);
        });

        emit(c);
    }

    /**
     * Visit the module corresponding to the class.
     *
     * @param name the fully qualified name (using dots) of the module.
     * @param access the module access flags, among {@code ACC_OPEN}, {@code ACC_SYNTHETIC} and {@code
     *     ACC_MANDATED}.
     * @param version the module version, or {@literal null}.
     * @return a visitor to visit the module values, or {@literal null} if this visitor is not
     *     interested in visiting this module.
     */
    @Override
    public ModuleVisitor visitModule(String name, int access, String version){
        //dump("module name="+name+" access="+(new AccFlags(access).flags())+" version="+version);

        CModule cmod = new CModule();
        cmod.setName(name);
        cmod.setAccess(access);
        cmod.setVersion(version!=null ? Optional.of(version) : Optional.empty());
        ModuleDump moduleDump = new ModuleDump(api){
            @Override
            protected Modulo newModulo(){
                return cmod;
            }
        };
        moduleDump.byteCode( bc -> {
            if( bc instanceof CModule ){
                CModule cmod0 = (CModule)bc;
                int ci = currentIndexGetAndInc();

                currentClass( cls -> {
                    cls.order(cmod0,ci);
                    cls.setModule( Optional.of( cmod0 ));
                });
            }
        });

        return moduleDump;
    }

    /**
     * Visits the nest host class of the class. A nest is a set of classes of the same package that
     * share access to their private members. One of these classes, called the host, lists the other
     * members of the nest, which in turn should link to the host of their nest. This method must be
     * called only once and only if the visited class is a non-host member of a nest. A class is
     * implicitly its own nest, so it's invalid to call this method with the visited class name as
     * argument.
     *
     * @param nestHost the internal name of the host class of the nest.
     */
    @Override
    public void visitNestHost(String nestHost){
        int ci = currentIndexGetAndInc();
        var c = new CNestHost(nestHost);

        currentClass( x -> {
            x.setNestHost(c);
            x.getOrder().put(c,ci);
        });

        emit(new CNestHost(nestHost));
    }

    /**
     * Visits the enclosing class of the class. This method must be called only if the class has an
     * enclosing class.
     *
     * @param owner internal name of the enclosing class of the class.
     * @param name the name of the method that contains the class, or {@literal null} if the class is
     *     not enclosed in a method of its enclosing class.
     * @param descriptor the descriptor of the method that contains the class, or {@literal null} if
     *     the class is not enclosed in a method of its enclosing class.
     */
    @Override
    public void visitOuterClass(String owner, String name, String descriptor){
        int ci = currentIndexGetAndInc();
        var c = new COuterClass(owner,name,descriptor);

        currentClass( x -> {
            x.setOuterClass(c);
            x.getOrder().put(c,ci);
        });

        emit(c);
    }

    /**
     * Visits an annotation of the class.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     *     interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
        int ci = currentIndexGetAndInc();
        CAnnotation a = new CAnnotation(descriptor,visible);

        AnnotationDump dump = new AnnotationDump(api);
        dump.byteCode(byteCodeConsumer,a);

        currentClass( x -> x.order(a,ci).getAnnotations().add(a) );

        emit(a);
        return dump;
    }

    /**
     * Visits an annotation on a type in the class signature.
     *
     * @param typeRef a reference to the annotated type. The sort of this type reference must be
     *     {@link TypeReference#CLASS_TYPE_PARAMETER}, {@link
     *     TypeReference#CLASS_TYPE_PARAMETER_BOUND} or {@link TypeReference#CLASS_EXTENDS}. See
     *     {@link TypeReference}.
     * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
     *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *     'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     *     interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
        int ci = currentIndexGetAndInc();
        CTypeAnnotation a = new CTypeAnnotation(typeRef,typePath!=null ? typePath.toString():null,descriptor,visible);

        AnnotationDump dump = new AnnotationDump(api);
        dump.byteCode(byteCodeConsumer,a);

        currentClass( x -> x.order(a,ci).getTypeAnnotations().add(a) );

        emit(a);
        return dump;
    }

    @Override
    public void visitAttribute(Attribute attribute){
        dump("ann "+attribute);
    }

    /**
     * Visits a member of the nest. A nest is a set of classes of the same package that share access
     * to their private members. One of these classes, called the host, lists the other members of the
     * nest, which in turn should link to the host of their nest. This method must be called only if
     * the visited class is the host of a nest. A nest host is implicitly a member of its own nest, so
     * it's invalid to call this method with the visited class name as argument.
     *
     * @param nestMember the internal name of a nest member.
     */
    @Override
    public void visitNestMember(String nestMember){
        int ci = currentIndexGetAndInc();
        var c = new CNestMember(nestMember);

        currentClass( x -> x.order(c,ci).getNestMembers().add(c) );

        emit(c);
    }

    /**
     * Visits a permitted subclasses. A permitted subclass is one of the allowed subclasses of the
     * current class.
     *
     * @param permittedSubclass the internal name of a permitted subclass.
     */
    @Override
    public void visitPermittedSubclass(String permittedSubclass){
        int ci = currentIndexGetAndInc();
        var c = new CPermittedSubclass(permittedSubclass);

        currentClass( x -> x.order(c,ci).setPermittedSubclass(c) );

        emit(c);
    }

    /**
     * Visits information about an inner class. This inner class is not necessarily a member of the
     * class being visited.
     *
     * @param name the internal name of an inner class (see {@link Type#getInternalName()}).
     * @param outerName the internal name of the class to which the inner class belongs (see {@link
     *     Type#getInternalName()}). May be {@literal null} for not member classes.
     * @param innerName the (simple) name of the inner class inside its enclosing class. May be
     *     {@literal null} for anonymous inner classes.
     * @param access the access flags of the inner class as originally declared in the enclosing
     *     class.
     */
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access){
        int ci = currentIndexGetAndInc();
        var c = new CInnerClass(name,outerName,innerName,access);

        currentClass( x -> x.order(c,ci).getInnerClasses().add(c) );

        emit(c);
    }

    /**
     * Visits a record component of the class.
     *
     * @param name the record component name.
     * @param descriptor the record component descriptor (see {@link Type}).
     * @param signature the record component signature. May be {@literal null} if the record component
     *     type does not use generic types.
     * @return a visitor to visit this record component annotations and attributes, or {@literal null}
     *     if this class visitor is not interested in visiting these annotations and attributes.
     */
    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature){
        dump("recordComponent name="+name+" descriptor="+descriptor+" signature="+signature);

        int ci = currentIndexGetAndInc();
        var c = new CRecord(name,descriptor,signature);

        currentClass( x -> x.order(c,ci).getRecords().add(c) );

        RecordDump dump = new RecordDump(api);
        dump.byteCode( bc -> {
            if( byteCodeConsumer!=null )byteCodeConsumer.accept(bc);
            if( bc instanceof RecordByteCode ){
                c.getRecordByteCodes().add( (RecordByteCode)bc );
            }
        });

        emit(c);

        return dump;
    }

    /**
     * Visits a field of the class.
     *
     * @param access the field's access flags (see {@link Opcodes}). This parameter also indicates if
     *     the field is synthetic and/or deprecated.
     * @param name the field's name.
     * @param descriptor the field's descriptor (see {@link Type}).
     * @param signature the field's signature. May be {@literal null} if the field's type does not use
     *     generic types.
     * @param value the field's initial value. This parameter, which may be {@literal null} if the
     *     field does not have an initial value, must be an {@link Integer}, a {@link Float}, a {@link
     *     Long}, a {@link Double} or a {@link String} (for {@code int}, {@code float}, {@code long}
     *     or {@code String} fields respectively). <i>This parameter is only used for static
     *     fields</i>. Its value is ignored for non static fields, which must be initialized through
     *     bytecode instructions in constructors or methods.
     * @return a visitor to visit field annotations and attributes, or {@literal null} if this class
     *     visitor is not interested in visiting these annotations and attributes.
     */
    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value){
        int ci = currentIndexGetAndInc();
        CFIELD c = classFactory.cfield(access,name,descriptor,signature,value);

        FieldDump dump = new FieldDump(api);
        dump.byteCode(b -> {
            if( byteCodeConsumer!=null )byteCodeConsumer.accept(b);
            if( b instanceof FieldByteCode ){
                c.getFieldByteCodes().add( (FieldByteCode) b);
            }
        });

        currentClass( x -> x.order(c,ci).getFields().add(c) );

        emit(c);
        return dump;
    }

    /**
     * Visits a method of the class. This method <i>must</i> return a new {@link MethodVisitor}
     * instance (or {@literal null}) each time it is called, i.e., it should not return a previously
     * returned visitor.
     *
     * @param access the method's access flags (see {@link Opcodes}). This parameter also indicates if
     *     the method is synthetic and/or deprecated.
     * @param name the method's name.
     * @param descriptor the method's descriptor (see {@link Type}).
     * @param signature the method's signature. May be {@literal null} if the method parameters,
     *     return type and exceptions do not use generic types.
     * @param exceptions the internal names of the method's exception classes (see {@link
     *     Type#getInternalName()}). May be {@literal null}.
     * @return an object to visit the byte code of the method, or {@literal null} if this class
     *     visitor is not interested in visiting the code of this method.
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions){
        int ci = currentIndexGetAndInc();
        var method = //new CMethod<>(classFactory.methodList(),access,name,descriptor,signature,exceptions);
            classFactory.cmethod(access, name, descriptor, signature, exceptions);

        MethodDump dump = new MethodDump(api);
        dump.byteCode(b -> {
            if( byteCodeConsumer!=null )byteCodeConsumer.accept(b);
            if( b instanceof MethodByteCode ){
                method.getMethodByteCodes().add((MethodByteCode) b);
            }
        });

        emit(method);

        currentClass( x -> x.order(method,ci).getMethods().add(method) );

        return dump;
    }

    @Override
    public void visitEnd(){
        emit(new CEnd());
    }
}
