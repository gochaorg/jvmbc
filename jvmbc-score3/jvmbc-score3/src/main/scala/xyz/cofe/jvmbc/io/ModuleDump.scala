package xyz.cofe.jvmbc
package io

import org.objectweb.asm.ModuleVisitor
import mdl._

/**
order

- ( visitMainClass | 
  - ( 
    - visitPackage | 
    - visitRequire | 
    - visitExport | 
    - visitOpen | 
    - visitUse | 
    - visitProvide 
  - )* 
- ) 
- visitEnd
*/
class ModuleDump(
  private val _api:Int,
  atEnd: Option[Either[String,Modulo]=>Unit] = None
)
extends ModuleVisitor(_api):
  var mainClass:Option[ModMainClass] = None
  var packages:List[ModPackage] = List()
  var requires:List[ModRequire] = List()
  var exports:List[ModExport] = List()
  var opens:List[ModOpen] = List()
  var uses:List[ModUse] = List()
  var providers:List[ModProvide] = List()

  /**
   * Visit the main class of the current module.
   * @param mainClass the internal name of the main class of the current module.
   */
  override def visitMainClass(mainClass:String):Unit =
    this.mainClass = Some(ModMainClass(mainClass))

  /**
   * Visit a package of the current module.
   * @param packaze the internal name of a package.
   */
  override def visitPackage(packaze:String):Unit =
    this.packages = ModPackage(packaze) :: this.packages

  /**
   * Visits a dependence of the current module.
   *
   * @param module the fully qualified name (using dots) of the dependence.
   * @param access the access flag of the dependence among {@code ACC_TRANSITIVE}, {@code
   *     ACC_STATIC_PHASE}, {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
   * @param version the module version at compile time, or {@literal null}.
   */
  override def visitRequire(module:String, access:Int, version:String):Unit =
    this.requires = ModRequire(
      module,
      ModRequireAccess(access),
      if version!=null then Some(version) else None
      ) :: this.requires

  /**
   * Visit an exported package of the current module.
   *
   * @param packaze the internal name of the exported package.
   * @param access the access flag of the exported package, valid values are among {@code
   *     ACC_SYNTHETIC} and {@code ACC_MANDATED}.
   * @param modules the fully qualified names (using dots) of the modules that can access the public
   *     classes of the exported package, or {@literal null}.
   */
  override def visitExport(packaze:String, access:Int, modules:Array[? <: String]):Unit = 
    this.exports = ModExport(
      packaze,
      ModExportAccess(access),
      if modules!=null then modules else List()
      ) :: this.exports

  /**
   * Visit an open package of the current module.
   *
   * @param packaze the internal name of the opened package.
   * @param access the access flag of the opened package, valid values are among {@code
   *     ACC_SYNTHETIC} and {@code ACC_MANDATED}.
   * @param modules the fully qualified names (using dots) of the modules that can use deep
   *     reflection to the classes of the open package, or {@literal null}.
   */
  override def visitOpen(packaze:String, access:Int, modules:Array[? <: String]):Unit =
    this.opens = ModOpen(
      packaze,
      ModOpenAccess(access),
      if modules!=null then modules else List()
    ) :: this.opens

  /**
   * Visit a service used by the current module. The name must be the internal name of an interface
   * or a class.
   *
   * @param service the internal name of the service.
   */
  override def visitUse(service:String):Unit = 
    this.uses = ModUse(service) :: this.uses

  /**
   * Visit an implementation of a service.
   *
   * @param service the internal name of the service.
   * @param providers the internal names of the implementations of the service (there is at least
   *     one provider).
   */
  override def visitProvide(service:String, providers:Array[? <: String]):Unit = 
    this.providers = ModProvide(
      service,
      providers
    ) :: this.providers

  /**
   * Visits the end of the module. This method, which is the last one to be called, is used to
   * inform the visitor that everything have been visited.
   */
  override def visitEnd():Unit =
    atEnd match
      case None =>
      case Some(call) => call(build)

  def build:Either[String,Modulo] =
    import FirstErr.firstErr
    Right(
      Modulo(
        mainClass,
        packages.reverse,
        requires.reverse,
        exports.reverse,
        opens.reverse,
        uses.reverse,
        providers.reverse
      )
    )