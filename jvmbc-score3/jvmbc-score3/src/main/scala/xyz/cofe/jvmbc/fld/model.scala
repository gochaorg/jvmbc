package xyz.cofe.jvmbc
package fld

import ann.AnnCode

sealed trait FieldCode extends ByteCode

case class FAnnotation(desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) 
  extends FieldCode with NestedAll

/** end of the method */
case class FieldEnd() extends FieldCode

case class FTypeAnnotation(typeRef:ATypeRef,typePath:Option[String],desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) 
  extends FieldCode with NestedAll
