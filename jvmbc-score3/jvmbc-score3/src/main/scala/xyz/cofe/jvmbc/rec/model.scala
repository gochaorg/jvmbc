package xyz.cofe.jvmbc
package rec

import ann._

sealed trait RecordCode extends xyz.cofe.jvmbc.ByteCode
case class RecAnnotation(desc:TDesc, visible:Boolean, annotations:Seq[AnnCode]) extends RecordCode
case class RecTypeAnnotation(typeRef:RTypeRef, typePath:Option[String], desc:TDesc, visible:Boolean, annotations:Seq[AnnCode]) extends RecordCode
case class RecEnd() extends RecordCode