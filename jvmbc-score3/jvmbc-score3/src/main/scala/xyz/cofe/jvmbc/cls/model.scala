package xyz.cofe.jvmbc
package cls

import ann.AnnCode
import mth.MethCode

sealed trait ClassCode extends ByteCode

/** аннотации прикрепленные к классу */
case class CAnnotation(desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends ClassCode

/** аннотации прикрепленные к классу */
case class CTypeAnnotation(typeRef:Int,typePath:String,desc:TDesc,visible:Boolean,annotations:Seq[AnnCode]) extends ClassCode

/**
 * имя исходного класса/файла отладки (debug)
 */
case class CSource(source:String,debug:String) extends ClassCode
case class CPermittedSubclass(permittedSubclass:String) extends ClassCode
case class COuterClass(owner:String,name:String,desc:TDesc) extends ClassCode
case class CNestMember(nestMember:String) extends ClassCode
case class CNestHost(nestHost:String) extends ClassCode

/**
 * Описывает поле класса
 * 
 * Пример
 * 
 *    public interface Desc {
 *        String value();
 *    }
 *    
 *    {@} Desc("sample User2")
 *    public class User2 {
 *        public User2(){}
 *        public User2(String name1){
 *            this.name = name1;
 *        }
 *    
 *        {@} Desc("name of user")
 *        private String name;
 *    
 *        {@} Desc("name of user")
 *        public String getName(){ return name; }
 *        public void setName( {@} Required {@} MaxLength(100) {@} MinLength(1) String name ){ this.name = name; }
 *    
 *        private List<String> emails;
 *        {@} Desc("emails of user")
 *        public List<String> getEmails(){ return emails; }
 *        public void setEmails( List<String> emails ){ this.emails = emails; }
 *    }
 * 
 *  Будет описываться как:
 * 
 *     CBegin version="55" ...
 *     CField
 *       name="name"
 *       access="2"
 *       accessDecode="[Private]"
 *       descriptor="Ljava/lang/String;"
 */
case class CField(access:Int,name:String,desc:TDesc,sign:Sign,value:AnyRef) extends ClassCode

/** Описывает метод класса */
case class CMethod(access:CMethodAccess,name:String,desc:MDesc,sign:MSign,exceptions:Seq[String],body:Seq[MethCode]) extends ClassCode
case class CMethodAccess(raw:Int)
case class MSign(raw:String)

case class CInnerClass(access:CInnerClassAccess,name:String,outerName:String,innerName:String) extends ClassCode
case class CInnerClassAccess(raw:Int)

/**
 * Описывает класс / модуль
 * @param version версия байт-кода
 * @param access флаги доступа
 * @param name имя (байт-код) класса
 * @param sign сигнатура, в случае Generic типа
 * @param superName имя (байт-код) класса родителя
 * @param interfaces имена (байт-код) интерфейсов
 * @param source имя исходного класса/файла отладки (debug)
 * @param nestHost 
 * @param annotations аннотации прикрепленные к классу
 * @param typeAnnotations аннотации прикрепленные к классу
 * @param nestMembers 
 * @param innerClasses 
 * @param fields список полней класса
 * @param methods список методов класса
 * @param order порядок определения полей/методов/..
 */
case class CBegin(
  version:CVersion,
  access:CBeginAccess,
  name:JavaName,
  sign:CSign,
  superName:JavaName,
  interfaces:Seq[String],
  source:CSource,
  nestHost:CNestHost,
  annotations:Seq[CAnnotation],
  typeAnnotations:Seq[CTypeAnnotation],
  nestMembers:Seq[CNestMember],
  innerClasses:Seq[CInnerClass],
  fields:Seq[CField],
  methods:Seq[CMethod],
  order:Map[ClassCode,Int]=Map()
) extends ClassCode
case class CSign(raw:String)
case class CVersion(raw:Int)

/**
 * Маркер конца класса.
 */
case class CEnd() extends ClassCode

/** Права доступа */
case class CBeginAccess(raw:Int)