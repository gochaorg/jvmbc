package xyz.cofe.jvmbc.sparse

import org.scalatest.funsuite.AnyFunSuite

import xyz.cofe.jvmbc.sparse.Pattern.textMatch

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
    val idPtrn = textMatch("true")(Id.apply _)
      .alt(textMatch("false")(Id.apply _))
      .alt(textMatch("null")(Id.apply _))

    assert( idPtrn(SPtr("true",0))==Right((Id("true")),SPtr("true",4)) )
    println( idPtrn(SPtr(" false",1)) )
    assert( idPtrn(SPtr(" false",1))==Right((Id("false")),SPtr(" false",6)) )
    println( idPtrn(SPtr(" false",0)) )
  }

  case class Id2( first:Id, second:Id )

  test("seq") {
    val idPtrn1 = textMatch("true")(Id.apply _)
      .alt(textMatch("false")(Id.apply _))
      .alt(textMatch("null")(Id.apply _))

    val idPtrn2 = textMatch("one")(Id.apply _)
      .alt(textMatch("two")(Id.apply _))
      .alt(textMatch("three")(Id.apply _))
  }

  case class BOOL(str:String)
  case class DIGIT(str:String)
  case class LETTER(str:String)
  case class NULL(str:String)

  test("seq 2") {
    val src = SPtr("true1anull",0)

    val boolPtrn = textMatch("true")(BOOL.apply _)
      .alt(textMatch("false")(BOOL.apply _))

    val digitPtrn = textMatch("1")(DIGIT.apply _)
      .alt(textMatch("2")(DIGIT.apply _))

    val letterPtrn = 
      textMatch("a")(LETTER.apply _) | textMatch("b")(LETTER.apply _)

    val nullPtrn =
      textMatch("null")(NULL.apply _)

    val p2 = boolPtrn + digitPtrn
    val p3 = p2 + letterPtrn
    val p4 = p3 + nullPtrn

    p4.map( (a,b,c,d) => a )
  }
