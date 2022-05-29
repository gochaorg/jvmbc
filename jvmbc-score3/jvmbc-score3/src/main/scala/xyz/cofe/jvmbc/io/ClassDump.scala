package xyz.cofe.jvmbc
package io

import cls._
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ModuleVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.RecordComponentVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.TypePath
import org.objectweb.asm.Attribute

/**

_(by author - Eric Bruneton)_

A visitor to visit a Java class. 
 
The methods of this class must be called in the following order:

- visit 
- [ visitSource ] 
- [ visitModule ]
- [ visitNestHost ]
- [ visitOuterClass ] 
- ( visitAnnotation |
  - visitTypeAnnotation | 
  - visitAttribute 
- )* 
- ( visitNestMember | 
  - [ * visitPermittedSubclass ] | 
  - visitInnerClass | 
  - visitRecordComponent | 
  - visitField | 
  - visitMethod 
- )*
- visitEnd.
 */
class ClassDump(private val _api:Int)
extends ClassVisitor(_api, null)
{
  var version:Option[CVersion] = None
  var access:Option[CBeginAccess] = None
  var name:Option[JavaName] = None
  var sign:Option[CSign] = None
  var superName:Option[JavaName] = None
  var interfaces:Seq[String] = List()
  var source:Option[CSource] = None
  //var visitModule
  var nestHost:Option[CNestHost] = None
  var outerClass:Option[COuterClass] = None
  var annotations:Seq[Either[String,CAnnotation]] = List()
  var typeAnnotations:Seq[Either[String,CTypeAnnotation]] = List()
  var nestMembers:Seq[CNestMember] = List()
  var permittedSubClasses:Seq[CPermittedSubclass] = List()
  var innerClasses:Seq[CInnerClass] = List()
  //var visitRecordComponent
  var fields:Seq[Either[String,CField]] = List()
  var methods:Seq[Either[String,CMethod]] = List()
  var order:Map[ClassCode,Int]=Map()

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
  override def visit(
       version: Int,
       access: Int,
       name: String,
       signature: String,
       superName: String,
      interfaces: Array[String])
      :Unit = 
    this.version = Some(CVersion(version))
    this.access = Some(CBeginAccess(access))
    this.name = if name!=null then Some(JavaName(name)) else None
    this.sign = if signature!=null then Some(CSign(signature)) else None
    this.superName = if superName!=null then Some(JavaName(superName)) else None
    this.interfaces = if interfaces!=null then interfaces else List()

  /**
   * Visits the source of the class.
   *
   * @param source the name of the source file from which the class was compiled. May be {@literal
   *     null}.
   * @param debug additional debug information to compute the correspondence between source and
   *     compiled elements of the class. May be {@literal null}.
   */
  override def visitSource(source:String, debug:String):Unit = 
    (source!=null, debug!=null) match
      case (true,true) =>
        this.source =Some(CSource(
          if source!=null then Some(source) else None,
          if debug!=null then Some(debug) else None
        ))
      case (false,true) =>
        this.source =Some(CSource(
          if source!=null then Some(source) else None,
          if debug!=null then Some(debug) else None
        ))
      case (true,false) =>
        this.source =Some(CSource(
          if source!=null then Some(source) else None,
          if debug!=null then Some(debug) else None
        ))
      case _ =>

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
  override def visitModule(name:String, access:Int, version:String):ModuleVisitor = 
    ModuleDump(_api)

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
  override def visitNestHost(nestHost:String):Unit = 
    this.nestHost = if nestHost!=null then Some(CNestHost(nestHost)) else None

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
  override def visitOuterClass(owner:String, name:String, descriptor:String):Unit = 
    this.outerClass = Some(COuterClass(
      owner,
      if name!=null then Some(name) else None,
      if descriptor!=null then Some(TDesc(descriptor)) else None
    ))

  /**
   * Visits an annotation of the class.
   *
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitAnnotation(descriptor:String, visible:Boolean):AnnotationVisitor =
    AnnotationDump(_api,Some(bodyEthier=>{
      annotations = bodyEthier.map { body => 
        CAnnotation(TDesc(descriptor),visible,body)
      } +: annotations
    }))

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
  override def visitTypeAnnotation(typeRef:Int, typePath:TypePath, descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api,Some(bodyEthier=>{
      typeAnnotations = bodyEthier.map { body => 
        CTypeAnnotation(
          CTypeRef(typeRef),
          if typePath!=null then Some(typePath.toString) else None,
          TDesc(descriptor),
          visible,
          body
        )
      } +: typeAnnotations
    }))
    
  override def visitAttribute(attribute:Attribute):Unit = {
    /*
    A non standard class, field, method or Code attribute, as defined in the Java Virtual Machine
    Specification (JVMS).
      @see <a href= "https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7">JVMS
        4.7</a>
    @see <a href= "https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.3">JVMS
        4.7.3</a>
    @author Eric Bruneton
    @author Eugene Kuleshov
    */        
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
  override def visitNestMember(nestMember:String):Unit = {
    this.nestMembers = CNestMember(nestMember) +: this.nestMembers
  }

  /**
   * Visits a permitted subclasses. A permitted subclass is one of the allowed subclasses of the
   * current class.
   *
   * @param permittedSubclass the internal name of a permitted subclass.
   */
  override def visitPermittedSubclass(permittedSubclass:String):Unit = {
    this.permittedSubClasses = CPermittedSubclass(permittedSubclass) +: this.permittedSubClasses
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
  override def visitInnerClass(name:String, outerName:String, innerName:String, access:Int):Unit = {
    this.innerClasses = CInnerClass(
      CInnerClassAccess(access),
      name,
      if outerName!=null then Some(outerName) else None,
      if innerName!=null then Some(innerName) else None ) +: this.innerClasses
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
  override def visitRecordComponent(name:String, descriptor:String, signature:String):RecordComponentVisitor = 
    RecordDump(_api)

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
  override def visitField(access:Int, name:String, descriptor:String, signature:String, value:AnyRef):FieldVisitor = 
    FieldDump(_api,Some(fieldEt => {
      val cf = fieldEt.map { body => 
        CField(
          access,
          name,
          TDesc(descriptor),
          if signature!=null then Some(Sign(signature)) else None,
          if value!=null then Some(value) else None
        )
      }
      fields = cf +: fields
    }))

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
  override def visitMethod(access:Int, name:String, descriptor:String, signature:String, exceptions:Array[String]):MethodVisitor = 
    MethodDump(_api,Some(methEt => {
      val cm = methEt.map { body => 
        CMethod(
          CMethodAccess(access),
          name,
          MDesc(descriptor),
          if signature!=null then Some(MSign(signature)) else None,
          if exceptions!=null then exceptions else List(),
          body
        )
      }
      methods = cm +: methods
    }))

  /**
   * Visits the end of the class. This method, which is the last one to be called, is used to inform
   * the visitor that all the fields and methods of the class have been visited.
   */
  override def visitEnd():Unit = {}

  def build:Either[String,CBegin] =
    import FirstErr.firstErr
    for {
      ver  <- version.toRight("version not defined")
      acc  <- access.toRight("access not defined")
      nam  <- name.toRight("name not defined")
      ann  <- firstErr(annotations)
      tann <- firstErr(typeAnnotations)
      flds <- firstErr(fields)
      mths <- firstErr(methods)
    } yield CBegin(
      ver,
      acc,
      nam,
      sign,
      superName,
      interfaces.reverse,
      source,
      nestHost,
      ann.reverse,
      tann.reverse,
      nestMembers.reverse,
      permittedSubClasses.reverse,
      innerClasses.reverse,
      flds,
      mths.reverse,
      order
    )
}
