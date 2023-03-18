package xyz.cofe.jvmbc.parse.sign

import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.AST

/** AST дерево описания типа */
//sealed trait Asts

sealed trait JavaTypeSignature extends Result

sealed trait BaseType  extends JavaTypeSignature
case object ByteType   extends BaseType //B
case object CharType   extends BaseType //C
case object DoubleType extends BaseType //D
case object FloatType  extends BaseType //F
case object IntType    extends BaseType //I
case object LongType   extends BaseType //J
case object ShortType  extends BaseType //S
case object BoolType   extends BaseType //Z

sealed trait ReferenceTypeSignature 
  extends JavaTypeSignature 
  with FieldSignature 
  with InterfaceBound

object ReferenceTypeSignature:
  given ToJson[ReferenceTypeSignature] with
    override def toJson(t: ReferenceTypeSignature): Option[AST] = 
      t match
        case v:ArrayTypeSignature => summon[ToJson[ArrayTypeSignature]].toJson(v)
        case v:ClassTypeSignature => summon[ToJson[ClassTypeSignature]].toJson(v)
        case v:TypeVariableSignature => summon[ToJson[TypeVariableSignature]].toJson(v)

case class ClassTypeSignature(
  pkg:List[PackageSpecifier],
  cls:SimpleClassTypeSignature,
  suff:List[ClassTypeSignatureSuffix]
) extends ReferenceTypeSignature 
  with ThrowsSignature 
  with SuperclassSignature 
  with SuperinterfaceSignature  

case class PackageSpecifier(name:String)
case class SimpleClassTypeSignature(
  name:String, 
  typeArgs:Option[TypeArguments])

case class TypeArguments(
  head:TypeArgument,
  tail:List[TypeArgument])

sealed trait TypeArgument
case object TypeArgumentAny extends TypeArgument
case class TypeArgumentRef(
  wildcard:Option[WildcardIndicator], 
  refType:ReferenceTypeSignature) extends TypeArgument

sealed trait WildcardIndicator
case object WildcardPlus extends WildcardIndicator
case object WildcardMinus extends WildcardIndicator

case class ClassTypeSignatureSuffix( simpleClTySign: SimpleClassTypeSignature )

case class TypeVariableSignature(name:String) 
  extends ReferenceTypeSignature 
  with ThrowsSignature

case class ArrayTypeSignature( javaTypeSign: JavaTypeSignature ) 
  extends ReferenceTypeSignature

case class ClassSignature(
  typeParams:Option[TypeParameters],
  superClsSign:SuperclassSignature,
  superItfSign:List[SuperinterfaceSignature]
)

case class TypeParameters( head:TypeParameter, tail:List[TypeParameter] )

case class TypeParameter(name:String, classBound:ClassBound, itfBound:List[InterfaceBound])

case class ClassBound( refTypeSign:Option[ReferenceTypeSignature] )

sealed trait InterfaceBound

sealed trait SuperclassSignature

sealed trait SuperinterfaceSignature

/**
  * Описывает тип/сигнатуру метода
  * 
  * Пример
  * 
  * `public &lt;A extends Number &amp; Runnable, B extends A> void some(A param, B param2){}`
  * 
  * В результате будет примерно такая структура если парсинг через `SignParser.methodSign:Pattern[MethodSignature`
  * 
  * <pre>
  *  {
  *  "typeParams":{
  *    "head":{
  *      "name":"A",
  *      "classBound":{
  *        "refTypeSign":{
  *          "pkg":[
  *            {
  *              "name":"java"
  *            },
  *            {
  *              "name":"lang"
  *            }
  *          ],
  *          "cls":{
  *            "name":"Number"
  *          },
  *          "suff":[]
  *        }
  *      },
  *      "itfBound":[
  *        {
  *          "ReferenceTypeSignature":{
  *            "pkg":[
  *              {
  *                "name":"java"
  *              },
  *              {
  *                "name":"lang"
  *              }
  *            ],
  *            "cls":{
  *              "name":"Runnable"
  *            },
  *            "suff":[]
  *          }
  *        }
  *      ]
  *    },
  *    "tail":[
  *      {
  *        "name":"B",
  *        "classBound":{
  *          "refTypeSign":{
  *            "name":"A"
  *          }
  *        },
  *        "itfBound":[]
  *      }
  *    ]
  *  },
  *  "args":[
  *    {
  *      "ReferenceTypeSignature":{
  *        "name":"A"
  *      }
  *    },
  *    {
  *      "ReferenceTypeSignature":{
  *        "name":"B"
  *      }
  *    }
  *  ],
  *  "res":{
  *    "VoidDescriptor":{}
  *  },
  *  "throws":[]
  *}
  *</pre>
  *
  * @param typeParams Generic параметры метода
  * @param args Аргументы метода
  * @param res результат
  * @param throws генерируемые исключения
  */
case class MethodSignature( 
  typeParams:Option[TypeParameters],
  args:List[JavaTypeSignature],
  res:Result,
  throws:List[ThrowsSignature]
)

sealed trait Result
case object VoidDescriptor extends Result

sealed trait ThrowsSignature

sealed trait FieldSignature