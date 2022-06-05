package xyz.cofe.jvmbc;

import org.objectweb.asm.ModuleVisitor;
import xyz.cofe.jvmbc.mdl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A visitor to visit a Java module. The methods of this class must be called in the following
 * order:
 * ( {@code visitMainClass} |
 *   ( {@code visitPackage} |
 *     {@code visitRequire} |
 *     {@code visitExport} |
 *     {@code visitOpen} |
 *     {@code visitUse} |
 *     {@code visitProvide}
 *   )*
 * )
 * {@code visitEnd}.
 */
public class ModuleDump extends ModuleVisitor {
    public ModuleDump( int api ){
        super(api);
    }

    protected Optional<MdlMainClass> mainClass = Optional.empty();
    protected List<MdlPackage> packages = new ArrayList<>();
    protected List<MdlRequire> requires = new ArrayList<>();
    protected List<MdlExport> exports = new ArrayList<>();
    protected List<MdlOpen> opens = new ArrayList<>();
    protected List<MdlUse> uses = new ArrayList<>();
    protected List<MdlProvide> provides = new ArrayList<>();

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public ModuleDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    protected void emit(ByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
    }

    /**
     * Visit the main class of the current module.
     *
     * @param mainClass the internal name of the main class of the current module.
     */
    @Override
    public void visitMainClass( String mainClass ){
        var c = new MdlMainClass(mainClass);
        this.mainClass = Optional.of(c);
        emit(c);
    }

    /**
     * Visit a package of the current module.
     *
     * @param packaze the internal name of a package.
     */
    @Override
    public void visitPackage( String packaze ){
        var c = new MdlPackage(packaze);
        packages.add(c);
        emit(c);
    }

    /**
     * Visits a dependence of the current module.
     *
     * @param module  the fully qualified name (using dots) of the dependence.
     * @param access  the access flag of the dependence among {@code ACC_TRANSITIVE}, {@code
     *                ACC_STATIC_PHASE}, {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     * @param version the module version at compile time, or {@literal null}.
     */
    @Override
    public void visitRequire( String module, int access, String version ){
        var c = new MdlRequire(module,access,version);
        requires.add(c);
        emit(c);
    }

    /**
     * Visit an exported package of the current module.
     *
     * @param packaze the internal name of the exported package.
     * @param access  the access flag of the exported package, valid values are among {@code
     *                ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     * @param modules the fully qualified names (using dots) of the modules that can access the public
     *                classes of the exported package, or {@literal null}.
     */
    @Override
    public void visitExport( String packaze, int access, String... modules ){
        var c = new MdlExport(packaze, access, modules);
        exports.add(c);
        emit(c);
    }

    /**
     * Visit an open package of the current module.
     *
     * @param packaze the internal name of the opened package.
     * @param access  the access flag of the opened package, valid values are among {@code
     *                ACC_SYNTHETIC} and {@code ACC_MANDATED}.
     * @param modules the fully qualified names (using dots) of the modules that can use deep
     *                reflection to the classes of the open package, or {@literal null}.
     */
    @Override
    public void visitOpen( String packaze, int access, String... modules ){
        var c = new MdlOpen(packaze, access, modules);
        opens.add(c);
        emit(c);
    }

    /**
     * Visit a service used by the current module. The name must be the internal name of an interface
     * or a class.
     *
     * @param service the internal name of the service.
     */
    @Override
    public void visitUse( String service ){
        var c = new MdlUse(service);
        uses.add(c);
        emit(c);
    }

    /**
     * Visit an implementation of a service.
     *
     * @param service   the internal name of the service.
     * @param providers the internal names of the implementations of the service (there is at least
     */
    @Override
    public void visitProvide( String service, String... providers ){
        var c = new MdlProvide(service,providers);
        provides.add(c);
        emit(c);
    }

    protected Modulo newModulo(){
        return new Modulo();
    }

    /**
     * Visits the end of the module. This method, which is the last one to be called, is used to
     * inform the visitor that everything have been visited.
     */
    @Override
    public void visitEnd(){
        var c = new MdlEnd();
        var mod = newModulo();
        mod.setMainClass(mainClass);
        mod.setPackages(packages);
        mod.setRequires(requires);
        mod.setExports(exports);
        mod.setOpens(opens);
        mod.setUses(uses);
        mod.setProvides(provides);
        emit(c);
        emit(mod);
    }
}
