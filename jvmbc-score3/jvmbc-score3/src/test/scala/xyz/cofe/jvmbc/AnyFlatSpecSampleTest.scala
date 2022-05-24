package xyz.cofe.jvmbc

import org.scalatest.flatspec.AnyFlatSpec

class AnyFlatSpecSampleTest extends AnyFlatSpec {
  "An empty Set" should "have size 0" in {
    assert(Set.empty.size == 0)
  }

  it should "produce NoSuchElementException when head is invoked" in {
    assertThrows[NoSuchElementException] {
      Set.empty.head
    }
  }
}
