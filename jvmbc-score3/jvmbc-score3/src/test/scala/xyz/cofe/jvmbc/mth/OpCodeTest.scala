package xyz.cofe.jvmbc.mth

import org.scalatest.funsuite.AnyFunSuite

class OpCodeTest extends AnyFunSuite:
  implicit class CodeOps( opCode:OpCode.NOP ):
    def test:Unit = {}

  test("aaa") {

    println
  }
