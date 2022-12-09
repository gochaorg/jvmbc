package xyz.cofe.jvmbc
package io

import cls.CBegin
import org.objectweb.asm.ClassReader
import scala.util.Using
import scala.util.Failure
import scala.util.Success
import java.net.URL

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

  def parse(url:URL):Either[String,CBegin] =
    if url==null then
      Left(s"resource null not found")
    else
      var stream : java.io.InputStream = null
      try 
        stream = url.openStream()
        val bytes = stream.readAllBytes()
        println(s"read ${bytes.length} bytes")
        val result = parse(bytes)
        result
      catch
        case err:java.io.IOException =>
          Left(err.getMessage())
      finally
        if stream!=null then
          stream.close()

  def parse(cl:ClassLoader, className:JavaName):Either[String,CBegin] =
    val resName = className.raw + ".class"
    val url = cl.getResource( resName )
    if url==null then
      Left(s"resource $resName not found")
    else
      parse(url)
}
