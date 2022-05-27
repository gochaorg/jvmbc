package xyz.cofe.jvmbc
package bm

sealed trait BootstrapArg
case class Handle(tag:Int,desc:TDesc,name:String,owner:String,iface:Boolean) extends BootstrapArg
case class TypeArg(value:String) extends BootstrapArg
case class StringArg(value:String) extends BootstrapArg
case class LongArg(value:String) extends BootstrapArg
case class IntArg(value:Int) extends BootstrapArg
case class FloatArg(value:Float) extends BootstrapArg
case class DoubleArg(value:Double) extends BootstrapArg

enum LdcType:
  case INT, FLOAT, LONG, DOUBLE, STRING, OBJECT, ARRAY, METHOD, HANDLE, CONST_DYNAMIC