package xyz.cofe.jvmbc
package s3

import org.scalatest.funsuite.AnyFunSuite
import scala.deriving.Mirror
import mth._

trait Props[T]:
  def props(propHolder:T):List[(String,AnyRef)]

object Props:
  given Props[MIincInsn] with
    def props(inst:MIincInsn):List[(String,AnyRef)] = 
      ("var" -> inst.variable.asInstanceOf[AnyRef]) :: ("inc" -> inst.inc.asInstanceOf[AnyRef]) :: Nil
  
  // def derived[T](using Mirror.Of[T]):Props[T]=
  //   val 

class S3Test extends AnyFunSuite {
  test("simple") {
    val inc = MIincInsn(Variable(1),2)
    println(summon[Props[MIincInsn]].props(inc))

    println( inc._1 )
    println( inc.productElementNames.toList )

    val t = Tuple.fromProductTyped(inc)
    println( t.size )
    println( t.productElementName(0) )

    //Product.
  }

  test("derive 1") {
    val lnum = MLineNumber(10,Label("L10"))
  }
}
