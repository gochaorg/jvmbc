package xyz.cofe.jvmbc

/** Имя класса */
case class JavaName(raw:String)

/** Сигнатура типа с Generic */
case class Sign(raw:String)

/** Сигнатура типа */
case class TDesc( raw:String )

/** Сигнатура метода типа */
case class MDesc(raw:String)

/** 
Маркер модели байт-кода 

В состав входит
- [[xyz.cofe.jmvbc.ann.AnnCode]] Байт-код аннотаций
- [[xyz.cofe.jmvbc.cls.ClassCode]] Байт-код классов
- [[xyz.cofe.jmvbc.ann.FieldCode]] Байт-код полей
- [[xyz.cofe.jmvbc.ann.MethCode]] Байт-код методов
 */
trait ByteCode