package xyz.cofe.jvmbc
package io

import cls.CBegin
import org.objectweb.asm.ClassReader
import scala.util.Using
import scala.util.Failure
import scala.util.Success
import java.net.URL
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.lang.invoke.SerializedLambda
import xyz.cofe.jvmbc.cls.CMethod

/** Генерация байт кода */
extension (cbegin:CBegin)
  def toBytes:Array[Byte] =
    val cwriter = ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
    summon[COut[CBegin]].write(cwriter, cbegin)
    cwriter.toByteArray()

/**
  * Парсинг байт-кода
  */
object ByteCodeIO {
  /** Парсинг лямбды, предполагается что лямбда реализует Serializable */
  object lambda {
    def parse[A,B]( some:A=>B ):Either[String,(CBegin, CMethod)] =
      val meth = some.getClass().getDeclaredMethod("writeReplace")
      meth.setAccessible(true)
      var sl = meth.invoke(some).asInstanceOf[SerializedLambda]
      parse(some.getClass().getClassLoader(), sl)

    def parse( cl:ClassLoader, sl:SerializedLambda ):Either[String,(CBegin, CMethod)] =
      ByteCodeIO.parse(cl, JavaName(sl.getImplClass())) match
        case Left(v) => Left(v)
        case Right(cbegin) => 
          cbegin.methods
            .find( m => m.name == sl.getImplMethodName && m.desc.raw == sl.getImplMethodSignature )
            .toRight(s"method ${sl.getImplMethodName()} with ${sl.getImplMethodSignature} not found in ${cbegin.name}")
            .map( m => (cbegin,m) )
  }

  /**
    * Парсинг байт-кода
    *
    * @param bytes байт-код
    * @return результат парсинга
    */
  def parse(bytes:Array[Byte]):Either[String,CBegin] =
    val inst = ClassDump(org.objectweb.asm.Opcodes.ASM9)
    val cr = new ClassReader(bytes)
    cr.accept(inst,0)
    inst.build

  /**
    * Парсинг класса, предполагается что байт-код есть в ресурсах программы
    *
    * @param clazz класс, его байт-код должен быть доступен в ресурсах программы
    * @return результат парсинга
    */
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

  /**
    * Парсинг байт-кода
    *
    * @param url байт-код
    * @return результат парсинга
    */
  def parse(url:URL):Either[String,CBegin] =
    if url==null then
      Left(s"resource null not found")
    else
      var stream : java.io.InputStream = null
      try 
        stream = url.openStream()
        val bytes = stream.readAllBytes()
        val result = parse(bytes)
        result
      catch
        case err:java.io.IOException =>
          Left(err.getMessage())
      finally
        if stream!=null then
          stream.close()

  /**
    * Парсинг класса, предполагается что байт-код есть в ресурсах программы
    *
    * @param cl загрузчик класса
    * @param className имя класса
    * @return результат парсинга
    */
  def parse(cl:ClassLoader, className:JavaName):Either[String,CBegin] =
    val resName = className.raw + ".class"
    val url = cl.getResource( resName )
    if url==null then
      Left(s"resource $resName not found")
    else
      parse(url)
}
