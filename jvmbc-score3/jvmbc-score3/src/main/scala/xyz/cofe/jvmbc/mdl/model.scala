package xyz.cofe.jvmbc
package mdl

sealed trait ModuleCode extends ByteCode

case class Modulo(
  mainClass: Option[ModMainClass],
  packages: Seq[ModPackage] = List(),
  requires: Seq[ModRequire] = List(),
  exports: Seq[ModExport] = List(),
  opens: Seq[ModOpen] = List(),
  uses: Seq[ModUse] = List(),
  providers: Seq[ModProvide] = List(),
) extends ModuleCode with NestedAll

case class ModMainClass(name:String) 
  extends ModuleCode

case class ModPackage(name:String) 
  extends ModuleCode

case class ModRequire(
  module:String, 
  access:ModRequireAccess,
  version:Option[String]
) extends ModuleCode

case class ModRequireAccess(raw:Int)

case class ModExport(packaze:String, access:ModExportAccess, modules:Seq[String]) 
  extends ModuleCode
  with NestedThey("modules")

case class ModExportAccess(raw:Int)

case class ModOpen(packaze:String, access:ModOpenAccess, modules:Seq[String]) 
  extends ModuleCode
  with NestedThey("modules")

case class ModOpenAccess(raw:Int)

case class ModUse(service:String) extends ModuleCode
case class ModProvide(service:String, providers:Seq[String]) extends ModuleCode
