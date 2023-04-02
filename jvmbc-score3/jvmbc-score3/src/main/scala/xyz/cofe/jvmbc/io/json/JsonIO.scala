package xyz.cofe.jvmbc.io.json

import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.AST

import xyz.cofe.jvmbc.*
import xyz.cofe.jvmbc.cls.*
import xyz.cofe.jvmbc.mdl.*
import xyz.cofe.jvmbc.ann.*
import xyz.cofe.jvmbc.mth.*
import xyz.cofe.jvmbc.bm.*
import xyz.cofe.json4s3.derv.errors.DervError

given [A:ToJson]:ToJson[Seq[A]] with
  override def toJson(v: Seq[A]): Option[AST] = 
    summon[ToJson[List[A]]].toJson(v.toList)

given [A:FromJson]:FromJson[Seq[A]] with
  override def fromJson(json: AST): Either[DervError, Seq[A]] = 
    summon[FromJson[List[A]]].fromJson(json)

given [A:ToJson]:ToJson[Array[A]] with
  override def toJson(t: Array[A]): Option[AST] = 
    summon[ToJson[List[A]]].toJson(t.toList)

given [A:FromJson]:FromJson[Array[A]] with
  override def fromJson(j: AST): Either[DervError, Array[A]] = ???

given ToJson[CModuleAccess] with
  override def toJson(v: CModuleAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CModuleAccess] with
  override def fromJson(j: AST): Either[DervError, CModuleAccess] = ???

given ToJson[ModRequireAccess] with
  override def toJson(v: ModRequireAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[ModRequireAccess] with
  override def fromJson(j: AST): Either[DervError, ModRequireAccess] = ???

given ToJson[ModExportAccess] with
  override def toJson(v: ModExportAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[ModExportAccess] with
  override def fromJson(j: AST): Either[DervError, ModExportAccess] = ???

given ToJson[ModOpenAccess] with
  override def toJson(v: ModOpenAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[ModOpenAccess] with
  override def fromJson(j: AST): Either[DervError, ModOpenAccess] = ???

given ToJson[CInnerClassAccess] with
  override def toJson(v: CInnerClassAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CInnerClassAccess] with
  override def fromJson(j: AST): Either[DervError, CInnerClassAccess] = ???

given ToJson[CFieldAccess] with
  override def toJson(v: CFieldAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CFieldAccess] with
  override def fromJson(j: AST): Either[DervError, CFieldAccess] = ???

given ToJson[CMethodAccess] with
  override def toJson(v: CMethodAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CMethodAccess] with
  override def fromJson(j: AST): Either[DervError, CMethodAccess] = ???
  
given ToJson[MFrameType] with
  override def toJson(v: MFrameType): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[MFrameType] with
  override def fromJson(j: AST): Either[DervError, MFrameType] = ???
  
given ToJson[MParameterAccess] with
  override def toJson(v: MParameterAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[MParameterAccess] with
  override def fromJson(j: AST): Either[DervError, MParameterAccess] = ???
  
given ToJson[Sign] with
  override def toJson(v: Sign): Option[AST] = summon[ToJson[String]].toJson(v.raw)

given FromJson[Sign] with
  override def fromJson(j: AST): Either[DervError, Sign] = ???
  
given ToJson[MSign] with
  override def toJson(v: MSign): Option[AST] = summon[ToJson[String]].toJson(v.raw)

given FromJson[MSign] with
  override def fromJson(j: AST): Either[DervError, MSign] = ???
  
given ToJson[Serializable] with
  override def toJson(ser: Serializable): Option[AST] = 
    Some( AST.JsStr(ser.toHexString()) )

given FromJson[Serializable] with
  override def fromJson(j: AST): Either[DervError, Serializable] = ???
  

given ToJson[Char] with
  override def toJson(c: Char): Option[AST] = 
    summon[ToJson[String]].toJson(s"$c")

given emArr:ToJson[EmAArray] with
  override def toJson(arr: EmAArray): Option[AST] =     
    Some(
      AST.JsObj(List("EmAArray"->
        AST.JsObj(List(
          "name" -> AST.JsStr(arr.name),
          "annotations" -> AST.JsArray(
            arr.annotations.map { 
              case p: APair => summon[ToJson[APair]].toJson(p)
              case en: AEnum => summon[ToJson[AEnum]].toJson(en)
              case arr: EmAArray => emArr.toJson(arr)
              case arr: EmANameDesc => emAName.toJson(arr)
              case end: AEnd => summon[ToJson[AEnd]].toJson(end)
            }.flatten)
        ))
      ))
    )

given emAName:ToJson[EmANameDesc] with
  override def toJson(em: EmANameDesc): Option[AST] = 
    Some(
      AST.JsObj(List("EmANameDesc"->
        AST.JsObj((
        List(
          "name"->Option(AST.JsStr(em.name)),
          "desc"->summon[ToJson[TDesc]].toJson(em.desc),
          "annotations"->Some(AST.JsArray(
            em.annotations.map {
              case p: APair => summon[ToJson[APair]].toJson(p)
              case en: AEnum => summon[ToJson[AEnum]].toJson(en)
              case arr: EmAArray => emArr.toJson(arr)
              case arr: EmANameDesc => emAName.toJson(arr)
              case end: AEnd => summon[ToJson[AEnd]].toJson(end)
            }.flatten
          ))
        )
        ).flatMap( (n,v) => v match
          case None => List.empty
          case Some(value) => List((n,value))
        )
      ))
    ))

given opCode:ToJson[OpCode] with
  override def toJson(op: OpCode): Option[AST] =     
    Some(AST.JsStr(op.name()))

given constDyn:ToJson[ConstDynamic] with
  override def toJson(cd: ConstDynamic): Option[AST] = 
    Some(
      AST.JsObj(List("ConstDynamic"->
        AST.JsObj(List(
          "name" -> Option(AST.JsStr(cd.name)),
          "desc" -> Option(AST.JsStr(cd.desc)),
          "handle" -> summon[ToJson[Handle]].toJson(cd.handle),
          "args" -> Option(AST.JsArray(
            {cd.args.map {
              case v@Handle(tag, desc, name, owner, iface) => summon[ToJson[Handle]].toJson(v)
              case v@TypeArg(value) => summon[ToJson[TypeArg]].toJson(v)
              case v@StringArg(value) => summon[ToJson[StringArg]].toJson(v)
              case v@LongArg(value) => summon[ToJson[LongArg]].toJson(v)
              case v@IntArg(value) => summon[ToJson[IntArg]].toJson(v)
              case v@FloatArg(value) => summon[ToJson[FloatArg]].toJson(v)
              case v@DoubleArg(value) => summon[ToJson[DoubleArg]].toJson(v)
              case v@ConstDynamic(name, desc, handle, args) => constDyn.toJson(v)
            }}.flatten
          ))
        ).flatMap( (n,v) => v match
          case None => List.empty
          case Some(value) => List((n,value))
        ))
      ))
    )

// def toJson(c:CModule):String = 
//   c.json

// def fromJson(str:String) =
//   str.jsonAs[AnnPair]