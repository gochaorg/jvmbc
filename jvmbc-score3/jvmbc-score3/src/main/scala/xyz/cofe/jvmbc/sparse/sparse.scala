package xyz.cofe.jvmbc.sparse

import scala.runtime.Tuples
import scala.deriving._

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

trait Pattern[A]:
  def test(ptr:SPtr):Either[String,(A,SPtr)]
  def alt(altPattern:Pattern[A]):Pattern[A] = 
    val self = this
    new Pattern[A] {
      override def test(ptr: SPtr): Either[String, (A, SPtr)] = 
        self.test(ptr).orElse( altPattern.test(ptr) )
    }
  def seq[B](p:Pattern[B]):Pattern[(A,B)] = 
    val base = this
    new Pattern[(A,B)] {
      override def test(ptr: SPtr): Either[String, ((A, B), SPtr)] = 
        base.test(ptr).flatMap { case (a,ptr2) => 
          p.test(ptr2).map { case (b,ptr3) => 
            ( (a,b),ptr3 )
          }
        }
    }
  def +[B](p:Pattern[B]):Pattern[(A,B)] = seq[B](p)
  def |(altPattern:Pattern[A]):Pattern[A] = alt(altPattern)

def matchz[T](str:String)(tok:String=>T):Pattern[T] = 
  new Pattern[T] {
    def test(ptr: SPtr): Either[String, (T, SPtr)] = 
      if ptr.fetch(str.length()) == str 
      then
        Right((tok(str)), ptr + str.length())
      else
        Left(s"not match $str")
  }
