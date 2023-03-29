package xyz.cofe.jvmbc

import org.scalatest.funsuite.AnyFunSuite
import xyz.cofe.jvmbc.io.ByteCodeIO
import xyz.cofe.jvmbc.io.toBytes
import xyz.cofe.json4s3.stream.ast.FormattingJson
import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.AST
import xyz.cofe.jvmbc.*
import xyz.cofe.jvmbc.cls.*
import xyz.cofe.jvmbc.mth.*

// object GenerateByteCodeSamble:
//   def sum( a:String, b:String ):String = a + b + a

class GenerateByteCodeTest extends AnyFunSuite:
  test("generate") {
    val cb = CBegin(
      version = CVersion.v8,
      access = CBeginAccess.builder.klass.publico.build,
      name = JavaName.java("autoGen.Sample"),
      superName = Some(JavaName.java("java.lang.Object")),
      methods = List(
        // default
      )
    )
  }


  test("sample json") {
    import xyz.cofe.jvmbc.io.json.given

    val sampleRes = this.getClass().getResource("/GenerateByteCodeSamble$.class")
    println(sampleRes)

    val srcCode = ByteCodeIO.parse(sampleRes) match
      case Left(err) => throw new Error(s"not parsed $err")
      case Right(value) => value
    
    implicit val fmt = FormattingJson.pretty(true)
    println( srcCode.json )

    val targetName = "autoGen.Sample"
    val targetCode = srcCode.copy(
      name = srcCode.name.rename(targetName)
    )

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
