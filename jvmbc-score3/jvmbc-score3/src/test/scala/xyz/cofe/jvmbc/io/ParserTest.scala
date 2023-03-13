package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite
import java.io.File
import java.nio.file.Path
import java.nio.file.Files
import xyz.cofe.jvmbc.cls.CBegin
import xyz.cofe.jvmbc.cls.CField
import xyz.cofe.jvmbc.cls.CMethod

class ParserTest extends AnyFunSuite {
  test("SampleClass.class") {
    val sampleClass = this.getClass.getResource("/xyz/cofe/jvmbc/SampleClass.class")
    require(sampleClass!=null)

    val strm = sampleClass.openStream    
    val sampleByteCode = strm.readAllBytes
    strm.close

    //val parser = ByteCodeIO.parser
    ByteCodeIO.parse(sampleByteCode) match
      case Left(err) => println(err)
      case Right(cbegin) =>
        println(cbegin.name)
        cbegin.fields.foreach { fld => println(s"  field ${fld.name} desc=${fld.desc.raw} sign=${fld.sign}") }
        cbegin.methods.foreach { mth => println(s"  meth ${mth.name} desc=${mth.desc} sign=${mth.sign}") }
        cbegin.methods(1).body.foreach {println}
  }
}