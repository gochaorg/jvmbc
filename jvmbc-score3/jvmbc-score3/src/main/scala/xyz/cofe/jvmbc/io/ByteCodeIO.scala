package xyz.cofe.jvmbc
package io

import cls.CBegin
import org.objectweb.asm.ClassReader
import scala.util.Using
import scala.util.Failure
import scala.util.Success

object ByteCodeIO {
  def parse(bytes:Array[Byte]):Either[String,CBegin] =
    val inst = ClassDump(org.objectweb.asm.Opcodes.ASM9)
    val cr = new ClassReader(bytes)
    cr.accept(inst,0)
    inst.build

  def parse(clazz:Class[_]):Either[String,CBegin] =
    val resName = clazz.getName().replace(".","/")+".class"
    val url = clazz.getResource(resName)
    if url==null then
      Left(s"resource $resName not found")
    else
      Using(url.openStream()) { stream => 
        parse(stream.readAllBytes())
      } match
        case Failure(exception) => Left(exception.getMessage())
        case Success(value) => value
          
}
