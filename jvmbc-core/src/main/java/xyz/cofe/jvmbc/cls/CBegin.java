package xyz.cofe.jvmbc.cls;

import java.io.IOError;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.jvmbc.*;
import xyz.cofe.jvmbc.io.IOFun;
import xyz.cofe.jvmbc.mth.MethodByteCode;

/**
 * Описывает класс / модуль
 */
public class CBegin<
    CFIELD extends CField,
    CMETHOD extends CMethod<CM_LIST>,
    CM_LIST extends List<MethodByteCode>
> implements ClsByteCode, ClazzWriter, AccFlagsProperty, ClassFlags {
    /**
     * Идентификатор версии при сериализации/де-сериализации
     */
    private static final long serialVersionUID = 1;

    /**
     * Конструктор по умолчанию
     */
    public CBegin(){}

    /**
     * Конструктор
     * @param version версия байт-кода {@link #version}
     * @param access флаги доступа
     * @param name имя (байт-код) класса, см {@link JavaClassName}
     * @param signature сигнатура, в случае Generic типа
     * @param superName имя (байт-код) класса родителя
     * @param interfaces имена (байт-код) интерфейсов
     */
    public CBegin(int version, int access, String name, String signature, String superName, String[] interfaces){
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature!=null ? Optional.of(new Sign(signature)) : Optional.empty();
        this.superName = superName;
        this.interfaces = interfaces!=null ? new ArrayList<>(Arrays.asList(interfaces)) : new ArrayList<>();
    }

    private static class Clones {
        public final HashMap<ClsByteCode,ClsByteCode> clones = new HashMap<ClsByteCode,ClsByteCode>();
        public <A extends ClsByteCode> A clone(A sample){
            if( sample!=null ){
                var clone = (A)sample.clone();
                clones.put(sample, clone);
                return clone;
            }
            return sample;
        }
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public CBegin(CBegin<CFIELD,CMETHOD,CM_LIST> sample){
        if( sample==null )throw new IllegalArgumentException("sample==null");
        version = sample.getVersion();
        access = sample.getAccess();
        name = sample.getName();
        signature = sample.getSignature();
        superName = sample.getSuperName();
        if( sample.interfaces!=null )
            interfaces = new ArrayList<>(sample.interfaces);

        source = sample.source!=null && sample.source.isPresent()
            ? sample.source.map(CSource::clone)
            : Optional.empty();
        outerClass = sample.outerClass!=null ? sample.outerClass.clone() : null;
        nestHost = sample.nestHost!=null ? sample.nestHost.clone() : null;
        permittedSubclass = sample.permittedSubclass!=null ? sample.permittedSubclass.clone() : null;

        var clones = new Clones();

        if( sample.annotations!=null ){
            annotations = new ArrayList<>();
            for( var a : sample.annotations ){
                annotations.add( clones.clone(a) );
            }
        }

        if( sample.typeAnnotations!=null ){
            typeAnnotations = new ArrayList<>();
            for( var a : sample.typeAnnotations ){
                typeAnnotations.add( clones.clone(a) );
            }
        }

        if( sample.nestMembers!=null ){
            nestMembers = new ArrayList<>();
            for( var a : sample.nestMembers ){
                nestMembers.add( clones.clone(a) );
            }
        }

        if( sample.innerClasses!=null ){
            innerClasses = new ArrayList<>();
            for( var a : sample.innerClasses ){
                innerClasses.add( clones.clone(a) );
            }
        }

        if( sample.fields!=null ){
            fields = new ArrayList<>();
            for( var a : sample.fields ){
                fields.add( clones.clone(a) );
            }
        }

        if( sample.methods!=null ){
            methods = new ArrayList<>();
            for( var a : sample.methods ){
                methods.add( clones.clone(a) );
            }
        }

        if( sample.order!=null ){
            order = new LinkedHashMap<>();
            sample.order.forEach( (sampleBc, idx) -> {
                var cloned = clones.clones.get(sampleBc);
                if( cloned!=null ){
                    order.put(cloned, idx);
                }
            });
        }
    }

    /**
     * Клонирование поля класса
     * @param field поле класса
     * @return клон
     */
    public CFIELD clone(CFIELD field){
        if( field==null )throw new IllegalArgumentException( "field==null" );
        //noinspection unchecked
        return (CFIELD) field.clone();
    }

    public CMETHOD clone(CMETHOD method){
        if( method==null )throw new IllegalArgumentException( "method==null" );
        //noinspection unchecked
        return (CMETHOD) method.clone();
    }

    /**
     * Создает полную копию класса
     * @return копия
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CBegin<CFIELD,CMETHOD,CM_LIST> clone(){
        return new CBegin<>(this);
    }

    //region version : int - версия байт-кода
    /** версия байт-кода
     * <ul>
     *     <li>45 - Java 1.0</li>
     *     <li>45.3 - Java 1.1</li>
     *     <li>46 - Java 1.2</li>
     *     <li>47 - Java 1.3</li>
     *     <li>48 - Java 1.4</li>
     *     <li>49 - Java 5</li>
     *     <li>50 - Java 6</li>
     *     <li>51 - Java 7</li>
     *     <li>52 - Java 8</li>
     *     <li>53 - Java 9</li>
     *     <li>54 - Java 10</li>
     *     <li>55 - Java 11</li>
     *     <li>56 - Java 12</li>
     *     <li>57 - Java 13</li>
     *     <li>58 - Java 14</li>
     *     <li>59 - Java 15</li>
     *     <li>60 - Java 16</li>
     *     <li>61 - Java 17</li>
     *     <li>62 - Java 18</li>
     * </ul>
     */
    protected int version;

    /**
     * Возвращает версию байт-кода совместимую с JVM {@link #version}
     * @return версия совместимая JVM
     */
    public int getVersion(){
        return version;
    }

    /**
     * Указывает версию байт-кода совместимую с JVM {@link #version}
     * @param version версия совместимая JVM
     */
    public void setVersion(int version){
        this.version = version;
    }
    //endregion
    //region access : int - флаги доступа к классу
    /**
     * флаги доступа к классу {@link AccFlags}
     */
    protected int access;

    /**
     * Возвращает флаги доступа к классу {@link AccFlags}
     * @return флаги доступа
     */
    public int getAccess(){
        return access;
    }

    /**
     * Указывает флаги доступа к классу {@link AccFlags}
     * @param access флаги доступа
     */
    public void setAccess(int access){
        this.access = access;
    }
    //endregion
    //region name : String - имя класса
    protected String name;

    /**
     * Возвращает имя (байт-код) класса, см {@link #javaName()}, {@link JavaClassName}
     * @return имя класса
     */
    public String getName(){
        return name;
    }

    /**
     * Указывает имя (байт-код) класса, см {@link #javaName()}, {@link JavaClassName}
     * @param name имя класса
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Манипуляции с именем класса
     */
    public static class JavaNamed<
        CFIELD extends CField,
        CMETHOD extends CMethod<CM_LIST>,
        CM_LIST extends List<MethodByteCode>
        > {
        public final CBegin<CFIELD,CMETHOD,CM_LIST> cBegin;
        public JavaNamed(CBegin<CFIELD,CMETHOD,CM_LIST> cBegin){
            if( cBegin==null )throw new IllegalArgumentException( "cBegin==null" );
            this.cBegin = cBegin;
        }

        //region name : String

        /**
         * Возвращает полное имя класса
         * @return полное имя класса, например <code>java.lang.String</code>
         */
        public String getName(){
            var n = cBegin.getName();
            if( n==null )return null;
            return new JavaClassName(n).name;
        }

        /**
         * Указывает полное имя класса
         * @param name полное имя класса, например <code>java.lang.String</code>
         */
        public void setName(String name){
            if( name==null ){
                cBegin.setName( null );
            }else {
                cBegin.setName( new JavaClassName(name).rawName() );
            }
        }
        //endregion
        //region simpleName : String - простое имя класса
        /**
         * Возвращает простое имя класса, например <code>String</code>
         * @return простое имя класса
         */
        public String getSimpleName(){
            var n = cBegin.getName();
            if( n==null )return null;

            return new JavaClassName(n).simpleName;
        }

        /**
         * Указывает простое имя класса, например <code>String</code>
         * @param name простое имя класса
         */
        public void setSimpleName( String name ){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            if( !JavaClassName.validId.matcher(name).matches() ){
                throw new IllegalArgumentException("name not match "+ JavaClassName.validId );
            }

            var curName = cBegin.getName();
            if( curName==null ){
                cBegin.setName(name);
                return;
            }

            cBegin.setName( new JavaClassName(curName).withSimpleName(name).rawName() );
        }
        //endregion
        //region package : String - имя пакета
        /**
         * Возвращает имя пакета содержащий класс, например <code>java.lang</code>
         * @return имя пакета
         */
        public String getPackage(){
            var n = cBegin.getName();
            if( n==null )return "";

            return new JavaClassName(n).packageName;
        }

        /**
         * Возвращает имя пакета содержащий класс, например <code>java.lang</code>
         * @param name имя пакета
         */
        public void setPackage(String name){
            if( name==null || name.length()<1 ){
                var n = cBegin.getName();
                if( n!=null ){
                    cBegin.setName( new JavaClassName(n).withPackage("").rawName() );
                }
            }else {
                var n = cBegin.getName();
                //noinspection ReplaceNullCheck
                if( n==null ){
                    cBegin.setName( new JavaClassName("Class0").withPackage(name).rawName() );
                }else{
                    cBegin.setName( new JavaClassName(n).withPackage(name).rawName() );
                }
            }
        }
        //endregion

        public String toString(){
            var n = cBegin.getName();
            if( n==null )return "null?";

            return new JavaClassName(n).name;
        }
    }

    /**
     * Изменение имени класса, меняет содержимое поля {@link #name}
     * @return управление именем класса
     */
    public JavaNamed<CFIELD,CMETHOD,CM_LIST> javaName(){
        return new JavaNamed<>(this);
    }
    //endregion
    //region signature : String - сигнатура generic или null
    /**
     * Сигнатура в случае Generic класса/интерфейса или null
     */
    protected Optional<Sign> signature = Optional.empty();

    /**
     * Возвращает сигнатуру generic
     * @return сигнатура или null
     */
    public Optional<Sign> getSignature(){
        return signature;
    }

    /**
     * Указывает сигнатуру generic
     * @param signature сигнатура или null
     */
    public void setSignature(Optional<Sign> signature){
        //noinspection OptionalAssignedToNull
        if( signature==null )throw new IllegalArgumentException( "signature==null" );
        this.signature = signature;
    }
    //endregion
    //region superName : String - имя (байт-код) класса родителя
    /**
     * Имя (байт-код) класса родителя {@link JavaClassName}
     */
    protected String superName;

    /**
     * Возвращает имя (байт-код) класса родителя {@link JavaClassName}
     * @return имя (байт-код) класса родителя
     */
    public String getSuperName(){
        return superName;
    }

    /**
     * Указывает имя (байт-код) класса родителя {@link JavaClassName}
     * @param superName имя (байт-код) класса родителя
     */
    public void setSuperName(String superName){
        this.superName = superName;
    }
    //endregion
    //region interfaces : String[] - имена (байт-код) интерфейсов
    /**
     * имена (байт-код) интерфейсов, см {@link JavaClassName}
     */
    protected List<String> interfaces;

    /**
     * Возвращает имена (байт-код) интерфейсов, см {@link JavaClassName}
     * @return имена (байт-код) интерфейсов
     */
    public List<String> getInterfaces(){
        if( interfaces==null )interfaces = new ArrayList<>();
        return interfaces;
    }

    /**
     * Указывает имена (байт-код) интерфейсов, см {@link JavaClassName}
     * @param interfaces имена (байт-код) интерфейсов
     */
    public void setInterfaces(List<String> interfaces){
        if( interfaces==null )throw new IllegalArgumentException( "interfaces==null" );
        this.interfaces = interfaces;
    }
    //endregion

    //region source : CSource - имя исходного файла
    /**
     * Содержит имя исходного класса/файла отладки (debug)
     */
    protected Optional<CSource> source = Optional.empty();

    /**
     * Возвращает имя исходного файла
     * @return имя исходного файла
     */
    public Optional<CSource> getSource(){ return source; }

    /**
     * Указывает имя исходного файла
     * @param s имя исходного файла
     */
    public void setSource(Optional<CSource> s){ source = s; }
    //endregion
    //region outerClass : COuterClass
    protected COuterClass outerClass;
    public COuterClass getOuterClass(){ return outerClass; }
    public void setOuterClass(COuterClass s){ outerClass = s; }
    //endregion
    //region nestHost : CNestHost
    protected CNestHost nestHost;
    public CNestHost getNestHost(){ return nestHost; }
    public void setNestHost(CNestHost s){ nestHost = s; }
    //endregion
    //region permittedSubclass : CPermittedSubclass
    protected CPermittedSubclass permittedSubclass;
    public CPermittedSubclass getPermittedSubclass(){ return permittedSubclass; }
    public void setPermittedSubclass(CPermittedSubclass s){ permittedSubclass = s; }
    //endregion

    //region module : Optional<CModule>
    protected Optional<CModule> module = Optional.empty();

    public Optional<CModule> getModule(){
        return module;
    }

    public void setModule( Optional<CModule> module ){
        this.module = module;
    }
    //endregion

    //region annotations : List<CAnnotation> - аннотации прикрепленные к классу
    /**
     * аннотации прикрепленные к классу
     */
    protected List<CAnnotation> annotations;

    /**
     * Возвращает аннотации прикрепленные к классу
     * @return аннотации
     */
    public List<CAnnotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }

    /**
     * Указывает аннотации прикрепленные к классу
     * @param ls аннотации
     */
    public void setAnnotations(List<CAnnotation> ls){
        annotations = ls;
    }
    //endregion
    //region typeAnnotations : List<CTypeAnnotation> - аннотации прикрепленные к классу
    /**
     * аннотации прикрепленные к классу
     */
    protected List<CTypeAnnotation> typeAnnotations;

    /**
     * Возвращает аннотации прикрепленные к классу
     * @return аннотации
     */
    public List<CTypeAnnotation> getTypeAnnotations(){
        if( typeAnnotations==null )typeAnnotations = new ArrayList<>();
        return typeAnnotations;
    }

    /**
     * Указывает аннотации прикрепленные к классу
     * @param ls аннотации
     */
    public void setTypeAnnotations(List<CTypeAnnotation> ls){
        typeAnnotations = ls;
    }
    //endregion

    // protected List visitAttribute

    //region nestMembers : List<CNestMember>
    protected List<CNestMember> nestMembers;
    public List<CNestMember> getNestMembers(){
        if( nestMembers==null )nestMembers = new ArrayList<>();
        return nestMembers;
    }
    public void setNestMembers(List<CNestMember> ls){
        nestMembers = ls;
    }
    //endregion
    //region innerClasses : List<CInnerClass>
    protected List<CInnerClass> innerClasses;
    public List<CInnerClass> getInnerClasses(){
        if( innerClasses==null )innerClasses = new ArrayList<>();
        return innerClasses;
    }
    public void setInnerClasses(List<CInnerClass> ls){
        innerClasses = ls;
    }
    //endregion

    //region records : List<CRecord>
    protected List<CRecord> records;
    public List<CRecord> getRecords(){
        if( records!=null )return records;
        records = new ArrayList<>();
        return records;
    }
    public void setRecords(List<CRecord> records){
        this.records = records;
    }
    //endregion

    //region fields : List<CFIELD> - Список полней класса
    /**
     * Список полней класса
     */
    protected List<CFIELD> fields;

    /**
     * Возвращает список полней класса
     * @return список полней класса
     */
    public List<CFIELD> getFields(){
        if( fields==null )fields = new ArrayList<>();
        return fields;
    }

    /**
     * Указывает список полней класса
     * @param fields список полней класса
     */
    public void setFields(List<CFIELD> fields){
        this.fields = fields;
    }
    //endregion
    //region methods : List<CMethod> - список методов класса
    /**
     * Список методов класса
     */
    protected List<CMETHOD> methods;

    /**
     * Возвращает список методов класса
     * @return список методов класса
     */
    public List<CMETHOD> getMethods(){
        if( methods==null )methods = new ArrayList<>();
        return methods;
    }

    /**
     * Указывает список методов класса
     * @param methods список методов класса
     */
    public void setMethods(List<CMETHOD> methods){
        this.methods = methods;
    }
    //endregion
    //region order : Map<ClsByteCode,Integer>
    /**
     * Содержит порядок определения полей/методов/.. в классе (байт-коде)
     */
    protected Map<ClsByteCode,Integer> order;

    /**
     * Возвращает порядок определения полей/методов/.. в классе (байт-коде)
     * @return порядок определения полей/методов/..
     */
    public Map<ClsByteCode,Integer> getOrder(){
        if( order==null )order = new LinkedHashMap<>();
        return order;
    }

    /**
     * Указывает  порядок определения полей/методов/.. в классе (байт-коде)
     * @param order порядок определения полей/методов/..
     */
    public void setOrder(Map<ClsByteCode,Integer> order){
        this.order = order;
    }

    /**
     * Указывает порядок определения полей/методов/..
     * @param c поле/методо/...
     * @param order порядок
     * @return SELF ссылка
     */
    public CBegin<CFIELD, CMETHOD, CM_LIST> order(ClsByteCode c, int order){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        getOrder().put(c,order);
        return this;
    }
    //endregion

    @Override
    public String toString(){
        return this.getClass().getSimpleName()+" " +
            "version=" + version +
            " access="+AccFlag.flags(access, AccFlag.Scope.CLASS)+"#"+access+
            " name=" + name +
            " signature=" + signature +
            " superName=" + superName +
            " interfaces=" + interfaces +
            "";
    }

    /**
     * Возвращает дочерние узлы
     * @return дочерние узлы
     */
    @Override
    public List<ByteCode> nodes(){
        ArrayList<ByteCode> r = new ArrayList<>();
        if( source!=null && source.isPresent() )r.add(source.get());
        if( outerClass!=null )r.add(outerClass);
        if( nestHost!=null )r.add(nestHost);
        if( permittedSubclass!=null )r.add(permittedSubclass);

        if( annotations!=null && !annotations.isEmpty() ) {
            r.addAll( annotations );
        }

        if( typeAnnotations!=null && !typeAnnotations.isEmpty() ){
            r.addAll( typeAnnotations );
        }
        if( nestMembers!=null && !nestMembers.isEmpty() ) {
            r.addAll( nestMembers );
        }

        if( innerClasses!=null && !innerClasses.isEmpty() ) {
            r.addAll( innerClasses );
        }

        if( fields!=null && !fields.isEmpty() ) {
            r.addAll( fields );
        }

        if( methods!=null && !methods.isEmpty() ) {
            r.addAll( methods );
        }

        return r.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    //region toByteCode(), parseByteCode()

    /**
     * Генерация байт кода
     * @param v куда будет записан байт код
     */
    @Override
    public void write( ClassWriter v ){
        if( v==null )throw new IllegalArgumentException( "v==null" );

        var itfs = getInterfaces();
        v.visit( getVersion(), getAccess(), getName(), getSignature().map(Sign::getRaw).orElse(null), getSuperName(),
            itfs!=null && !itfs.isEmpty() ? itfs.toArray(new String[0]) : null
        );

        var src = source;
        //noinspection OptionalAssignedToNull
        if( src!=null )src.ifPresent( _src -> _src.write(v));

        var nh = nestHost;
        if( nh!=null )nh.write(v);

        var pss = permittedSubclass;
        if( pss!=null )pss.write(v);

        var oc = outerClass;
        if( oc!=null )oc.write(v);

        List<ClsByteCode> anns = new ArrayList<>();
        if( annotations!=null )anns.addAll(annotations);
        if( typeAnnotations!=null )anns.addAll(typeAnnotations);
        anns.sort( (a,b)->{
            int o1 = getOrder().getOrDefault(a,-1);
            int o2 = getOrder().getOrDefault(b,-1);
            if( o1==o2 )return a.toString().compareTo(b.toString());
            return Integer.compare(o1,o2);
        });

        for( var ann : anns ){
            ann.write(v);
        }

        List<ClsByteCode> body = new ArrayList<>();
        if( fields!=null )body.addAll(fields);
        if( methods!=null )body.addAll(methods);
        if( nestMembers!=null )body.addAll(nestMembers);
        if( innerClasses!=null )body.addAll(innerClasses);
        if( records!=null )body.addAll(records);
        //noinspection OptionalAssignedToNull
        if( module!=null )module.ifPresent(body::add);

        body.sort( (a,b)->{
            int o1 = getOrder().getOrDefault(a,-1);
            int o2 = getOrder().getOrDefault(b,-1);
            if( o1==o2 )return a.toString().compareTo(b.toString());
            return Integer.compare(o1,o2);
        });

        for( var b : body ){
            b.write(v);
        }

        v.visitEnd();
    }

    /**
     * Возвращает байт-код.
     * <br>
     * Класс будет сгенерирован с использованием таких флагов
     * <code>
     * new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES)
     * </code>
     * @return байт-код
     */
    public byte[] toByteCode(){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        write(cw);
        return cw.toByteArray();
    }

    /**
     * Парсинг байт-кода
     * @param byteCode байт-код
     * @return представление класса
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static CBegin<CField,CMethod<List<MethodByteCode>>,List<MethodByteCode>> parseByteCode(byte[] byteCode){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );

        ClassReader classReader = new ClassReader(byteCode);
        List<ByteCode> byteCodes = new ArrayList<>();

        var dump = ClassDump.create();
        dump.byteCode( byteCodes::add );
        classReader.accept(dump,0);

        //noinspection unchecked,rawtypes
        return byteCodes.stream().filter( b -> b instanceof CBegin )
            .map( b -> (CBegin)b ).findFirst().get();
    }

    /**
     * Парсинг байт-кода класса.
     * <ul>
     *     <li>Будет произведен поиск класса в ресурсах
     *
     *     <br>
     *     <code>var resName = "/"+clazz.getName().replace(".","/")+".class"</code>
     *
     *     <br>
     *     <code>
     *          clazz.getResource(resName)
     *     </code>
     *     </li>
     *     <li>Если ресурс будет найдет, то быдет вызов {@link #parseByteCode(URL)}</li>
     * </ul>
     * @param clazz класс
     * @return представление байт кода
     */
    public static CBegin<CField,CMethod<List<MethodByteCode>>,List<MethodByteCode>> parseByteCode(Class<?> clazz){
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );

        var resName = "/"+clazz.getName().replace(".","/")+".class";
        var classUrl = clazz.getResource(resName);

        if( classUrl==null )throw new IOError(
            new IOException("resource "+resName+" not found")
        );

        return parseByteCode(classUrl);
    }

    /**
     * Парсинг байт-кода
     * @param url ссылка на байт-код
     * @return представление класса
     */
    public static CBegin<CField,CMethod<List<MethodByteCode>>,List<MethodByteCode>> parseByteCode(URL url){
        if( url==null )throw new IllegalArgumentException( "url==null" );
        try{
            return parseByteCode(IOFun.readBytes(url));
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    /**
     * Парсинг байт-кода
     * @param  byteCode байт-код
     * @return представление класса
     */
    public static
    <
        CBEGIN extends CBegin<CFIELD,CMETHOD,CM_LIST>,
        CFIELD extends CField,
        CMETHOD extends CMethod<CM_LIST>,
        CM_LIST extends List<MethodByteCode>
    > CBEGIN parseByteCode(byte[] byteCode, ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> factory){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        if( factory==null )throw new IllegalArgumentException( "factory==null" );

        ClassReader classReader = new ClassReader(byteCode);
        List<ByteCode> byteCodes = new ArrayList<>();

        var dump =
            new ClassDump<>(factory);

        dump.byteCode( byteCodes::add );
        classReader.accept(dump,0);

        //noinspection unchecked,OptionalGetWithoutIsPresent
        return byteCodes.stream().filter( b -> b instanceof CBegin )
            .map( b -> (CBEGIN)b ).findFirst().get();
    }

    /**
     * Парсинг байт-кода
     * @param clazz ссылка на байт-код
     * @return представление класса
     */
    public static
    <
        CBEGIN extends CBegin<CFIELD,CMETHOD,CM_LIST>,
        CFIELD extends CField,
        CMETHOD extends CMethod<CM_LIST>,
        CM_LIST extends List<MethodByteCode>
        > CBEGIN
    parseByteCode(Class<?> clazz, ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> factory){
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );
        if( factory==null )throw new IllegalArgumentException( "factory==null" );

        var resName = "/"+clazz.getName().replace(".","/")+".class";
        var classUrl = clazz.getResource(resName);

        if( classUrl==null )throw new IOError(
            new IOException("resource "+resName+" not found")
        );

        return parseByteCode(classUrl, factory);
    }

    /**
     * Парсинг байт-кода
     * @param url ссылка на байт-код
     * @return представление класса
     */
    public static <
        CBEGIN extends CBegin<CFIELD,CMETHOD,CM_LIST>,
        CFIELD extends CField,
        CMETHOD extends CMethod<CM_LIST>,
        CM_LIST extends List<MethodByteCode>
        > CBEGIN
    parseByteCode(URL url, ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> factory){
        if( url==null )throw new IllegalArgumentException( "url==null" );
        try{
            return parseByteCode(IOFun.readBytes(url), factory);
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }
    //endregion
}
