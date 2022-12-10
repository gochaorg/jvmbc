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
