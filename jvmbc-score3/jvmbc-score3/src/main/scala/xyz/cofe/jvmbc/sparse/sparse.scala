package xyz.cofe.jvmbc.sparse

import scala.runtime.Tuples
import scala.deriving._
import scala.reflect.ClassTag

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
  def apply(ptr:SPtr):Either[String,(A,SPtr)]
  def map[B](f:A=>B):Pattern[B] = 
    val self = this
    new Pattern[B] {
      override def apply(ptr: SPtr): Either[String, (B, SPtr)] = 
        self(ptr).map( (a,ptr) => (f(a),ptr) )
    }

  def alt(altPattern:Pattern[A]):Pattern[A] = 
    val self = this
    new Pattern[A] {
      override def apply(ptr: SPtr): Either[String, (A, SPtr)] = 
        self(ptr).orElse( altPattern(ptr) )
    }
  def |(altPattern:Pattern[A]):Pattern[A] = alt(altPattern)

  def seq[B](p:Pattern[B]):Pattern[(A,B)] = 
    val base = this
    new Pattern[(A,B)] {
      override def apply(ptr: SPtr): Either[String, ((A, B), SPtr)] = 
        base(ptr).flatMap { case (a,ptr2) => 
          p(ptr2).map { case (b,ptr3) => 
            ( (a,b),ptr3 )
          }
        }
    }
  def +[B](p:Pattern[B]):Pattern[(A,B)] = seq[B](p)

object Pattern:
  def textMatch[T](str:String)(tok:String=>T):Pattern[T] = 
    new Pattern[T] {
      def apply(ptr: SPtr): Either[String, (T, SPtr)] = 
        if ptr.fetch(str.length()) == str 
        then
          Right((tok(str)), ptr + str.length())
        else
          Left(s"not match $str")
    }

extension [A,B,C]( ptrn:Pattern[((A,B),C)] )
  def map[Z]( f:(A,B,C)=>Z ):Pattern[Z] = ptrn.map { case((a,b),c) => f(a,b,c) }

extension [A,B,C,D]( ptrn:Pattern[(((A,B),C),D)] )
  def map[Z]( f:(A,B,C,D)=>Z ):Pattern[Z] = ptrn.map { case(((a,b),c),d) => f(a,b,c,d) }  

extension [A,B,C,D,E]( ptrn:Pattern[((((A,B),C),D),E)] )
  def map[Z]( f:(A,B,C,D,E)=>Z ):Pattern[Z] = ptrn.map { case((((a,b),c),d),e) => f(a,b,c,d,e) }

extension [A,B,C,D,E,F]( ptrn:Pattern[(((((A,B),C),D),E),F)] )
  def map[Z]( f:(A,B,C,D,E,F)=>Z ):Pattern[Z] = ptrn.map { case(((((a,b),c),d),e),f0) => f(a,b,c,d,e,f0) }

extension [A,B,C,D,E,F,G]( ptrn:Pattern[((((((A,B),C),D),E),F),G)] )
  def map[Z]( f:(A,B,C,D,E,F,G)=>Z ):Pattern[Z] = ptrn.map { case((((((a,b),c),d),e),f0),g) => f(a,b,c,d,e,f0,g) }

extension [A,B,C,D,E,F,G,H]( ptrn:Pattern[(((((((A,B),C),D),E),F),G),H)] )
  def map[Z]( f:(A,B,C,D,E,F,G,H)=>Z ):Pattern[Z] = ptrn.map { case(((((((a,b),c),d),e),f0),g),h) => f(a,b,c,d,e,f0,g,h) }
