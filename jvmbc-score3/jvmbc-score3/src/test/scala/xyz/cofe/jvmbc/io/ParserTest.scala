package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite
import java.io.File
import java.nio.file.Path
import java.nio.file.Files

class ParserTest extends AnyFunSuite {
  test("aaa") {
    // /home/uzer/code/jvmbc/jvmbc-core/target/test-classes/xyz/cofe/jvmbc/SampleClass.class
    val sampleClass = new File("/home/uzer/code/jvmbc/jvmbc-core/target/test-classes/xyz/cofe/jvmbc/SampleClass.class")
    val sampleByteCode = Files.readAllBytes(sampleClass.toPath)
    val parser = ByteCodeIO.parser
    parser.parse(sampleByteCode) match
      case Left(err) => println(err)
      case Right(cbegin) =>
        println(cbegin.name.raw)
        cbegin.fields.foreach { fld => println(s"  field ${fld.name} ${fld.desc.raw}") }
  }
}