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
import scala.reflect.ClassTag
import xyz.cofe.json4s3.derv.errors.TypeCastFail
import xyz.cofe.json4s3.derv.errors.FieldNotFound
import scala.util.Try
import scala.util.Failure
import scala.util.Success

given [A:ToJson]:ToJson[Seq[A]] with
  override def toJson(v: Seq[A]): Option[AST] = 
    summon[ToJson[List[A]]].toJson(v.toList)

given [A:FromJson]:FromJson[Seq[A]] with
  override def fromJson(json: AST): Either[DervError, Seq[A]] = 
    summon[FromJson[List[A]]].fromJson(json)

given [A:ToJson]:ToJson[Array[A]] with
  override def toJson(t: Array[A]): Option[AST] = 
    summon[ToJson[List[A]]].toJson(t.toList)

given [A:FromJson:ClassTag]:FromJson[Array[A]] with
  override def fromJson(j: AST): Either[DervError, Array[A]] = 
    summon[FromJson[List[A]]].fromJson(j).map(ls => ls.toArray)

given ToJson[CModuleAccess] with
  override def toJson(v: CModuleAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CModuleAccess] with
  override def fromJson(j: AST): Either[DervError, CModuleAccess] = 
    summon[FromJson[Int]].fromJson(j).map(CModuleAccess(_))

given ToJson[ModRequireAccess] with
  override def toJson(v: ModRequireAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[ModRequireAccess] with
  override def fromJson(j: AST): Either[DervError, ModRequireAccess] = 
    summon[FromJson[Int]].fromJson(j).map(ModRequireAccess(_))

given ToJson[ModExportAccess] with
  override def toJson(v: ModExportAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[ModExportAccess] with
  override def fromJson(j: AST): Either[DervError, ModExportAccess] = 
    summon[FromJson[Int]].fromJson(j).map(ModExportAccess(_))

given ToJson[ModOpenAccess] with
  override def toJson(v: ModOpenAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[ModOpenAccess] with
  override def fromJson(j: AST): Either[DervError, ModOpenAccess] = 
    summon[FromJson[Int]].fromJson(j).map(ModOpenAccess(_))

given ToJson[CInnerClassAccess] with
  override def toJson(v: CInnerClassAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CInnerClassAccess] with
  override def fromJson(j: AST): Either[DervError, CInnerClassAccess] = 
    summon[FromJson[Int]].fromJson(j).map(CInnerClassAccess(_))

given ToJson[CFieldAccess] with
  override def toJson(v: CFieldAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CFieldAccess] with
  override def fromJson(j: AST): Either[DervError, CFieldAccess] = 
    summon[FromJson[Int]].fromJson(j).map(CFieldAccess(_))

given ToJson[CMethodAccess] with
  override def toJson(v: CMethodAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[CMethodAccess] with
  override def fromJson(j: AST): Either[DervError, CMethodAccess] = 
    summon[FromJson[Int]].fromJson(j).map(CMethodAccess(_))
  
given ToJson[MFrameType] with
  override def toJson(v: MFrameType): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[MFrameType] with
  override def fromJson(j: AST): Either[DervError, MFrameType] = 
    summon[FromJson[Int]].fromJson(j).map(MFrameType(_))
  
given ToJson[MParameterAccess] with
  override def toJson(v: MParameterAccess): Option[AST] = summon[ToJson[Int]].toJson(v.raw)

given FromJson[MParameterAccess] with
  override def fromJson(j: AST): Either[DervError, MParameterAccess] = 
    summon[FromJson[Int]].fromJson(j).map(MParameterAccess(_))
  
given ToJson[Sign] with
  override def toJson(v: Sign): Option[AST] = summon[ToJson[String]].toJson(v.raw)

given FromJson[Sign] with
  override def fromJson(j: AST): Either[DervError, Sign] = 
    summon[FromJson[String]].fromJson(j).map(Sign(_))
  
given ToJson[MSign] with
  override def toJson(v: MSign): Option[AST] = summon[ToJson[String]].toJson(v.raw)

given FromJson[MSign] with
  override def fromJson(j: AST): Either[DervError, MSign] = 
    summon[FromJson[String]].fromJson(j).map(MSign(_))

given ToJson[TDesc] with
  override def toJson(v: TDesc): Option[AST] = summon[ToJson[String]].toJson(v.raw)

given FromJson[TDesc] with
  override def fromJson(j: AST): Either[DervError, TDesc] = 
    summon[FromJson[String]].fromJson(j).map(TDesc.unsafe(_))
  

given ToJson[Serializable] with
  override def toJson(ser: Serializable): Option[AST] = 
    Some( AST.JsStr(ser.toHexString()) )

given FromJson[Serializable] with
  override def fromJson(j: AST): Either[DervError, Serializable] = 
    summon[FromJson[String]].fromJson(j).flatMap { str =>
      str.deSerialize().left.map(TypeCastFail(_))
    }
  
given ToJson[TDesc | MDesc] with
  override def toJson(tOrM: TDesc | MDesc): Option[AST] = 
    tOrM match
      case td:TDesc => summon[ToJson[TDesc]].toJson(td).map( a => AST.JsObj(List("TDesc" -> a)) )
      case md:MDesc => summon[ToJson[MDesc]].toJson(md).map( a => AST.JsObj(List("MDesc" -> a)) )

given FromJson[TDesc | MDesc] with
  override def fromJson(json: AST): Either[DervError, TDesc | MDesc] = 
    json match
      case ob @ AST.JsObj(value) => 
        ob.get("TDesc").toRight(FieldNotFound("TDesc not found")).flatMap(ast => summon[FromJson[TDesc]].fromJson(ast))
          .orElse(
            ob.get("MDesc").toRight(FieldNotFound("MDesc not found")).flatMap(ast => summon[FromJson[MDesc]].fromJson(ast))
          )
      case _ =>
        Left(TypeCastFail("expect JsObj"))

given ToJson[Char] with
  override def toJson(c: Char): Option[AST] = 
    summon[ToJson[String]].toJson(s"$c")

given FromJson[Char] with
  override def fromJson(j: AST): Either[DervError, Char] = 
    summon[FromJson[String]].fromJson(j).flatMap { str =>
      if str.length()<1
      then Left(TypeCastFail("string is empty"))
      else Right(str.charAt(0))
    }

given emArr2Json:ToJson[EmAArray] with
  override def toJson(arr: EmAArray): Option[AST] =     
    Some(
      AST.JsObj(List("EmAArray"->
        AST.JsObj(List(
          "name" -> AST.JsStr(arr.name),
          "annotations" -> AST.JsArray(
            arr.annotations.map { 
              case p: APair => summon[ToJson[APair]].toJson(p)
              case en: AEnum => summon[ToJson[AEnum]].toJson(en)
              case arr: EmAArray => emArr2Json.toJson(arr)
              case arr: EmANameDesc => emAName2Json.toJson(arr)
              case end: AEnd => summon[ToJson[AEnd]].toJson(end)
            }.flatten)
        ))
      ))
    )

given emArr4Json:FromJson[EmAArray] with
  override def fromJson(json: AST): Either[DervError, EmAArray] = 
    json match
      case js @ AST.JsObj(value) => 
        val name = js.get("name").toRight(FieldNotFound("name")).flatMap { ast => 
          summon[FromJson[String]].fromJson(ast)
        }

        val anns = js.get("annotations").toRight(FieldNotFound("annotations")).flatMap { ast =>
          ast match
            case AST.JsArray(lst) => 
              lst.map { ast => 
                ast match
                  case js @ AST.JsObj(value) =>
                    val a0 = js.get("APair").toRight(TypeCastFail("expect APair")).flatMap( ast => summon[FromJson[APair]].fromJson(ast) )
                    val a1 = js.get("AEnum").toRight(TypeCastFail("expect AEnum")).flatMap( ast => summon[FromJson[AEnum]].fromJson(ast) )
                    val a2 = js.get("EmAArray").toRight(TypeCastFail("expect EmAArray")).flatMap( ast => summon[FromJson[EmAArray]].fromJson(ast) )
                    val a3 = js.get("EmANameDesc").toRight(TypeCastFail("expect EmANameDesc")).flatMap( ast => summon[FromJson[EmANameDesc]].fromJson(ast) )
                    val a4 = js.get("AEnd").toRight(TypeCastFail("expect AEnd")).flatMap( ast => summon[FromJson[AEnd]].fromJson(ast) )
                    a0.orElse(a1).orElse(a2).orElse(a3).orElse(a4)
                  case _ => Left(TypeCastFail(s"can't decode annotations array value, expect JsObj"))
              }.foldLeft( Right(List.empty[AnnCode]):Either[DervError,List[AnnCode]] ){ case (rs,it) => 
                rs.flatMap( rs => 
                  it.map( it => rs :+ it )
                )
              }
            case _ => Left(TypeCastFail(s"can't decode annotations field, expect JsArray"))
        }

        name.flatMap { name =>
          anns.map { anns =>
            EmAArray(name,anns)
          }
        }
      case _ => Left(TypeCastFail(s"can't decode EmAArray, expect JsObj"))

given emAName2Json:ToJson[EmANameDesc] with
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
              case arr: EmAArray => emArr2Json.toJson(arr)
              case arr: EmANameDesc => emAName2Json.toJson(arr)
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

given emAName4Json:FromJson[EmANameDesc] with
  override def fromJson(json: AST): Either[DervError, EmANameDesc] = 
    json match
      case js @ AST.JsObj(value) => 
        val name = js.get("name").toRight(FieldNotFound("name")).flatMap { ast => 
          summon[FromJson[String]].fromJson(ast)
        }
        val desc = js.get("desc").toRight(FieldNotFound("desc")).flatMap { ast => 
          summon[FromJson[TDesc]].fromJson(ast)
        }
        val anns = js.get("annotations").toRight(FieldNotFound("annotations")).flatMap { ast =>
          ast match
            case AST.JsArray(lst) => 
              lst.map { ast => 
                ast match
                  case js @ AST.JsObj(value) =>
                    val a0 = js.get("APair").toRight(TypeCastFail("expect APair")).flatMap( ast => summon[FromJson[APair]].fromJson(ast) )
                    val a1 = js.get("AEnum").toRight(TypeCastFail("expect AEnum")).flatMap( ast => summon[FromJson[AEnum]].fromJson(ast) )
                    val a2 = js.get("EmAArray").toRight(TypeCastFail("expect EmAArray")).flatMap( ast => summon[FromJson[EmAArray]].fromJson(ast) )
                    val a3 = js.get("EmANameDesc").toRight(TypeCastFail("expect EmANameDesc")).flatMap( ast => summon[FromJson[EmANameDesc]].fromJson(ast) )
                    val a4 = js.get("AEnd").toRight(TypeCastFail("expect AEnd")).flatMap( ast => summon[FromJson[AEnd]].fromJson(ast) )
                    a0.orElse(a1).orElse(a2).orElse(a3).orElse(a4)
                  case _ => Left(TypeCastFail(s"can't decode annotations array value, expect JsObj"))
              }.foldLeft( Right(List.empty[AnnCode]):Either[DervError,List[AnnCode]] ){ case (rs,it) => 
                rs.flatMap( rs => 
                  it.map( it => rs :+ it )
                )
              }
            case _ => Left(TypeCastFail(s"can't decode annotations field, expect JsArray"))
        }

        name.flatMap { name =>
          desc.flatMap { desc =>
            anns.map { anns =>
              EmANameDesc(name,desc,anns)
            }
          }
        }
      case _ => Left(TypeCastFail(s"can't decode EmANameDesc, expect JsObj"))

given opCode2Json:ToJson[OpCode] with
  override def toJson(op: OpCode): Option[AST] =     
    Some(AST.JsStr(op.name()))

given opCode4Json:FromJson[OpCode] with
  override def fromJson(j: AST): Either[DervError, OpCode] = 
    summon[FromJson[String]].fromJson(j).flatMap(str => {
      Try(OpCode.valueOf(str)) match
        case Failure(exception) => Left(TypeCastFail(s"can't decode OpCode from $str"))
        case Success(value) => Right(value)
    })

given constDyn2Json:ToJson[ConstDynamic] with
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
              case v@ConstDynamic(name, desc, handle, args) => constDyn2Json.toJson(v)
            }}.flatten
          ))
        ).flatMap( (n,v) => v match
          case None => List.empty
          case Some(value) => List((n,value))
        ))
      ))
    )

given constDyn4Json:FromJson[ConstDynamic] with
  override def fromJson(json: AST): Either[DervError, ConstDynamic] = {
    json match
      case ob @ AST.JsObj(value) =>
        ob.get("ConstDynamic") match
          case Some(value) => value match
            case ob @ AST.JsObj(value) =>
              val name = ob.get("name").toRight(FieldNotFound("name in ConstDynamic")).flatMap(a => summon[FromJson[String]].fromJson(a))
              val desc = ob.get("desc").toRight(FieldNotFound("desc in ConstDynamic")).flatMap(a => summon[FromJson[String]].fromJson(a))
              val handle = ob.get("handle").toRight(FieldNotFound("handle in ConstDynamic")).flatMap(a => summon[FromJson[Handle]].fromJson(a))
              val args = ob.get("args") match
                case Some(AST.JsArray(arr)) => 
                  arr.map { 
                    case ob @ AST.JsObj(value) => 
                      ob.get("Handle").toRight(FieldNotFound("expect key Handle")).flatMap(ast => summon[FromJson[Handle]].fromJson(ast):Either[DervError,BootstrapArg])
                        .orElse(ob.get("TypeArg").toRight(FieldNotFound("expect key TypeArg")).flatMap(ast => summon[FromJson[TypeArg]].fromJson(ast):Either[DervError,BootstrapArg]))
                        .orElse(ob.get("StringArg").toRight(FieldNotFound("expect key StringArg")).flatMap(ast => summon[FromJson[StringArg]].fromJson(ast):Either[DervError,BootstrapArg]))
                        .orElse(ob.get("LongArg").toRight(FieldNotFound("expect key LongArg")).flatMap(ast => summon[FromJson[LongArg]].fromJson(ast):Either[DervError,BootstrapArg]))
                        .orElse(ob.get("IntArg").toRight(FieldNotFound("expect key IntArg")).flatMap(ast => summon[FromJson[IntArg]].fromJson(ast):Either[DervError,BootstrapArg]))
                        .orElse(ob.get("FloatArg").toRight(FieldNotFound("expect key FloatArg")).flatMap(ast => summon[FromJson[FloatArg]].fromJson(ast):Either[DervError,BootstrapArg]))
                        .orElse(ob.get("DoubleArg").toRight(FieldNotFound("expect key DoubleArg")).flatMap(ast => summon[FromJson[DoubleArg]].fromJson(ast):Either[DervError,BootstrapArg]))
                        .orElse(ob.get("ConstDynamic").toRight(FieldNotFound("expect key ConstDynamic")).flatMap(ast => constDyn4Json.fromJson(ast):Either[DervError,BootstrapArg]))
                    case _ =>
                      Left(TypeCastFail("field args[i] expect JsObj"))
                  }.foldLeft( Right(List.empty):Either[DervError,List[BootstrapArg]] ){ case (res,itm) => 
                    res.flatMap( res => 
                      itm.map( itm => res :+ itm )
                    )
                  }
                case _ => Left(TypeCastFail("field args not found in ConstDynamic or not array"))
              name.flatMap( name => 
                desc.flatMap( desc =>
                  handle.flatMap( handle =>
                    args.map( args =>
                      ConstDynamic(
                        name, desc, handle, args
                      )
                    )
                  )
                )
              )
            case _ => Left(TypeCastFail(s"can't decode ConstDynamic, field 'ConstDynamic' must be JsObj in $json"))
          case None => Left(TypeCastFail(s"can't decode ConstDynamic, not found field 'ConstDynamic' in $json"))
      case _ => Left(TypeCastFail(s"can't decode ConstDynamic from $json"))
  }

// def fromJson(str:String) =
//   str.jsonAs[CBegin]