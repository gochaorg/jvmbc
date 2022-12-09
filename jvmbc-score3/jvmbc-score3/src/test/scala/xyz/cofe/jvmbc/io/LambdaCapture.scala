package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite
import java.lang.invoke.SerializedLambda
import xyz.cofe.jvmbc.JavaName

class LambdaCapture extends AnyFunSuite {
  def capture( some:Any=>Any ):Unit = 
    println(s"capture")
    val meth = some.getClass().getDeclaredMethod("writeReplace")
    meth.setAccessible(true)
    var sl = meth.invoke(some).asInstanceOf[SerializedLambda]
    println(s"""|captured 
                |  class  ${sl.getImplClass()}
                |  sign   ${sl.getImplMethodSignature()}
                |  method ${sl.getImplMethodName()}
             """.stripMargin)
    ByteCodeIO.parse(some.getClass().getClassLoader(), JavaName(sl.getImplClass())) match
      case Left(value) => println(s"byte code not load: $value")
      case Right(value) => 
        println(s"byte code loaded")
    

  test("capture the lambda") {
    capture( (a:Any) => {
      println("this lambda")
    } )
  }
}
