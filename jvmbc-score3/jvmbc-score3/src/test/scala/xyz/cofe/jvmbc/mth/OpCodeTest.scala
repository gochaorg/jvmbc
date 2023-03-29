package xyz.cofe.jvmbc.mth

import org.scalatest.funsuite.AnyFunSuite

class OpCodeTest extends AnyFunSuite:
  implicit class CodeOps( opCode:OpCode.NOP.type ):
    def test:Unit = {}

  test("aaa") {
    val c = OpCode.NOP
    //c.test

    println
  }
