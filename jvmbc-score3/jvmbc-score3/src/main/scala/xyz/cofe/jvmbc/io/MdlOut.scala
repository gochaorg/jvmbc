package xyz.cofe.jvmbc.io

import org.objectweb.asm.ModuleVisitor
import xyz.cofe.jvmbc.mdl._

trait MdlOut[V]:
  def write(out:ModuleVisitor, v:V):Unit

object MdlOut:
  given MdlOut[ModMainClass] with
    def write(out: ModuleVisitor, v: ModMainClass): Unit = 
      out.visitMainClass(v.name)

  given MdlOut[ModPackage] with
    def write(out: ModuleVisitor, v: ModPackage): Unit = 
      out.visitPackage(v.name)

  given MdlOut[ModRequire] with
    def write(out: ModuleVisitor, v: ModRequire): Unit = 
      out.visitRequire(v.module, v.access.raw, v.version.orNull)

  given MdlOut[ModExport] with
    def write(out: ModuleVisitor, v: ModExport): Unit = 
      out.visitExport(
        v.packaze, v.access.raw, v.modules:_*
      )

  given MdlOut[ModOpen] with
    def write(out: ModuleVisitor, v: ModOpen): Unit = 
      out.visitOpen(v.packaze, v.access.raw, v.modules:_*)

  given MdlOut[ModUse] with
    def write(out: ModuleVisitor, v: ModUse): Unit = 
      out.visitUse(v.service)

  given MdlOut[ModProvide] with
    def write(out: ModuleVisitor, v: ModProvide): Unit = 
      out.visitProvide(v.service, v.providers:_*)

  given [V:MdlOut]:MdlOut[Seq[V]] with
    def write(out: ModuleVisitor, v: Seq[V]): Unit = 
      v.foreach { v => summon[MdlOut[V]].write(out,v) }

  given [V:MdlOut]:MdlOut[List[V]] with
    def write(out: ModuleVisitor, v: List[V]): Unit = 
      v.foreach { v => summon[MdlOut[V]].write(out,v) }

  given MdlOut[Modulo] with
    def write(out: ModuleVisitor, v: Modulo): Unit = 
      v.mainClass.foreach { v => summon[MdlOut[ModMainClass]].write(out, v) }
      v.packages.foreach { v => summon[MdlOut[ModPackage]].write(out, v) }
      v.requires.foreach { v => summon[MdlOut[ModRequire]].write(out, v) }
      v.exports.foreach { v => summon[MdlOut[ModExport]].write(out, v) }
      v.opens.foreach { v => summon[MdlOut[ModOpen]].write(out, v) }
      v.uses.foreach { v => summon[MdlOut[ModUse]].write(out, v) }
      v.providers.foreach { v => summon[MdlOut[ModProvide]].write(out, v) }
      out.visitEnd()

  given MdlOut[ModuleCode] with
    def write(out: ModuleVisitor, v: ModuleCode): Unit = 
      v match
        case m:Modulo =>  summon[MdlOut[Modulo]].write(out, m)
        case m:ModMainClass => summon[MdlOut[ModMainClass]].write(out, m)
        case m:ModPackage => summon[MdlOut[ModPackage]].write(out, m)
        case m:ModRequire => summon[MdlOut[ModRequire]].write(out, m)
        case m:ModExport => summon[MdlOut[ModExport]].write(out, m)
        case m:ModOpen => summon[MdlOut[ModOpen]].write(out, m)
        case m:ModUse => summon[MdlOut[ModUse]].write(out, m)
        case m:ModProvide => summon[MdlOut[ModProvide]].write(out, m)
        case ModEnd() => ()
      