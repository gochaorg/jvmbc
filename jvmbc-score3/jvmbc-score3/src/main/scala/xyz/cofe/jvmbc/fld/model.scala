package xyz.cofe.jvmbc
package fld

import ann.AnnCode

sealed trait FieldCode
case class FAnnotation(desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends FieldCode

/** end of the method */
case class FieldEnd() extends FieldCode
case class FTypeAnnotation(typeRef:Int,typePath:String,desc:TDesc,visible:Boolean) extends FieldCode
