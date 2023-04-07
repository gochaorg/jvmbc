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

/** Парсинг байт-кода аннотаций [[xyz.cofe.jvmvc.ann]] */
class AnnotationDump(private val _api:Int, atEnd:Option[Either[String,Seq[AnnCode]]=>Unit]=None)
extends AnnotationVisitor(_api)
{
  var codes:Seq[()=>AnnCode|String] = List()

  /**
   * Visits a primitive value of the annotation.
   *
   * @param name the value name.
   * @param value the actual value, whose type must be {@link Byte}, {@link Boolean}, {@link
   *     Character}, {@link Short}, {@link Integer} , {@link Long}, {@link Float}, {@link Double},
   *     {@link String} or {@link Type} of {@link Type#OBJECT} or {@link Type#ARRAY} sort. This
   *     value can also be an array of byte, boolean, short, char, int, long, float or double values
   *     (this is equivalent to using {@link #visitArray} and visiting each array element in turn,
   *     but is more convenient).
   */
  override def visit(name:String, value:AnyRef):Unit = 
    (if name!=null then APair(name,value) else APair(value)) match
      case Left(err) =>
        codes = (()=>err) +: codes
      case Right(a) =>
        codes = (()=>a) +: codes

  /**
   * Visits an enumeration value of the annotation.
   *
   * @param name the value name.
   * @param descriptor the class descriptor of the enumeration class.
   * @param value the actual enumeration value.
   */
  override def visitEnum(name:String, descriptor:String, value:String):Unit = 
    codes = (()=>AEnum(name,TDesc.unsafe(descriptor),value)) +: codes

  /**
   * Visits a nested annotation value of the annotation.
   *
   * @param name the value name.
   * @param descriptor the class descriptor of the nested annotation class.
   * @return a visitor to visit the actual nested annotation value, or {@literal null} if this
   *     visitor is not interested in visiting this nested annotation. <i>The nested annotation
   *     value must be fully visited before calling other methods on this annotation visitor</i>.
   */
  override def visitAnnotation(name:String, descriptor:String):AnnotationVisitor = 
    val adump = AnnotationDump(_api)
    codes = (()=>{
      adump.build match {
        case Left(err) =>
          err
        case Right(ok) =>
          EmANameDesc(name,TDesc.unsafe(descriptor),ok)
      }
    }) +: codes
    adump

  /**
   * Visits an array value of the annotation. Note that arrays of primitive values (such as byte,
   * boolean, short, char, int, long, float or double) can be passed as value to {@link #visit
   * visit}. This is what {@link ClassReader} does for non empty arrays of primitive values.
   *
   * @param name the value name.
   * @return a visitor to visit the actual array value elements, or {@literal null} if this visitor
   *     is not interested in visiting these values. The 'name' parameters passed to the methods of
   *     this visitor are ignored. <i>All the array values must be visited before calling other
   *     methods on this annotation visitor</i>.
   */
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

  /** Visits the end of the annotation. */
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