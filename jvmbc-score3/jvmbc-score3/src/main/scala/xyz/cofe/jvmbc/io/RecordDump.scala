package xyz.cofe.jvmbc
package io

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ModuleVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.RecordComponentVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.TypePath
import org.objectweb.asm.Attribute

import rec._

/**
order

- ( 
  - visitAnnotation | 
  - visitTypeAnnotation | 
  - visitAttribute 
- )* 
- visitEnd
*/
class RecordDump(
  private val _api:Int,
  atEnd:Option[Either[String,Seq[RecordCode]]=>Unit]=None
)
extends RecordComponentVisitor(_api) {
  var body = List[Either[String,RecordCode]]()

  /**
   * Visits an annotation of the record component.
   *
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  override def visitAnnotation(descriptor:String, visible:Boolean):AnnotationVisitor = 
    AnnotationDump(_api,Some(bodyEthier=>{
      body = bodyEthier.map { body => 
        RecAnnotation(
          TDesc(descriptor),
          visible,
          body
        )
      } +: body
    }))

  /**
   * Visits an annotation on a type in the record component signature.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#CLASS_TYPE_PARAMETER}, {@link
   *     TypeReference#CLASS_TYPE_PARAMETER_BOUND} or {@link TypeReference#CLASS_EXTENDS}. See
   *     {@link TypeReference}.
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
        RecTypeAnnotation(
          RTypeRef(typeRef),
          if typePath!=null then Some(typePath.toString) else None,
          TDesc(descriptor),
          visible,
          body
        )
      } +: body
    }))

  /**
   * Visits a non standard attribute of the record component.
   *
   * @param attribute an attribute.
   */
  override def visitAttribute(attribute:Attribute):Unit = {}

  /**
   * Visits the end of the record component. This method, which is the last one to be called, is
   * used to inform the visitor that everything have been visited.
   */
  override def visitEnd():Unit = 
    body = Right(RecEnd()) +: body
    atEnd match
      case None => 
      case Some(call) => call(build)    

  def build:Either[String,Seq[RecordCode]] = 
    import FirstErr.firstErr
    firstErr(body)
}