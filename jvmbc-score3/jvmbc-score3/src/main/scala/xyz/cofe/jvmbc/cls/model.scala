package xyz.cofe.jvmbc
package cls

import ann.AnnCode
import mth.MethCode
import mdl.ModuleCode
import mdl.Modulo
import rec.RecordCode
import xyz.cofe.jvmbc.fld.FieldCode
import xyz.cofe.jvmbc.parse.desc.{ObjectType => JavaName}
import xyz.cofe.jvmbc.parse.desc.{Method => MDesc}

/** Байт-код `.class` файла */
sealed trait ClassCode extends ByteCode

/** аннотации прикрепленные к классу */
case class CAnnotation(
  desc:TDesc,
  visible:Boolean,
  annotations:Seq[AnnCode]
) extends ClassCode with NestedThey("annotations")

/** аннотации прикрепленные к классу */
case class CTypeAnnotation(
  typeRef:CTypeRef,
  typePath:Option[String],
  desc:TDesc,
  visible:Boolean,
  annotations:Seq[AnnCode]
) extends ClassCode 

/**
 * имя исходного класса/файла отладки (debug)
 */
case class CSource(source:Option[String],debug:Option[String]) 
  extends ClassCode

/** 
 * Модуль приложения
 * @param name имя модуля
 * @param access Доступ
 * @param version Версия
 */
case class CModule(
  name:String,
  access:CModuleAccess,
  version:Option[String],
  body:Modulo
) 
  extends ClassCode 
  with NestedThey("body")

/** 
 * Доступ модуля
 * @param raw флаги доступ
 */
case class CModuleAccess(raw:Int) extends AnyVal

case class CPermittedSubclass(permittedSubclass:String) 
  extends ClassCode

case class COuterClass(owner:String,name:Option[String],desc:Option[TDesc]) 
  extends ClassCode

case class CNestMember(nestMember:String) extends ClassCode
case class CNestHost(nestHost:String) extends ClassCode

/**
 * Описывает поле класса
 * 
 * Пример
 * 
 *    public interface Desc {
 *        String value();
 *    }
 *    
 *    {@} Desc("sample User2")
 *    public class User2 {
 *        public User2(){}
 *        public User2(String name1){
 *            this.name = name1;
 *        }
 *    
 *        {@} Desc("name of user")
 *        private String name;
 *    
 *        {@} Desc("name of user")
 *        public String getName(){ return name; }
 *        public void setName( {@} Required {@} MaxLength(100) {@} MinLength(1) String name ){ this.name = name; }
 *    
 *        private List<String> emails;
 *        {@} Desc("emails of user")
 *        public List<String> getEmails(){ return emails; }
 *        public void setEmails( List<String> emails ){ this.emails = emails; }
 *    }
 * 
 *  Будет описываться как:
 * 
 *     CBegin version="55" ...
 *     CField
 *       name="name"
 *       access="2"
 *       accessDecode="[Private]"
 *       descriptor="Ljava/lang/String;"
 */
case class CField(
  access:CFieldAccess,
  name:String,
  desc:TDesc,
  sign:Option[Sign],
  value:FieldInitValue,
  body:Seq[FieldCode]
) extends ClassCode 
  with NestedThey("body")

case class CFieldAccess(raw:Int) extends AnyVal
enum FieldInitValue:
  case NULL
  case IntV(value:Int)
  case FloatV(value:Float)
  case LongV(value:Long)
  case DoubleV(value:Double)
  case StringV(value:String)
  case SerializableV(value:Serializable)
  case Undef

/** Описывает метод класса */
case class CMethod(
  access:CMethodAccess,
  name:String,
  desc:MDesc,
  sign:Option[MSign],
  exceptions:Seq[String],
  body:Seq[MethCode]
) extends ClassCode 
  with NestedThey("body")

case class CMethodAccess(raw:Int) extends AnyVal:
  def `public`:Boolean       = MethodAccessFlag.ACC_PUBLIC.isSet(raw)
  def `private`:Boolean      = MethodAccessFlag.ACC_PRIVATE.isSet(raw)
  def `protected`:Boolean    = MethodAccessFlag.ACC_PROTECTED.isSet(raw)
  def `static`:Boolean       = MethodAccessFlag.ACC_STATIC.isSet(raw)
  def `final`:Boolean        = MethodAccessFlag.ACC_FINAL.isSet(raw)
  def `synchronized`:Boolean = MethodAccessFlag.ACC_SYNCHRONIZED.isSet(raw)
  def bridge:Boolean         = MethodAccessFlag.ACC_BRIDGE.isSet(raw)
  def varargs:Boolean        = MethodAccessFlag.ACC_VARARGS.isSet(raw)
  def native:Boolean         = MethodAccessFlag.ACC_NATIVE.isSet(raw)
  def strict:Boolean         = MethodAccessFlag.ACC_STRICT.isSet(raw)
  def synthetic:Boolean      = MethodAccessFlag.ACC_SYNTHETIC.isSet(raw)
  def `abstract`:Boolean     = MethodAccessFlag.ACC_ABSTRACT.isSet(raw)

object CMethodAccess:
  def build:Builder = Builder()
  case class Builder():
    def statico = StaticBuilder(Set(MethodAccessFlag.ACC_STATIC))
    def virtual = VirtualBuilder(Set())

  trait AccessBld[T]( set:Set[MethodAccessFlag]=>T ):
    def publico = set(Set(MethodAccessFlag.ACC_PUBLIC))
    def privato = set(Set(MethodAccessFlag.ACC_PRIVATE))
    def protecto = set(Set(MethodAccessFlag.ACC_PROTECTED))
    def packaje = set(Set.empty)

  trait AbstractBld[T]( set:Set[MethodAccessFlag]=>T ):
    def abstracto = set(Set(MethodAccessFlag.ACC_ABSTRACT))

  trait NativeBld[T]( set:Set[MethodAccessFlag]=>T ):
    def native = set(Set(MethodAccessFlag.ACC_NATIVE))

  trait FinalBld[T]( set:Set[MethodAccessFlag]=>T ):
    def finalo = set(Set(MethodAccessFlag.ACC_FINAL))

  trait OptBld[T]( set:Set[MethodAccessFlag]=>T ):
    def synchro = set(Set(MethodAccessFlag.ACC_SYNCHRONIZED))
    def bridge = set(Set(MethodAccessFlag.ACC_BRIDGE))
    def varargs = set(Set(MethodAccessFlag.ACC_VARARGS))
    def strict = set(Set(MethodAccessFlag.ACC_STRICT))
    def synthetic = set(Set(MethodAccessFlag.ACC_SYNTHETIC))

  trait BuildIt:
    def flags:Set[MethodAccessFlag]
    def build:CMethodAccess =
      CMethodAccess( flags.foldLeft(0){ case (f,ff) => f | ff.bitMask } )

  case class StaticBuilder(flags:Set[MethodAccessFlag]) 
    extends AccessBld[StaticAccBuilder]( addFlags => StaticAccBuilder(flags ++ addFlags) )

  case class StaticAccBuilder(flags:Set[MethodAccessFlag])
    extends OptBld[StaticAccBuilder]( addFlags => StaticAccBuilder(flags ++ addFlags) )
    with BuildIt

  case class VirtualBuilder(flags:Set[MethodAccessFlag])
    extends AccessBld[VirtualAccBuilder]( af => VirtualAccBuilder(flags ++ af) )

  case class VirtualAccBuilder(flags:Set[MethodAccessFlag])
    extends OptBld[VirtualAccBuilder]( af => VirtualAccBuilder(af ++ flags) )
    with BuildIt

case class MSign(raw:String) extends AnyVal

case class CInnerClass(
  access:CInnerClassAccess,
  name:String,
  outerName:Option[String],
  innerName:Option[String]
) extends ClassCode

case class CInnerClassAccess(raw:Int) extends AnyVal

/** 
 * Record класс
 */
case class CRecordComponent(name:String, desc:TDesc, sign:Option[Sign],body:Seq[RecordCode]) 
  extends ClassCode 
  with NestedThey("body")

/**
 * Описывает класс / модуль
 * @param version версия байт-кода
 * @param access Права доступа, а так же всяки флаги
 * @param name имя (байт-код) класса
 * @param sign сигнатура, в случае Generic типа
 * @param superName имя (байт-код) класса родителя
 * @param interfaces имена (байт-код) интерфейсов
 * @param source имя исходного класса/файла отладки (debug)
 * @param module
 * @param nestHost 
 * @param annotations аннотации прикрепленные к классу
 * @param typeAnnotations аннотации прикрепленные к классу
 * @param nestMembers 
 * @param innerClasses 
 * @param fields список полней класса
 * @param methods список методов класса
 * @param order порядок определения полей/методов/..
 */
case class CBegin(
  version:CVersion,
  access:CBeginAccess,
  name:JavaName,
  sign:Option[CSign]=None,
  superName:Option[JavaName]=None,
  interfaces:Seq[String]=List(),
  source:Option[CSource]=None,
  module:Option[CModule]=None,
  nestHost:Option[CNestHost]=None,
  outerClass:Option[COuterClass] = None,
  annotations:Seq[CAnnotation]=List(),
  typeAnnotations:Seq[CTypeAnnotation]=List(),
  nestMembers:Seq[CNestMember]=List(),
  permittedSubClasses:Seq[CPermittedSubclass]=List(),
  innerClasses:Seq[CInnerClass]=List(),
  recordComponents:Seq[CRecordComponent]=List(),
  fields:Seq[CField]=List(),
  methods:Seq[CMethod]=List(),
  //order:Map[ClassCode,Int]=Map()
) 
  extends ClassCode 
  with NestedExcl("version","access","name","sign","superName","interfaces","order")

case class CSign(raw:String)

/** 
 * Версия класса. Младшая версия хранится в 16 старших битах,
 * и основная версия в 16 младших значащих битах
 * 
 * https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html
 * 
 * | JavaSE | Released         | Major | Supported majors |
 * |--------|------------------|-------|------------------|
 * | 1.0.2  | May 1996	       | 45    |  45              |
 * | 1.1	  | February 1997	   | 45    |	45              |
 * | 1.2	  | December 1998	   | 46    |	45 .. 46        |
 * | 1.3	  | May 2000	       | 47    |	45 .. 47        |
 * | 1.4	  | February 2002	   | 48    |	45 .. 48        |
 * | 5.0	  | September 2004	 | 49    |	45 .. 49        |
 * | 6	    | December 2006	   | 50    |	45 .. 50        |
 * | 7	    | July 2011	       | 51    |	45 .. 51        |
 * | 8	    | March 2014	     | 52    |	45 .. 52        |
 * | 9	    | September 2017	 | 53    |	45 .. 53        |
 * | 10     | March 2018	     | 54    |	45 .. 54        |
 * | 11     | September 2018	 | 55    |	45 .. 55        |
 * | 12     | March 2019	     | 56    |	45 .. 56        |
 * | 13     | September 2019	 | 57    |	45 .. 57        |
 * | 14     | March 2020	     | 58    |	45 .. 58        |
 * | 15     | September 2020	 | 59    |	45 .. 59        |
 * | 16     | March 2021	     | 60    |	45 .. 60        |
 * | 17     | September 2021	 | 61    |	45 .. 61        |
 * | 18     | March 2022	     | 62    |	45 .. 62        |
 * | 19     | September 2022	 | 63    |	45 .. 63        |
 */
case class CVersion(raw:Int):
  lazy val major:Int = raw         & 0xFFFFFFFF
  lazy val minor:Int = (raw >> 16) & 0xFFFFFFFF

object CVersion:
  case class MajorV(v:Int) extends AnyVal:
    def build:CVersion = CVersion(v & 0xFFFFFFFF)
    def minor(minor:Int):MinorV = MinorV(this, minor)

  case class MinorV(major:MajorV, minor:Int):
    def build:CVersion = CVersion((major.v & 0xFFFFFFFF) | (minor << 16))

  def major(v:Int):MajorV = MajorV(v)

  lazy val v1_1:CVersion = major(JSEVersion.v1_1.major.raw).build
  lazy val v1_2:CVersion = major(JSEVersion.v1_2.major.raw).build
  lazy val v1_3:CVersion = major(JSEVersion.v1_3.major.raw).build
  lazy val v1_4:CVersion = major(JSEVersion.v1_4.major.raw).build
  lazy val v5:CVersion   = major(JSEVersion.v5.major.raw).build
  lazy val v6:CVersion   = major(JSEVersion.v6.major.raw).build
  lazy val v7:CVersion   = major(JSEVersion.v7.major.raw).build
  lazy val v8:CVersion   = major(JSEVersion.v8.major.raw).build
  lazy val v9:CVersion   = major(JSEVersion.v9.major.raw).build
  lazy val v10:CVersion  = major(JSEVersion.v10.major.raw).build
  lazy val v11:CVersion  = major(JSEVersion.v11.major.raw).build
  lazy val v12:CVersion  = major(JSEVersion.v12.major.raw).build
  lazy val v13:CVersion  = major(JSEVersion.v13.major.raw).build
  lazy val v14:CVersion  = major(JSEVersion.v14.major.raw).build
  lazy val v15:CVersion  = major(JSEVersion.v15.major.raw).build
  lazy val v16:CVersion  = major(JSEVersion.v16.major.raw).build
  lazy val v17:CVersion  = major(JSEVersion.v17.major.raw).build
  lazy val v18:CVersion  = major(JSEVersion.v18.major.raw).build
  lazy val v19:CVersion  = major(JSEVersion.v19.major.raw).build

opaque type MajorVersion = Int
object MajorVersion:
  def apply(ver:Int):MajorVersion = ver

extension (mv:MajorVersion)
  def raw:Int = mv
  def ==(v:JSEVersion):Boolean = raw == v.major.raw
  def !=(v:JSEVersion):Boolean = raw != v.major.raw
  def <=(v:JSEVersion):Boolean = raw <= v.major.raw
  def >=(v:JSEVersion):Boolean = raw >= v.major.raw
  def <(v:JSEVersion):Boolean = raw < v.major.raw
  def >(v:JSEVersion):Boolean = raw > v.major.raw

given Ordering[MajorVersion] with
  def compare(a:MajorVersion, b:MajorVersion):Int = a.raw.compareTo(b.raw)

enum JSEVersion(val major:MajorVersion):
  case v1_1 extends JSEVersion(MajorVersion(45))
  case v1_2 extends JSEVersion(MajorVersion(46))
  case v1_3 extends JSEVersion(MajorVersion(47))
  case v1_4 extends JSEVersion(MajorVersion(48))
  case v5  extends JSEVersion(MajorVersion(49))
  case v6  extends JSEVersion(MajorVersion(50))
  case v7  extends JSEVersion(MajorVersion(51))
  case v8  extends JSEVersion(MajorVersion(52))
  case v9  extends JSEVersion(MajorVersion(53))
  case v10 extends JSEVersion(MajorVersion(54))
  case v11 extends JSEVersion(MajorVersion(55))
  case v12 extends JSEVersion(MajorVersion(56))
  case v13 extends JSEVersion(MajorVersion(57))
  case v14 extends JSEVersion(MajorVersion(58))
  case v15 extends JSEVersion(MajorVersion(59))
  case v16 extends JSEVersion(MajorVersion(60))
  case v17 extends JSEVersion(MajorVersion(61))
  case v18 extends JSEVersion(MajorVersion(62))
  case v19 extends JSEVersion(MajorVersion(63))

/**
 * Маркер конца класса.
 */
case class CEnd() extends ClassCode

/** 
 * Права доступа, а так же всяки флаги
 * 
 */
case class CBeginAccess(raw:Int):
  def `public`:Boolean     = ClassAccessFlag.ACC_PUBLIC.isSet(raw)
  def `final`:Boolean      = ClassAccessFlag.ACC_FINAL.isSet(raw)
  def `super`:Boolean      = ClassAccessFlag.ACC_SUPER.isSet(raw)
  def `interface`:Boolean  = ClassAccessFlag.ACC_INTERFACE.isSet(raw)
  def `abstract`:Boolean   = ClassAccessFlag.ACC_ABSTRACT.isSet(raw)
  def `synthetic`:Boolean  = ClassAccessFlag.ACC_SYNTHETIC.isSet(raw)
  def `annotation`:Boolean = ClassAccessFlag.ACC_ANNOTATION.isSet(raw)
  def `enum`:Boolean       = ClassAccessFlag.ACC_ENUM.isSet(raw)
  def `module`:Boolean     = ClassAccessFlag.ACC_MODULE.isSet(raw)

object CBeginAccess:
  import ClassAccessFlag.*

  def builder:Builder = Builder()

  case class Builder():
    def klass:ClassBuilder = ClassBuilder()

  case class ClassBuilder():
    def publico:ClassFlags = ClassFlags(Set(ACC_PUBLIC,ACC_SUPER))
    def packaje:ClassFlags = ClassFlags(Set(ACC_SUPER))

  case class ClassFlags(flags:Set[ClassAccessFlag]):
    def finale = FinalClassFlags(flags + ClassAccessFlag.ACC_FINAL)
    def abstracto = AbstractoClassFlags(flags + ClassAccessFlag.ACC_ABSTRACT)
    def synthetic = ClassFlags(flags + ClassAccessFlag.ACC_SYNTHETIC)
    def build:CBeginAccess =
      CBeginAccess( flags.foldLeft(0){ case (f, cf) => f | cf.bitMask } )
      
  case class FinalClassFlags(flags:Set[ClassAccessFlag]):
    def synthetic = FinalClassFlags(flags + ClassAccessFlag.ACC_SYNTHETIC)

  case class AbstractoClassFlags(flags:Set[ClassAccessFlag]):
    def synthetic = AbstractoClassFlags(flags + ClassAccessFlag.ACC_SYNTHETIC)

/**
  * The ACC_MODULE flag indicates that this class file defines a module, not a class or interface. 
  * If the ACC_MODULE flag is set, then special rules apply to the class file which are given at the end of this section. 
  * If the ACC_MODULE flag is not set, then the rules immediately below the current paragraph apply to the class file.
  * 
  * An interface is distinguished by the ACC_INTERFACE flag being set. 
  * If the ACC_INTERFACE flag is not set, this class file defines a class, not an interface or module.
  * 
  * If the ACC_INTERFACE flag is set, the ACC_ABSTRACT flag must also be set, 
  * and the ACC_FINAL, ACC_SUPER, ACC_ENUM, and ACC_MODULE flags set must not be set.
  * 
  * If the ACC_INTERFACE flag is not set, any of the other flags in Table 4.1-B may 
  * be set except ACC_ANNOTATION and ACC_MODULE. However, such a class file must not 
  * have both its ACC_FINAL and ACC_ABSTRACT flags set (JLS §8.1.1.2).
  * 
  * The ACC_SUPER flag indicates which of two alternative semantics is to be expressed by the invokespecial instruction 
  * (§invokespecial) if it appears in this class or interface. 
  * Compilers to the instruction set of the Java Virtual Machine should set the ACC_SUPER flag. 
  * In Java SE 8 and above, the Java Virtual Machine considers the ACC_SUPER flag to be set in every class file, 
  * regardless of the actual value of the flag in the class file and the version of the class file.
  * 
  * The ACC_SUPER flag exists for backward compatibility with code compiled by older compilers for the Java programming language. 
  * Prior to JDK 1.0.2, the compiler generated access_flags in which the flag now representing ACC_SUPER had no assigned meaning, 
  * and Oracle's Java Virtual Machine implementation ignored the flag if it was set.
  * 
  * The ACC_SYNTHETIC flag indicates that this class or interface was generated by a compiler and does not appear in source code.
  * 
  * An annotation interface (JLS §9.6) must have its ACC_ANNOTATION flag set. 
  * If the ACC_ANNOTATION flag is set, the ACC_INTERFACE flag must also be set.
  * 
  * The ACC_ENUM flag indicates that this class or its superclass is declared as an enum class (JLS §8.9).
  * 
  * All bits of the access_flags item not assigned in Table 4.1-B are reserved for future use. 
  * They should be set to zero in generated class files and should be ignored by Java Virtual Machine implementations.
  *
  * @param bitMask
  */
enum ClassAccessFlag(val bitMask:Int) extends BitMask:
  case ACC_PUBLIC extends     ClassAccessFlag(0x0001)
  case ACC_FINAL extends      ClassAccessFlag(0x0010)
  case ACC_SUPER extends      ClassAccessFlag(0x0020)
  case ACC_INTERFACE extends  ClassAccessFlag(0x0200)
  case ACC_ABSTRACT extends   ClassAccessFlag(0x0400)
  case ACC_SYNTHETIC extends  ClassAccessFlag(0x1000)
  case ACC_ANNOTATION extends ClassAccessFlag(0x2000)
  case ACC_ENUM extends       ClassAccessFlag(0x4000)
  case ACC_MODULE extends     ClassAccessFlag(0x8000)

object ClassAccessFlag:
  def flagsOf( flags:Int ):Set[ClassAccessFlag] =
    ClassAccessFlag
      .values
      .map( f => (f.isSet(flags), f) )
      .filter( (e,f) => e )
      .map( (e,f) => f )
      .toSet

  /** Правило проверки установленых битов */
  enum Rule(val validator:(flags:Int, clazz:CBegin) => Boolean ):
    case ItfRequiredAbs extends Rule( (flags,cls)=> 
      if ! ACC_INTERFACE.isSet(flags)
      then true
      else ACC_PUBLIC.isSet(flags)
    )
    case ItfExcFinalSuperEnumModule extends Rule( (flags,cls)=> 
      if ! ACC_INTERFACE.isSet(flags)
      then true
      else 
        !( ACC_FINAL.isSet(flags) || ACC_SUPER.isSet(flags) || ACC_ENUM.isSet(flags) || ACC_MODULE.isSet(flags) )
    )
    case NonItfExcAnnotationModule  extends Rule( (flags,cls)=> 
      if ACC_INTERFACE.isSet(flags)
      then true
      else 
        !( ACC_ANNOTATION.isSet(flags) || ACC_MODULE.isSet(flags) )
    )
    case NonItfExcFinalAndAbs extends Rule( (flags,cls)=> 
      if ACC_INTERFACE.isSet(flags)
      then true
      else 
        !( ACC_ABSTRACT.isSet(flags) && ACC_FINAL.isSet(flags) )
    )
    case SuperRequired extends Rule( (flags,cls)=> 
      if cls.version.major >= JSEVersion.v8
      then true
      else 
        if cls.superName.isDefined
        then ACC_SUPER.isSet(flags)   
        else true
    )
    case AnnRequiredItf extends Rule( (flags,cls)=> 
      if ! ACC_ANNOTATION.isSet(flags)
      then true
      else 
        ACC_INTERFACE.isSet(flags)
    )

  object Rule:
    def validate( flags:Int, clazz:CBegin ):Either[Rule,Unit] =
      Rule.values.find( r => ! r.validator(flags, clazz) ).toLeft( () )

//#region MethodAccessFlag

/**
 * Маска доступа
 * https://docs.oracle.com/javase/specs/jvms/se19/html/jvms-4.html#jvms-4.6
 * 
 * The value 0x0800 is interpreted as the ACC_STRICT flag only in a class file whose major 
 * version number is at least 46 and at most 60. For methods in such a class file, 
 * the rules below determine whether the ACC_STRICT flag may be set in combination with other flags. 
 * (Setting the ACC_STRICT flag constrained a method's floating-point instructions in 
 * Java SE 1.2 through 16 (§2.8).) For methods in a class file whose major version number is 
 * less than 46 or greater than 60, the value 0x0800 is not interpreted as the ACC_STRICT flag, 
 * but rather is unassigned; it is not meaningful to "set the ACC_STRICT flag" in such a class file.
 * 
 * Methods of classes may have any of the flags in Table 4.6-A set. 
 * However, each method of a class may have at most one of its ACC_PUBLIC, ACC_PRIVATE, and ACC_PROTECTED flags set (JLS §8.4.3).
 * 
 * Methods of interfaces may have any of the flags in Table 4.6-A set except ACC_PROTECTED, ACC_FINAL, ACC_SYNCHRONIZED, and ACC_NATIVE (JLS §9.4). 
 * 
 * In a class file whose version number is less than 52.0, each method of an interface must have its ACC_PUBLIC and ACC_ABSTRACT flags set; 
 * in a class file whose version number is 52.0 or above, each method of an interface must have exactly one of its ACC_PUBLIC and ACC_PRIVATE flags set.
 * 
 * If a method of a class or interface has its ACC_ABSTRACT flag set, 
 * it must not have any of its ACC_PRIVATE, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, ACC_NATIVE, or ACC_STRICT flags set.
 * 
 * Each instance initialization method (§2.9) 
 * may have at most one of its ACC_PUBLIC, ACC_PRIVATE, and ACC_PROTECTED flags set, and may also have 
 * its ACC_VARARGS, ACC_STRICT, and ACC_SYNTHETIC flags set, but must not have any of the other flags in Table 4.6-A set.
 * 
 * Class and interface initialization methods are called implicitly by the Java Virtual Machine. 
 * The value of their access_flags item is ignored except for the setting of the ACC_STRICT flag.
 * 
 * The ACC_BRIDGE flag is used to indicate a bridge method generated by a compiler for the Java programming language.
 * 
 * The ACC_VARARGS flag indicates that this method takes a variable number of arguments at the source code level. 
 * A method declared to take a variable number of arguments must be compiled with the ACC_VARARGS flag set to 1. 
 * All other methods must be compiled with the ACC_VARARGS flag set to 0.
 * 
 * The ACC_SYNTHETIC flag indicates that this method was generated by a compiler and does not appear in source code, 
 * unless it is one of the methods named in §4.7.8.
 * 
 * All bits of the access_flags item not assigned in Table 4.6-A are reserved for future use. 
 * They should be set to zero in generated class files and should be ignored by Java Virtual Machine implementations.
 * 
 * -----------------------
 * 
 * 4.6-A
 * 
 * | Flag Name | Value | Interpretation |
 * |-----------|-------|----------------|
 * | ACC_PUBLIC | 	0x0001 | 	Declared public; may be accessed from outside its package.|
 * | ACC_PRIVATE | 	0x0002 | 	Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).|
 * | ACC_PROTECTED | 	0x0004 | 	Declared protected; may be accessed within subclasses.|
 * | ACC_STATIC | 	0x0008 | 	Declared static.|
 * | ACC_FINAL | 	0x0010 | 	Declared final; must not be overridden (§5.4.5).|
 * | ACC_SYNCHRONIZED | 	0x0020 | 	Declared synchronized; invocation is wrapped by a monitor use.|
 * | ACC_BRIDGE | 	0x0040 | 	A bridge method, generated by the compiler.|
 * | ACC_VARARGS | 	0x0080 | 	Declared with variable number of arguments.|
 * | ACC_NATIVE | 	0x0100 | 	Declared native; implemented in a language other than the Java programming language.|
 * | ACC_ABSTRACT | 	0x0400 | 	Declared abstract; no implementation is provided.|
 * | ACC_STRICT | 	0x0800 | 	In a class file whose major version number is at least 46 and at most 60: Declared strictfp.|
 * | ACC_SYNTHETIC | 	0x1000 | 	Declared synthetic; not present in the source code. |
 *
 * @param bitMask маска
 */
enum MethodAccessFlag(val bitMask:Int) extends BitMask:
  case ACC_PUBLIC       extends MethodAccessFlag(0x0001)
  case ACC_PRIVATE      extends MethodAccessFlag(0x0002)
  case ACC_PROTECTED    extends MethodAccessFlag(0x0004)

  /** 
   * Метод инициализации класса или интерфейса (§2.9.2) неявно 
   * вызывается виртуальной машиной Java. 
   * Значение его элемента access_flags игнорируется, 
   * за исключением установки флага ACC_STATIC и 
   * (в файле класса, номер основной версии которого составляет не менее 46 и не более 60) 
   * флага ACC_STRICT, а метод освобождается от предыдущих правил, 
   * касающихся разрешенных комбинации флагов.
   */
  case ACC_STATIC       extends MethodAccessFlag(0x0008)
  case ACC_FINAL        extends MethodAccessFlag(0x0010)
  case ACC_SYNCHRONIZED extends MethodAccessFlag(0x0020)

  /**
    * Флаг ACC_BRIDGE используется для обозначения метода моста, 
    * сгенерированного компилятором для языка программирования Java.
    */
  case ACC_BRIDGE       extends MethodAccessFlag(0x0040)

  /**
    * Флаг ACC_VARARGS указывает, что этот метод принимает 
    * переменное количество аргументов на уровне исходного кода. 
    * Метод, объявленный для приема переменного количества аргументов, 
    * должен быть скомпилирован с флагом ACC_VARARGS, установленным в 1. 
    * Все остальные методы должны быть скомпилированы 
    * с флагом ACC_VARARGS, установленным в 0.
    */
  case ACC_VARARGS      extends MethodAccessFlag(0x0080)
  case ACC_NATIVE       extends MethodAccessFlag(0x0100)
  case ACC_ABSTRACT     extends MethodAccessFlag(0x0400)
  case ACC_STRICT       extends MethodAccessFlag(0x0800)

  /**
    * Флаг ACC_SYNTHETIC указывает, что этот метод был сгенерирован компилятором 
    * и не появляется в исходном коде, если только это не один из методов, 
    * названных в §4.7.8.
    */
  case ACC_SYNTHETIC    extends MethodAccessFlag(0x1000)

object MethodAccessFlag:
  /** Возвращает флаги */
  def flagsOf( flags:Int ):Set[MethodAccessFlag] =
    MethodAccessFlag
      .values
      .map( f => (f.isSet(flags), f) )
      .filter( (e,f) => e )
      .map( (e,f) => f )
      .toSet

  /** Список правил которым комбинация битов должна удовлетворять */
  enum Rule(val validator:(flags:Int, method:CMethod, clazz:CBegin) => Boolean ):
    /** 
     * The value 0x0800 is interpreted as the ACC_STRICT flag only in a class file 
     * whose major version number is at least 46 and at most 60. 
     * For methods in such a class file, the rules below determine whether 
     * the ACC_STRICT flag may be set in combination with other flags. 
     * (Setting the ACC_STRICT flag constrained a method's floating-point 
     * instructions in Java SE 1.2 through 16 (§2.8).) 
     * For methods in a class file whose major version number is 
     * less than 46 or greater than 60, the value 0x0800 is not 
     * interpreted as the ACC_STRICT flag, but rather is unassigned; 
     * it is not meaningful to "set the ACC_STRICT flag" in such a class file.
    */
    case StrictNonEffective extends Rule(
      (flags,meth,cls) =>
        if ACC_STRICT.isSet(flags) && (cls.version.major < 46 || cls.version.major > 60) 
        then false
        else true
    )

    /**
      * Methods of classes may have any of the flags in Table 4.6-A set. 
      * However, each method of a class may have at most one of 
      * its ACC_PUBLIC, ACC_PRIVATE, and ACC_PROTECTED flags set (JLS §8.4.3). 
      */
    case OnlyOneOfPublicPrivateProtected extends Rule(
      (flags,meth,cls) =>
        if ! cls.access.interface then
          val pub = if ACC_PUBLIC.isSet(flags) then 1 else 0
          val prv = if ACC_PRIVATE.isSet(flags) then 1 else 0
          val prt = if ACC_PROTECTED.isSet(flags) then 1 else 0
          (pub + prv + prt)<2
        else
          true
    )

    /** 
      * Methods of interfaces may have any of the flags in Table 4.6-A 
      * set except ACC_PROTECTED, ACC_FINAL, ACC_SYNCHRONIZED, and ACC_NATIVE (JLS §9.4). 
      * In a class file whose version number is less than 52.0, 
      * each method of an interface must have its ACC_PUBLIC and ACC_ABSTRACT flags set; 
      * in a class file whose version number is 52.0 or above, each method of an interface 
      * must have exactly one of its ACC_PUBLIC and ACC_PRIVATE flags set. 
    */
    case ItfExcludeProtectedFinalSyncNative extends Rule(
      (flags,meth,cls) =>
        if ! cls.access.interface then true
        else 
          val prt = ACC_PROTECTED.isSet(flags)
          val fnl = ACC_FINAL.isSet(flags)
          val snc = ACC_SYNCHRONIZED.isSet(flags)
          val ntv = ACC_NATIVE.isSet(flags)
          !( prt || fnl || snc || ntv )
    )

    /**
      * Methods of interfaces may have any of the flags in Table 4.6-A 
      * set except ACC_PROTECTED, ACC_FINAL, ACC_SYNCHRONIZED, and ACC_NATIVE (JLS §9.4). 
      * In a class file whose version number is less than 52.0, 
      * each method of an interface must have its ACC_PUBLIC and ACC_ABSTRACT flags set; 
      * in a class file whose version number is 52.0 or above, each method of an interface 
      * must have exactly one of its ACC_PUBLIC and ACC_PRIVATE flags set. 
      */
    case ItfLess52PubAbstractRequired extends Rule(
      (flags,meth,cls) =>
        if !( cls.access.interface && cls.version.major < 52 )
        then true
        else
          val pub = ACC_PUBLIC.isSet(flags)
          val abs = ACC_ABSTRACT.isSet(flags)
          (pub && abs)
    )

    /**
      * Methods of interfaces may have any of the flags in Table 4.6-A 
      * set except ACC_PROTECTED, ACC_FINAL, ACC_SYNCHRONIZED, and ACC_NATIVE (JLS §9.4). 
      * In a class file whose version number is less than 52.0, 
      * each method of an interface must have its ACC_PUBLIC and ACC_ABSTRACT flags set; 
      * in a class file whose version number is 52.0 or above, each method of an interface 
      * must have exactly one of its ACC_PUBLIC and ACC_PRIVATE flags set. 
      */
    case Itf52MoreOrEqPubOrPrivateRequired extends Rule(
      (flags,meth,cls) =>
        if !( cls.access.interface && cls.version.major >= 52 )
        then true
        else 
          val pub = ACC_PUBLIC.isSet(flags)
          val prv = ACC_PRIVATE.isSet(flags)
          if pub && prv 
          then false
          else pub || prv
    )

    /**
     * If a method of a class or interface has its ACC_ABSTRACT flag set, 
     * it must not have any of its ACC_PRIVATE, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, 
     * or ACC_NATIVE flags set, nor (in a class file whose major version number 
     * is at least 46 and at most 60) have its ACC_STRICT flag set. 
     */
    case AbsExceludePrivStaticFinalSyncNative extends Rule(
      (flags,meth,cls) =>
        if ! ACC_ABSTRACT.isSet(flags)
        then true
        else 
          val prv = ACC_PRIVATE.isSet(flags)
          val stc = ACC_STATIC.isSet(flags)
          val fnl = ACC_FINAL.isSet(flags)
          val snc = ACC_SYNCHRONIZED.isSet(flags)
          val ntv = ACC_NATIVE.isSet(flags)
          !(prv || stc || fnl || snc || ntv)
    )

      /**
       * Метод инициализации экземпляра (§2.9.1) может иметь не более одного 
       * установленного флага ACC_PUBLIC, ACC_PRIVATE и ACC_PROTECTED, 
       * а также может иметь установленные флаги ACC_VARARGS и ACC_SYNTHETIC, 
       * а также (в файле класса, основной номер версии которого не менее 46 и не более 60) 
       * должны иметь установленный флаг ACC_STRICT, 
       * но не должны иметь никаких других установленных флагов в Таблице 4.6-A.
       */
      case InitMthOneOfPubPrivPrtAndVarArgSyntStrict extends Rule(
        (flags,meth,cls) =>
          if SpecialMethodName.parse(meth.name).isEmpty
          then true
          else
            val pub = if ACC_PUBLIC.isSet(flags) then 1 else 0
            val prv = if ACC_PRIVATE.isSet(flags) then 1 else 0
            val prt = if ACC_PROTECTED.isSet(flags) then 1 else 0
            if (pub + prv + prt) > 1
            then false
            else
              val flagSet = flagsOf(flags)
              val remFlagSet = 
                flagSet - 
                  ACC_PUBLIC - ACC_PRIVATE - ACC_PROTECTED -
                  ACC_STRICT - ACC_VARARGS - ACC_SYNTHETIC 
              remFlagSet.isEmpty
      )

      /**
        * В файле класса с номером версии 51.0 или выше для метода 
        * с именем <clinit> должен быть установлен флаг ACC_STATIC.
        */
      case CInitRequiredStatFor51 extends Rule(
        (flags,meth,cls) =>
          if ! ( cls.version.major >= 51 
                 && SpecialMethodName.parse(meth.name).contains(SpecialMethodName.StaticInit) )
          then true
          else 
            ACC_STATIC.isSet(flags)
      )

      /**
        * A class or interface initialization method (§2.9.2) is called 
        * implicitly by the Java Virtual Machine. 
        * The value of its access_flags item is ignored except 
        * for the setting of the ACC_STATIC flag and (in a class file 
        * whose major version number is at least 46 and at most 60) 
        * the ACC_STRICT flag, and the method is exempt from the preceding 
        * rules about legal combinations of flags. 
        */

  def validate( flags:Int, method:CMethod, clazz:CBegin ):Either[Rule,Unit] =
    Rule.values.find( r => ! r.validator(flags, method, clazz) ).toLeft( () )

//#endregion

/**
  * Специальные имена методов (конструктор)
  *
  * @param raw исходное имя
  */
enum SpecialMethodName(raw:String):
  case Constructor extends SpecialMethodName("<init>")
  case StaticInit  extends SpecialMethodName("<clinit>")

object SpecialMethodName:
  def parse(name:String):Option[SpecialMethodName] =
    name match
      case "<init>"   => Some(SpecialMethodName.Constructor)
      case "<clinit>" => Some(SpecialMethodName.StaticInit)
      case _ => None
