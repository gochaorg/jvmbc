package xyz.cofe.jvmbc;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.objectweb.asm.*;
import xyz.cofe.jvmbc.cls.*;
import xyz.cofe.jvmbc.fld.FieldByteCode;
import xyz.cofe.jvmbc.mth.MethodByteCode;
import xyz.cofe.jvmbc.rec.RecordByteCode;

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

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
        var c = classFactory.cbegin(version,access,name,signature,superName,interfaces);
        currentClass(c);
        currentIndex(0);
        emit(c);
    }

    @Override
    public void visitSource(String source, String debug){
        int ci = currentIndexGetAndInc();

        var c = new CSource(source,debug);
        currentClass(x -> {
            x.setSource(c);
            x.getOrder().put(c,ci);
        });

        emit(c);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version){
        dump("module name="+name+" access="+(new AccFlags(access).flags())+" version="+version);
        return super.visitModule(name, access, version);
    }

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

    @Override
    public void visitNestMember(String nestMember){
        int ci = currentIndexGetAndInc();
        var c = new CNestMember(nestMember);

        currentClass( x -> x.order(c,ci).getNestMembers().add(c) );

        emit(c);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass){
        int ci = currentIndexGetAndInc();
        var c = new CPermittedSubclass(permittedSubclass);

        currentClass( x -> x.order(c,ci).setPermittedSubclass(c) );

        emit(c);
    }

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
