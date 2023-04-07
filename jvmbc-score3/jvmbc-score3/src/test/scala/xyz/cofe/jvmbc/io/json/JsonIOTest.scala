package xyz.cofe.jvmbc.io.json

import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.*
import xyz.cofe.jvmbc.ann.*

class JsonIOTest extends munit.FunSuite:
  test("APair 2 json") {    
    println("APair 2 json\n"+"="*60)

    implicit val x = FormattingJson.pretty(true)
    println(
      (APair.BoolV(Some("name"), true):AnnCode).json
    )
  }
