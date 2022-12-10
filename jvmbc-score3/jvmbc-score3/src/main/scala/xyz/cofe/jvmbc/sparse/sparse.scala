package xyz.cofe.jvmbc.sparse

case class SPtr(string:String, value:Int):
  def hasTarget:Boolean = value >= 0 && value < string.length()
  def available:Int = 
    if hasTarget then
      string.length() - value
    else
      0

  def fetch(size:Int):String =
    if hasTarget then
      string.substring(value, value + (size min available))
    else
      ""

  def +(offsetValue:Int):SPtr = copy(value = value+offsetValue)
  def -(offsetValue:Int):SPtr = copy(value = value-offsetValue)

trait Pattern[P]:
  def test(ptr:SPtr):Either[String,(P,SPtr)]
