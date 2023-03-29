package xyz.cofe.jvmbc

import org.scalatest.funsuite.AnyFunSuite
import xyz.cofe.jvmbc.io.ByteCodeIO
import xyz.cofe.jvmbc.io.toBytes
import xyz.cofe.json4s3.stream.ast.FormattingJson
import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.AST
import xyz.cofe.jvmbc.cls.CModule

object GenerateByteCodeSamble:
  def sum( a:String, b:String ):String = a + b + a

import GenerateByteCodeTest.*

object GenerateByteCodeTest:
  class MyClassLoader( byteCodeOf:JavaName=>Option[Array[Byte]] ) extends ClassLoader("myClassloader",classOf[GenerateByteCodeTest].getClassLoader()):
    override def findClass(name: String):Class[?] =
      if name!=null then byteCodeOf(JavaName.java(name)) match
        case None => super.findClass(name)
        case Some(bytes) => defineClass(bytes,0,bytes.length)
      else
        super.findClass(name)

  given [A:ToJson]:ToJson[Seq[A]] with
    override def toJson(v: Seq[A]): Option[AST] = 
      summon[ToJson[List[A]]].toJson(v.toList)

class GenerateByteCodeTest extends AnyFunSuite:  
  import GenerateByteCodeTest.given
  test("generate") {
    val sampleRes = this.getClass().getResource("/GenerateByteCodeSamble$.class")
    println(sampleRes)

    val srcCode = ByteCodeIO.parse(sampleRes) match
      case Left(err) => throw new Error(s"not parsed $err")
      case Right(value) => value
    
    val targetName = "autoGen.Sample"

    val targetCode = srcCode.copy(
      name = srcCode.name.rename(targetName)
    )

    implicit val fmt = FormattingJson.pretty(true)
    // val cmodule : CModule = ???
    // cmodule.json

    // targetCode.methods.foreach { meth =>
    //   println(s"meth ${meth.access} ${meth.name} ${meth.desc}")
    //   meth.body.foreach(println)
    //   println()
    // }

    // val myCl = MyClassLoader {
    //   case n if n == JavaName.java(targetName) => Some(targetCode.toBytes)
    //   case _ => None
    // }

    // val cl = Class.forName(targetName,true,myCl)
    // println(cl)
  }
