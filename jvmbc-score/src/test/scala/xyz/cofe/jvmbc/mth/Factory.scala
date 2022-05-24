package xyz.cofe.jvmbc.mth

trait Factory {
  type Code <: MCode
  type End <: MEnd
  type Inst <: MInst
  
  def code:Code
  def end:End
  def inst(code:Int):Inst
}

object Factory {
  case class Default() extends Factory {
    override type End = MEnd
    override type Code = MCode
    override type Inst = MInst
  
    override def code: MCode = Codes.code
    override def end: MEnd = Codes.end
    override def inst(code:Int): MInst = Codes.inst(code)
  }
  
  trait Feature
  
  case class Ext() extends Factory {
    override type End = MEnd with Feature
    override type Code = MCode with Feature
    override type Inst = MInst with Feature
  
    override def code: End = Codes.code
    override def end: MCode = Codes.end
    override def inst(code:Int): Inst = Codes.inst(code)
  }
}
