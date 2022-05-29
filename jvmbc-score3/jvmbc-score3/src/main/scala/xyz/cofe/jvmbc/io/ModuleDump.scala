package xyz.cofe.jvmbc
package io

import org.objectweb.asm.ModuleVisitor

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
class ModuleDump(private val _api:Int)
extends ModuleVisitor(_api):
  /**
   * Visit the main class of the current module.
   * @param mainClass the internal name of the main class of the current module.
   */
  override def visitMainClass(mainClass:String):Unit = ???

  /**
   * Visit a package of the current module.
   * @param packaze the internal name of a package.
   */
  override def visitPackage(packaze:String):Unit = ???

  /**
   * Visits a dependence of the current module.
   *
   * @param module the fully qualified name (using dots) of the dependence.
   * @param access the access flag of the dependence among {@code ACC_TRANSITIVE}, {@code
   *     ACC_STATIC_PHASE}, {@code ACC_SYNTHETIC} and {@code ACC_MANDATED}.
   * @param version the module version at compile time, or {@literal null}.
   */
  override def visitRequire(module:String, access:Int, version:String):Unit = ???

  /**
   * Visit an exported package of the current module.
   *
   * @param packaze the internal name of the exported package.
   * @param access the access flag of the exported package, valid values are among {@code
   *     ACC_SYNTHETIC} and {@code ACC_MANDATED}.
   * @param modules the fully qualified names (using dots) of the modules that can access the public
   *     classes of the exported package, or {@literal null}.
   */
  override def visitExport(packaze:String, access:Int, modules:Array[? <: String]):Unit = ???

  /**
   * Visit an open package of the current module.
   *
   * @param packaze the internal name of the opened package.
   * @param access the access flag of the opened package, valid values are among {@code
   *     ACC_SYNTHETIC} and {@code ACC_MANDATED}.
   * @param modules the fully qualified names (using dots) of the modules that can use deep
   *     reflection to the classes of the open package, or {@literal null}.
   */
  override def visitOpen(packaze:String, access:Int, modules:Array[? <: String]):Unit = ???

  /**
   * Visit a service used by the current module. The name must be the internal name of an interface
   * or a class.
   *
   * @param service the internal name of the service.
   */
  override def visitUse(service:String):Unit = ???

  /**
   * Visit an implementation of a service.
   *
   * @param service the internal name of the service.
   * @param providers the internal names of the implementations of the service (there is at least
   *     one provider).
   */
  override def visitProvide(service:String, providers:Array[? <: String]):Unit = ???

  /**
   * Visits the end of the module. This method, which is the last one to be called, is used to
   * inform the visitor that everything have been visited.
   */
  override def visitEnd():Unit = ???