package xyz.cofe.jvmbc.sparse

import org.scalatest.funsuite.AnyFunSuite

class SParseTest extends AnyFunSuite:
  test("SPtr") {
    val sptr = SPtr("abcde",0)
    assert(sptr.hasTarget)
    assert(sptr.fetch(2) == "ab")

    val p = sptr+1
    assert( p.hasTarget )
    assert( p.fetch(2)=="bc" )

    val p1 = sptr-1
    assert( ! p1.hasTarget )
  }

  case class Id(string:String)

  test("alt test") {
    val idPtrn = matchz("true")(Id.apply _)
      .alt(matchz("false")(Id.apply _))
      .alt(matchz("null")(Id.apply _))

    assert( idPtrn.test(SPtr("true",0))==Right((Id("true")),SPtr("true",4)) )
    println( idPtrn.test(SPtr(" false",1)) )
    assert( idPtrn.test(SPtr(" false",1))==Right((Id("false")),SPtr(" false",6)) )
    println( idPtrn.test(SPtr(" false",0)) )
  }

  case class Id2( first:Id, second:Id )

  test("seq") {
    val idPtrn1 = matchz("true")(Id.apply _)
      .alt(matchz("false")(Id.apply _))
      .alt(matchz("null")(Id.apply _))

    val idPtrn2 = matchz("one")(Id.apply _)
      .alt(matchz("two")(Id.apply _))
      .alt(matchz("three")(Id.apply _))
  }

  case class BOOL(str:String)
  case class DIGIT(str:String)
  case class LETTER(str:String)
  case class NULL(str:String)

  test("seq 2") {
    val src = SPtr("true1anull",0)

    val boolPtrn = matchz("true")(BOOL.apply _)
      .alt(matchz("false")(BOOL.apply _))

    val digitPtrn = matchz("1")(DIGIT.apply _)
      .alt(matchz("2")(DIGIT.apply _))

    val letterPtrn = 
      matchz("a")(LETTER.apply _) | matchz("b")(LETTER.apply _)

    val nullPtrn =
      matchz("null")(NULL.apply _)

    val p2 = boolPtrn + digitPtrn
    val p3 = p2 + letterPtrn
    val p4 = p3 + nullPtrn

    (boolPtrn,digitPtrn,letterPtrn,nullPtrn)
  }

  trait Pars[A]:
    def pars(p:SPtr):Either[String,(A,SPtr)]

  given Pars[EmptyTuple] with
    def pars(p:SPtr):Either[String,(EmptyTuple,SPtr)] =
      Left("!!EmptyTuple")

  given [H:Pars, T <: Tuple : Pars]:Pars[H *: T] with
    def pars(p:SPtr):Either[String,(H *: T,SPtr)] =
      ???

  