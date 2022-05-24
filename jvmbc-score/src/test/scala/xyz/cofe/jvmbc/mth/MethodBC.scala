package xyz.cofe.jvmbc.mth

trait MCode
trait MEnd
trait MInst { val code:Int }

object Codes {
  def code:MCode = new MCode {}
  def end:MEnd = new MEnd {}
  def inst(code0:Int) = new MInst {
    override val code: Int = code0
  }
}
