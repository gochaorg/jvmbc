package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite
import java.lang.invoke.SerializedLambda
import xyz.cofe.jvmbc.parse.desc.{ObjectType => JavaName}
import xyz.cofe.jvmbc.mth.MMethodInsn
import xyz.cofe.jvmbc.mth.OpCode

class LambdaCapture extends AnyFunSuite {
  test("lambda 1") {
    println("""
      |lambda 1
      |=================
      """.stripMargin)

    ByteCodeIO.lambda.parse( (a:Any) => {
      println("this lambda")
    }).foreach( (_,cm) => 
      println("decl")
      println(s"${cm.name} ${cm.desc} ${cm.sign}")

      println("body")
      cm.body.foreach( println )
    )
  }

  test("lambda 2") {
    println("""
      |lambda 2
      |=================
      """.stripMargin)

    ByteCodeIO.lambda.parse( (a:Int, b:String) => {
      println(s"this lambda $a  $b")
    }).foreach( (cb,cm) => 
      println("decl")
      println(s"${cm.name} ${cm.desc} ${cm.sign}")

      println("body")
      cm.body.foreach( println )

      /////////////
      cm.body.flatMap {
        case ins @ MMethodInsn.InvokeStatic(
          JavaName("xyz/cofe/jvmbc/io/LambdaCapture"), name, desc, iface) => 
          List(ins)
        case _ => List.empty
      }.flatMap( mi => cb.methods.find(m => m.name==mi.name && m.desc.raw == mi.desc.raw).fold(List.empty)(m=>List(m)) )
      .foreach { cm => 
        println("decl")
        println(s"${cm.name} ${cm.desc} ${cm.sign}")

        println("body")
        cm.body.foreach( println )
      }
    )
  }

  test("repeat sample") {
    ByteCodeIO.lambda.parse( (str:String, cnt:Int)=>str.repeat(cnt) ).foreach{ (cb,cm) => 
      println("decl")
      println(s"${cm.name} ${cm.desc} ${cm.sign}")

      println("body")
      cm.body.foreach( println ) 
    }
  }
}
