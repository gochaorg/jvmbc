package xyz.cofe.jvmbc.io

import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.Path
import java.nio.file.Files
import xyz.cofe.jvmbc.cls.CBegin
import xyz.cofe.jvmbc.cls.CField
import xyz.cofe.jvmbc.cls.CMethod
import xyz.cofe.jvmbc.Nested

class TreeTest extends AnyFunSuite {
  test("SampleClass.class nested tree") {
    println("-"*60)
    //val sampleClass = this.getClass.getResource("/xyz/cofe/jvmbc/SampleClass.class")
    val sampleClass = this.getClass.getResource("/xyz/cofe/jvmbc/SampleAnn.class")
    require(sampleClass!=null)

    val strm = sampleClass.openStream    
    val sampleByteCode = strm.readAllBytes
    strm.close

    //val parser = ByteCodeIO.parser
    ByteCodeIO.parse(sampleByteCode) match
      case Left(err) => println(err)
      case Right(cbegin) =>
        cbegin.walk.foreach { tpath =>
          tpath.head match
            case _ => 
              print( "."*tpath.length + tpath.head.getClass.getSimpleName )
              tpath.head match
                case c:CBegin =>
                  print( s"${c.name}" )
                  print( s" ver=${c.version}" )
                  print( c.superName.map { o => s" super=$o" }.getOrElse("") )
                  print( c.sign.map { o => s" sign=$o" }.getOrElse("") )
                  print( c.superName.map { o => s" super=$o" }.getOrElse("") )
                  if c.interfaces.nonEmpty then print( s" itfs=${c.interfaces}" )
                  //print( c.source.map { o => s" source=$o" }.getOrElse("") )
                case ns:Nested =>
                  ns.productElementNames.zip( ns.productIterator ).foreach { case(name,value) =>
                    if !ns.isNestedItem(name) then
                      print(value match
                        case None => ""
                        case Some(v0) => s" ${name}=${v0}"
                        case _ => s" ${name}=$value"
                      )
                  }
                case prd:Product =>
                  prd.productElementNames.zip( prd.productIterator ).foreach { case(name,value) =>
                    print(value match
                      case None => ""
                      case Some(v0) => s" ${name}=${v0}"
                      case _ => s" ${name}=$value"
                    )
                  }
              println()
        }
  }
}