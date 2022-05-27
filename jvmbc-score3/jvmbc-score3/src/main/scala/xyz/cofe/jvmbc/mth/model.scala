package xyz.cofe.jvmbc
package mth

import ann.AnnCode
import bm.BootstrapArg
import bm.LdcType

sealed trait MethCode
case class MCode() extends MethCode
case class MEnd() extends MethCode
case class MInst(op:OpCode) extends MethCode
case class MAnnotableParameterCount(parameterCount:Int,visible:Boolean) extends MethCode
case class MAnnotation(desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode
case class MAnnotationDefault(annotations:Seq[AnnCode]) extends MethCode
case class MFieldInsn(op:OpCode,owner:String,name:String,desc:TDesc) extends MethCode
case class MFrame(kind:Int,numLocal:Int,local:Seq[AnyRef],numStack:Int,stack:Seq[AnyRef]) extends MethCode
case class MIincInsn(variable:Int,inc:Int) extends MethCode
case class MInsn(op:OpCode) extends MethCode
case class MInsnAnnotation(typeRef:Int,typePath:String,desc:TDesc,visible:Boolean,stack:Seq[AnyRef],annotations:Seq[AnnCode]) extends MethCode
case class MIntInsn(op:OpCode,operand:Int) extends MethCode
case class MInvokeDynamicInsn(name:String,desc:MDesc,args:Seq[BootstrapArg]) extends MethCode
case class MJumpInsn(op:OpCode,label:String) extends MethCode
case class MLabel(name:String) extends MethCode
case class MLdcInsn(value:AnyRef,ldcType:LdcType) extends MethCode
case class MLineNumber(line:Int,label:String) extends MethCode
case class MLocalVariable(name:String,desc:TDesc,sign:Sign,labelStart:String,labelEnd:String,index:Int) extends MethCode
case class MLocalVariableAnnotation(typeRef:Int,typePath:String,startLabels:Seq[String],endLabels:Seq[String],index:Seq[Int],desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode
case class MLookupSwitchInsn(defaultHandle:String,keys:Seq[Int],labels:Seq[String]) extends MethCode
case class MMaxs(maxStack:Int,maxLocal:Int) extends MethCode
case class MMethodInsn(op:OpCode,owner:String,name:String,desc:TDesc,iface:Boolean) extends MethCode
case class MMultiANewArrayInsn(desc:TDesc,numDimensions:Int) extends MethCode
case class MParameter(name:String,access:Int) extends MethCode
case class MParameterAnnotation(param:Int,desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends MethCode
case class MTableSwitchInsn(min:Int,max:Int,defaultLabel:String,labels:Seq[String]) extends MethCode
case class MTryCatchAnnotation(typeRef:Int,typePath:String,desc:TDesc,visible:Boolean) extends MethCode
case class MTryCatchBlock(startLabel:String,endLabel:String,handlerLabel:String,`type`:String) extends MethCode
case class MTypeAnnotation(typeRef:Int,typePath:String,desc:TDesc,visible:Boolean) extends MethCode
case class MTypeInsn(op:OpCode,`type`:String) extends MethCode
case class MVarInsn(op:OpCode,variable:Int) extends MethCode
