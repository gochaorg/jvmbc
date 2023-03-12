package xyz.cofe.jvmbc.sparse

import org.scalatest.funsuite.AnyFunSuite

class SParse2Test extends AnyFunSuite:
  test("tupl parse 1") {
    val a = (1,true)
    val b = (a,"xyz")
    val c = (b,2L)
    val d = c
  }

  test("tupl parse 2") {
    val a = (1,true)
    val b = 1 *: true *: "xyz" *: EmptyTuple
    val c = b ++ 2L *: EmptyTuple
    val d = c
  }
