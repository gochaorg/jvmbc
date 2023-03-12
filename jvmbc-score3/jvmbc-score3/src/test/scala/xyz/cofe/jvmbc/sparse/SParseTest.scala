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

    val seq2HL = idPtrn1 + idPtrn2
    val seq2Tpl = seq2HL.tupled
    println( seq2Tpl.test(SPtr("trueone",0)) )
    println( seq2Tpl.test(SPtr("truetwo",0)) )

    val seq3HL = idPtrn1 + idPtrn2 + idPtrn1
    seq3HL.tupled2.test(SPtr("truetwonull",0)).foreach{ x => 
    }

    val seq4HL = idPtrn1 + idPtrn2 + idPtrn1 + idPtrn2
  }

  case class BOOL(str:String)
  case class DIGIT(str:String)
  case class NUM(str:String)

  test("seq 2") {
    val src = SPtr("true1twoid-b",0)

    val boolPtrn = matchz("true")(BOOL.apply _)
      .alt(matchz("false")(BOOL.apply _))

    val digitPtrn = matchz("1")(DIGIT.apply _)
      .alt(matchz("2")(DIGIT.apply _))

    println( (boolPtrn + digitPtrn).tupled2.test(src) )

    val numPtrn = matchz("one")(NUM.apply _)
      .alt(matchz("two")(NUM.apply _))

    val ptrn3a = boolPtrn + digitPtrn + numPtrn
    val ptrn3 = ptrn3a.tupled2
    println( ptrn3.test(src) )

    println("!!!")
    val res3 = ptrn3.test(src)
    res3.foreach{ case (res,_) =>
      val ((a,b),c) = res
      println(s"$a $b $c")
    }

    // ptrn3.test(src).foreach { case (res,_) => 
    //   val ((a:BOOL, b:DIGIT), c:NUM) = res
    //   println( (a,b,c) )
    // }

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