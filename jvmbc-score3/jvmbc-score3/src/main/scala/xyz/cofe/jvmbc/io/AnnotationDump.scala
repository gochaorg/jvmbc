package xyz.cofe.jvmbc
package io

import ann._
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ModuleVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.RecordComponentVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.TypePath
import org.objectweb.asm.Attribute

class AnnotationDump(private val _api:Int, atEnd:Option[Either[String,Seq[AnnCode]]=>Unit]=None)
extends AnnotationVisitor(_api)
{
  var codes:Seq[()=>AnnCode|String] = List()

  override def visit(name:String, value:AnyRef):Unit = 
    (if name!=null then APair(name,value) else APair(value)) match
      case Left(err) =>
        codes = (()=>err) +: codes
      case Right(a) =>
        codes = (()=>a) +: codes
  override def visitEnum(name:String, descriptor:String, value:String):Unit = 
    codes = (()=>AEnum(name,TDesc(descriptor),value)) +: codes
  override def visitAnnotation(name:String, descriptor:String):AnnotationVisitor = 
    val adump = AnnotationDump(_api)
    codes = (()=>{
      adump.build match {
        case Left(err) =>
          err
        case Right(ok) =>
          EmANameDesc(name,TDesc(descriptor),ok)
      }
    }) +: codes
    adump
  override def visitArray(name:String):AnnotationVisitor = 
    val adump = AnnotationDump(_api)
    codes = (()=>{
      adump.build match {
        case Left(err) =>
          err
        case Right(ok) =>
          EmAArray(name,ok)
      }
    }) +: codes
    adump
  override def visitEnd():Unit = 
    codes = (()=>AEnd()) +: codes
    atEnd match
      case None =>
      case Some(call) => call(build)

  def build:Either[String,Seq[AnnCode]] =
    val vals = codes.reverse.map(call => call())
    vals.find { x => x match 
      case err:String => true
      case _ => false
    } match
      case Some(err) => Left(err.asInstanceOf[String])
      case None =>
        Right( vals.map( _.asInstanceOf[AnnCode] ) )
}