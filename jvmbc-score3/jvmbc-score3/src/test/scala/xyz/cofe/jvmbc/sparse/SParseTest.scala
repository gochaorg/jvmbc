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
    val idPtrn = matchz("true")(Id)
      .alt(matchz("false")(Id))
      .alt(matchz("null")(Id))

    assert( idPtrn.test(SPtr("true",0))==Right((Id("true")),SPtr("true",4)) )
    println( idPtrn.test(SPtr(" false",1)) )
    assert( idPtrn.test(SPtr(" false",1))==Right((Id("false")),SPtr(" false",6)) )
    println( idPtrn.test(SPtr(" false",0)) )
  }

  case class Id2( first:Id, second:Id )

  test("seq") {
    val idPtrn1 = matchz("true")(Id)
      .alt(matchz("false")(Id.apply _))
      .alt(matchz("null")(Id))

    val idPtrn2 = matchz("one")(Id)
      .alt(matchz("two")(Id))
      .alt(matchz("three")(Id))

    val seq2HL = idPtrn1 + idPtrn2
    val seq2Tpl = seq2HL.tupled
    println( seq2Tpl.test(SPtr("trueone",0)) )
    println( seq2Tpl.test(SPtr("truetwo",0)) )

    val seq3HL = idPtrn1 + idPtrn2 + idPtrn1
    seq3HL.tupled2.test(null).foreach{ x => 
    }

    val seq4HL = idPtrn1 + idPtrn2 + idPtrn1 + idPtrn2
  }

  case class BOOL(str:String)
  case class DIGIT(str:String)
  case class NUM(str:String)

  test("seq 2") {
    val src = SPtr("true1twoid-b",0)

    val boolPtrn = matchz("true")(BOOL)
      .alt(matchz("false")(BOOL))

    val digitPtrn = matchz("1")(DIGIT)
      .alt(matchz("2")(DIGIT))

    println( (boolPtrn + digitPtrn).tupled2.test(src) )

    val numPtrn = matchz("one")(NUM)
      .alt(matchz("two")(NUM))

    val ptrn3 = (boolPtrn + digitPtrn + numPtrn).tupled2
    println( ptrn3.test(src) )

    ptrn3.test(src).foreach { case (res,_) => 
      val ((a:BOOL, b:DIGIT), c:NUM) = res
      println( (a,b,c) )
    }

    // val idPtrn1 = matchz("id-a")(Id)
    //   .alt(matchz("id-b")(Id))

    // val ptrnHL = boolPtrn + digitPtrn + numPtrn + idPtrn1
  }

  test("flatten") {
    val tup = (((10,"str"),false),'a')
    val x = tup.last
    val y = tup.flat
    //val z = y.flat
  }