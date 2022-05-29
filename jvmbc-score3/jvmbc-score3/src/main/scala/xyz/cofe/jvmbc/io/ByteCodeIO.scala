package xyz.cofe.jvmbc
package io

import cls.CBegin
import org.objectweb.asm.ClassReader

trait ByteCodeIO[ERR] {
  def parse(bytes:Array[Byte]):Either[ERR,CBegin]
}

object ByteCodeIO {
  def parser:ByteCodeIO[String]=
    val inst = ClassDump(org.objectweb.asm.Opcodes.ASM9)
    new ByteCodeIO[String] {
      override def parse(bytes:Array[Byte]):Either[String,CBegin]=
        val cr = new ClassReader(bytes)
        cr.accept(inst,0)
        inst.build
    }
}
