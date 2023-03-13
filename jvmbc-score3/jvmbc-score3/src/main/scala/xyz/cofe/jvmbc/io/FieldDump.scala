package xyz.cofe.jvmbc
package io

import fld._
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.TypePath
import org.objectweb.asm.Attribute

/** Парсинг байт-кода поля [[xyz.cofe.jvmbc.fld]] */
class FieldDump(
  private val _api:Int,
  atEnd:Option[Either[String,Seq[FieldCode]]=>Unit]=None
)
extends FieldVisitor(_api)
{
  var body = List[Either[String,FieldCode]]()

  /**
   * Visits an annotation of the field.
   *
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitAnnotation(descriptor:String, visible:Boolean):AnnotationVisitor =
    AnnotationDump(_api,Some(bodyEthier=>{
      body = bodyEthier.map { body => 
        FAnnotation(
          TDesc(descriptor),
          visible,
          body
        )
      } +: body
    }))

  /**
   * Visits an annotation on the type of the field.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#FIELD}. See {@link TypeReference}.
   * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
   *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
   *     'typeRef' as a whole.
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitTypeAnnotation(typeRef:Int, typePath:TypePath, descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api,Some(bodyEthier=>{
      body = bodyEthier.map { body => 
        FTypeAnnotation(
          ATypeRef(typeRef),
          if typePath!=null then Some(typePath.toString) else None,
          TDesc(descriptor),
          visible,
          body
        )
      } +: body
    }))

  /**
   * Visits a non standard attribute of the field.
   *
   * @param attribute an attribute.
   */
  override def visitAttribute(attribute:Attribute):Unit = {}
  
  /**
   * Visits the end of the field. This method, which is the last one to be called, is used to inform
   * the visitor that all the annotations and attributes of the field have been visited.
   */
  override def visitEnd():Unit =
    body = Right(FieldEnd()) +: body
    atEnd match
      case None => 
      case Some(call) => call(build)    

  def build:Either[String,Seq[FieldCode]] = 
    import FirstErr.firstErr
    firstErr(body)

}