package xyz.cofe.jvmbc.io

//import org.scalatest.funsuite.AnyFunSuite
import xyz.cofe.jvmbc.io.ByteCodeIO
import xyz.cofe.jvmbc.io.toBytes
import xyz.cofe.json4s3.stream.ast.FormattingJson
import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.AST
import xyz.cofe.jvmbc.*
import xyz.cofe.jvmbc.cls.*
import xyz.cofe.jvmbc.mth.*

// object GenerateByteCodeSamble:
//   def sum( a:String, b:String ):String = a + b + a

class GenerateByteCodeTest extends munit.FunSuite:
  test("generate") {
    val stringType = JavaName.java("java.lang.String")
    val stringMType = s"L${stringType.raw};"
    val targetName = "autoGen.Sample"
    val clsName = JavaName.java(targetName)
    val cb = CBegin(
      version = CVersion.v8,
      access = CBeginAccess.builder.klass.publico.build,
      name = clsName,
      superName = Some(JavaName.java("java.lang.Object")),
      methods = List(
        // constructor default
        CMethod(
          access = CMethodAccess.build.virtual.publico.build,
          name = "<init>",
          desc = MDesc("()V"),
          sign = None,
          exceptions = List.empty,
          body = List(
            MCode(),
            MLabel("begin"),
            MVarInsn(OpCode.ALOAD,0),
            MMethodInsn(OpCode.INVOKESPECIAL, "java/lang/Object", "<init>", MDesc("()V"), false),
            MInst(OpCode.RETURN),
            MLabel("end"),
            MLocalVariable("this", TDesc(s"L${clsName.raw};"),None,"begin","end",0),
            MMaxs(1,1),
            MEnd()
          )
        ),
        CMethod(
          access = CMethodAccess.build.statico.publico.build,
          name = "concat",
          desc = MDesc(s"(${stringMType}${stringMType})$stringMType"),
          sign = None,
          exceptions = List.empty,
          body = List(
            MCode(),
            MLabel("begin"),

            // new StringBuilder(0)
            MTypeInsn(OpCode.NEW, "java/lang/StringBuilder"),
            MInst(OpCode.DUP),
            MLdcInsn(LdcValue.INT(0)),
            MMethodInsn(OpCode.INVOKESPECIAL,"java/lang/StringBuilder","<init>",MDesc("(I)V"),false),

            // stringBuilder.append(a)
            MVarInsn(OpCode.ALOAD,0),
            MMethodInsn(OpCode.INVOKEVIRTUAL,"java/lang/StringBuilder","append",MDesc("(Ljava/lang/String;)Ljava/lang/StringBuilder;"),false),

            // ....append(b)
            MVarInsn(OpCode.ALOAD,1),
            MMethodInsn(OpCode.INVOKEVIRTUAL,"java/lang/StringBuilder","append",MDesc("(Ljava/lang/String;)Ljava/lang/StringBuilder;"),false),

            // ....toString()
            MMethodInsn(OpCode.INVOKEVIRTUAL,"java/lang/StringBuilder","toString",MDesc("()Ljava/lang/String;"),false),

            // return
            MInst(OpCode.ARETURN),

            MLabel("end"),

            MLocalVariable("a",TDesc("Ljava/lang/String;"),None,"begin","end",0),
            MLocalVariable("b",TDesc("Ljava/lang/String;"),None,"begin","end",1),

            MMaxs(1,1),
            MEnd()
          )
        )
      )
    )

    val byteCode = cb.toBytes
    val myCl = MyClassLoader {
       case n if n == JavaName.java(targetName) => Some(byteCode)
       case _ => None
    }

    val cl = Class.forName(targetName,true,myCl)
    println(cl)
    assert(cl.getName()==targetName)

    val concatMth = cl.getDeclaredMethods().find(_.getName()=="concat")
      .getOrElse(throw new Error("not found concat method"))

    val res = concatMth.invoke(null, "a", "b")
    println( res )
    assert( res=="ab" )
  }


  // test("sample json") {
  //   import xyz.cofe.jvmbc.io.json.given

  //   val sampleRes = this.getClass().getResource("/GenerateByteCodeSamble$.class")
  //   println(sampleRes)

  //   val srcCode = ByteCodeIO.parse(sampleRes) match
  //     case Left(err) => throw new Error(s"not parsed $err")
  //     case Right(value) => value
    
  //   implicit val fmt = FormattingJson.pretty(true)
  //   println( srcCode.json )

  //   val targetName = "autoGen.Sample"
  //   val targetCode = srcCode.copy(
  //     name = srcCode.name.rename(targetName)
  //   )

  //   targetCode.methods.foreach { meth =>
  //     println(s"meth ${meth.access} ${meth.name} ${meth.desc}")
  //     meth.body.foreach(println)
  //     println()
  //   }
  // }
