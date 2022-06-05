package xyz.cofe.jvmbc.mdl;

import org.objectweb.asm.ModuleVisitor;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.mdl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Modulo implements ModuleByteCode {
    public Modulo(){}
    public Modulo(Modulo sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        mainClass = sample.getMainClass().map(MdlMainClass::clone);
        packages = sample.getPackages().stream().map(MdlPackage::clone).collect(Collectors.toList());
        requires = sample.getRequires().stream().map(MdlRequire::clone).collect(Collectors.toList());
        exports = sample.getExports().stream().map(MdlExport::clone).collect(Collectors.toList());
        opens = sample.getOpens().stream().map(MdlOpen::clone).collect(Collectors.toList());
        uses = sample.getUses().stream().map(MdlUse::clone).collect(Collectors.toList());
        provides = sample.getProvides().stream().map(MdlProvide::clone).collect(Collectors.toList());
    }
    //region mainClass : Optional<MdlMainClass>
    protected Optional<MdlMainClass> mainClass = Optional.empty();
    public Optional<MdlMainClass> getMainClass(){
        return mainClass;
    }

    public void setMainClass( Optional<MdlMainClass> mainClass ){
        this.mainClass = mainClass;
    }
    //endregion
    //region packages : List<MdlPackage>
    protected List<MdlPackage> packages;
    public List<MdlPackage> getPackages(){
        if( packages==null )packages = new ArrayList<>();
        return packages;
    }

    public void setPackages( List<MdlPackage> packages ){
        this.packages = packages;
    }
    //endregion
    //region requires : List<MdlPackage>
    protected List<MdlRequire> requires;
    public List<MdlRequire> getRequires(){
        if( requires==null )requires = new ArrayList<>();
        return requires;
    }

    public void setRequires( List<MdlRequire> requires ){
        this.requires = requires;
    }
    //endregion
    //region exports : List<MdlExport>
    protected List<MdlExport> exports;
    public List<MdlExport> getExports(){
        if( exports==null )exports = new ArrayList<>();
        return exports;
    }

    public void setExports( List<MdlExport> exports ){
        this.exports = exports;
    }
    //endregion
    //region opens : List<MdlOpen>
    protected List<MdlOpen> opens;
    public List<MdlOpen> getOpens(){
        if( opens==null )opens = new ArrayList<>();
        return opens;
    }

    public void setOpens( List<MdlOpen> opens ){
        this.opens = opens;
    }
    //endregion
    //region uses : List<MdlUse>
    protected List<MdlUse> uses;
    public List<MdlUse> getUses(){
        if( uses==null )uses = new ArrayList<>();
        return uses;
    }

    public void setUses( List<MdlUse> uses ){
        this.uses = uses;
    }
    //endregion
    //region provides : List<MdlProvide>
    protected List<MdlProvide> provides;

    public List<MdlProvide> getProvides(){
        if( provides==null )provides = new ArrayList<>();
        return provides;
    }

    public void setProvides( List<MdlProvide> provides ){
        this.provides = provides;
    }
    //endregion


    /**
     * Возвращает дочерние узлы
     *
     * @return дочерние узлы
     */
    @Override
    public List<ByteCode> nodes(){
        List<ByteCode> nested = new ArrayList<>();
        //noinspection OptionalAssignedToNull
        if(mainClass!=null) mainClass.ifPresent(nested::add);
        if(packages!=null) nested.addAll(packages);
        if(requires!=null) nested.addAll(requires);
        if(exports!=null) nested.addAll(exports);
        if(opens!=null) nested.addAll(opens);
        if(uses!=null) nested.addAll(uses);
        if(provides!=null) nested.addAll(provides);
        return nested;
    }

    @Override
    public Modulo clone(){
        return new Modulo(this);
    }

    @Override
    public void write( ModuleVisitor v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );
        //noinspection OptionalAssignedToNull
        if(mainClass!=null) mainClass.ifPresent( x -> v.visitMainClass(x.getMainClass()) );
        if(packages!=null) packages.forEach( x -> v.visitPackage(x.getPackaze()));
        if(exports!=null) exports.forEach( x -> v.visitExport(x.getPackaze(), x.getAccess(), x.getModules().toArray(new String[0])));
        if(opens!=null) opens.forEach( x -> v.visitOpen(x.getPackaze(), x.getAccess(), x.getModules().toArray(new String[0])));
        if(uses!=null) uses.forEach( x -> v.visitUse(x.getService()));
        if(provides!=null) provides.forEach( x -> v.visitProvide(x.getService(), x.getProviders().toArray(new String[0])));

        v.visitEnd();
    }
}
