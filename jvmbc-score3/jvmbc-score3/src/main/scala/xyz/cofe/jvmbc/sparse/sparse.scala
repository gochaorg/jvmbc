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

trait Pattern[P]:
  def test(ptr:SPtr):Either[String,(P,SPtr)]
  def alt(altPattern:Pattern[P]):Pattern[P] = 
    val self = this
    new Pattern[P] {
      override def test(ptr: SPtr): Either[String, (P, SPtr)] = 
        self.test(ptr).orElse( altPattern.test(ptr) )
    }

def matchz[T](str:String)(tok:String=>T):Pattern[T] = 
  new Pattern[T] {
    def test(ptr: SPtr): Either[String, (T, SPtr)] = 
      if ptr.fetch(str.length()) == str 
      then
        Right((tok(str)), ptr + str.length())
      else
        Left(s"not match $str")
  }

case class Head[H,T](head:H, tail:T)
case object HNil

extension [A]( base:Pattern[A] )
  def +[B]( next:Pattern[B] ):Head[Pattern[B],Pattern[A]] = 
    Head(next,base)

extension [A,B]( hlist:Head[Pattern[B],Pattern[A]] )
  def tupled:Pattern[(A,B)] =
    new Pattern[(A,B)] {
      def test(ptr: SPtr): Either[String, ((A, B), SPtr)] = {
        hlist.tail.test(ptr).flatMap { case(a,p) => 
          hlist.head.test(p).map { case(b,p) => 
            ( (a,b), p )
          }
        }
      }
    }

extension [A,B]( from:Head[A,B] )
  def +[C]( ptrn:Pattern[C] ):Head[Pattern[C],Head[A,B]] =
    Head( ptrn, from )

trait TupledPattern[A]:
  type C
  def tupled( from:A ):Pattern[C]

object TupledPattern:
  given [A,B]:TupledPattern[Head[Pattern[B],Pattern[A]]] with
    type C = (A,B)
    override def tupled(from: Head[Pattern[B], Pattern[A]]): Pattern[C] = 
      new Pattern[C] {
        def test(ptr: SPtr): Either[String, (C, SPtr)] = 
          from.tail.test(ptr).flatMap { case (a,p) =>
            from.head.test(p).map { case (b,p) => 
              ( (a,b),p )
            }
          }
      }

  given x[A,B](using tp:TupledPattern[B]):TupledPattern[Head[Pattern[A],B]] with
    type C = (tp.C,A)
    override def tupled(from: Head[Pattern[A], B]): Pattern[C] = 
      new Pattern[C] {
        def test(ptr: SPtr): Either[String, (C, SPtr)] = 
          tp.tupled(from.tail).test(ptr).flatMap { case (c,p) => 
            from.head.test(p).map { case (a,p) => 
              ( (c,a),p )
            }
          }
      }

extension [A](hl:A)(using tp:TupledPattern[A])
  def tupled2:Pattern[tp.C] = new Pattern[tp.C] {
    def test(ptr: SPtr): Either[String, (tp.C, SPtr)] = 
      tp.tupled(hl).test(ptr)
  }

trait TupleFlatten[A <: Tuple]:
  type  Out <: Tuple
  def flatten( a:A ):Out

object TupleFlatten:
  given [A,B]: TupleFlatten[(A,B)] with
    type Out = (A,B)
    def flatten(a: (A, B)): Out = a

  given [A,B,C]: TupleFlatten[((A,B),C)] with
    type Out = (A,B,C)
    def flatten(a: ((A, B), C)): Out = (a._1._1, a._1._2, a._2)

extension [A,B,C](tupl:((A,B),C))(using fl:TupleFlatten[((A,B),C)])
  def flat:fl.Out = fl.flatten(tupl)