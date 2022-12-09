package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite
import xyz.cofe.json4s3.derv.ToJson
import xyz.cofe.json4s3.derv._
import xyz.cofe.json4s3.derv
import xyz.cofe.json4s3.stream.ast.FormattingJson
import xyz.cofe.json4s3.stream.ast.AST

case class Some1(a:String="a")

class JsonTest extends AnyFunSuite {
  given [V:ToJson]:ToJson[Seq[V]] with
    def toJson(v:Seq[V]):Option[AST] = summon[ToJson[List[V]]].toJson(v.toList)

  test("try json") {
    val sampleClass = this.getClass.getResource("/xyz/cofe/jvmbc/SampleAnn.class")
    require(sampleClass!=null)

    val strm = sampleClass.openStream    
    val sampleByteCode = strm.readAllBytes
    strm.close

    //val parser = ByteCodeIO.parser
    ByteCodeIO.parse(sampleByteCode) match
      case Left(err) => 
        println(err)
      case Right(cbegin) =>
        println(s"parsed $sampleClass")

        implicit val fmt = FormattingJson.formatting.pretty(true)
        println( cbegin.name.json )
  }
}
