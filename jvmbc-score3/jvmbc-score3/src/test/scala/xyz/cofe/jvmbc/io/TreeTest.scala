package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.Path
import java.nio.file.Files
import xyz.cofe.jvmbc.cls.CBegin
import xyz.cofe.jvmbc.cls.CField
import xyz.cofe.jvmbc.cls.CMethod

class TreeTest extends AnyFunSuite {
  test("SampleClass.class nested tree") {
    println("-"*60)
    val sampleClass = this.getClass.getResource("/xyz/cofe/jvmbc/SampleClass.class")
    require(sampleClass!=null)

    val strm = sampleClass.openStream    
    val sampleByteCode = strm.readAllBytes
    strm.close

    val parser = ByteCodeIO.parser
    parser.parse(sampleByteCode) match
      case Left(err) => println(err)
      case Right(cbegin) =>
        cbegin.walk.foreach { tpath =>
          tpath.head match
            case c:CBegin => println( "."*tpath.length + s"CBegin ${c.name}" )
            case f:CField => println( "."*tpath.length + s"CField ${f.name} : ${f.desc}" )
            case m:CMethod => println( "."*tpath.length + s"CMethod ${m.name} ${m.desc}" )
            case None =>
            case _ => println( "."*tpath.length + " " +tpath.head.getClass )
        }
  }
}