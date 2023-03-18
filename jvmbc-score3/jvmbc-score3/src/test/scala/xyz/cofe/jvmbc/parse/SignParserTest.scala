package xyz.cofe.jvmbc.parse

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.flatspec.AnyFlatSpec
import xyz.cofe.jvmbc.sparse.SPtr
import xyz.cofe.json4s3.derv.*
import xyz.cofe.json4s3.stream.ast.FormattingJson

class SignParserTest extends AnyFunSuite:
  /*
  public class SampleClass<Arg extends Number> {
      private int privateInt = 20;
      protected int protectedInt = 30;
      public int publicInt = 10;
      public final int publicFinalInt = 15;
      public String publicString = "value";
      public final List<String> listOfString = List.of("str");
      public final Map<Integer,List<Integer>> mapIntListOfInt = Map.of(
          1, List.of(10,11),
          2, List.of(20,21)
      );
      public SampleClass<AtomicInteger> intSelf;
      public SampleClass<? extends AtomicInteger> extIntSelf;
      public SampleClass<? super AtomicInteger> ext2IntSelf;
      public String concat(int a,Double b){
          System.out.println("concat of "+a+" "+b);
          return ""+a+""+b;
      }
      public <A extends Number & Runnable, B extends A> void some(A param, B param2){}
  }
  */

  // public final List<String> listOfString = List.of("str");
  val sample1 = "Ljava/util/List<Ljava/lang/String;>;"
  test("sample1") {
    println("sample1")
    println("="*40)
    val res = sign.SignParser.fieldSign(SPtr(sample1,0))
    res.foreach( (f,_) => {
      implicit val fmt = FormattingJson.pretty(true)
      println(f.json)
    })    
  }

  // public final Map<Integer,List<Integer>> mapIntListOfInt
  val sample2 = "Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Ljava/lang/Integer;>;>;"
  test("sample2"){
    println("sample2")
    println("="*40)
    val res = sign.SignParser.fieldSign(SPtr(sample2,0))
    res.foreach( (f,_) => {
      implicit val fmt = FormattingJson.pretty(true)
      println(f.json)
    })    
  }

  // public SampleClass<AtomicInteger> intSelf
  val sample3 = "Lxyz/cofe/jvmbc/SampleClass<Ljava/util/concurrent/atomic/AtomicInteger;>;"

  // public SampleClass<? extends AtomicInteger> extIntSelf
  val sample4 = "Lxyz/cofe/jvmbc/SampleClass<+Ljava/util/concurrent/atomic/AtomicInteger;>;"

  // public SampleClass<? super AtomicInteger> ext2IntSelf
  val sample5 = "Lxyz/cofe/jvmbc/SampleClass<-Ljava/util/concurrent/atomic/AtomicInteger;>;"

  // public <A extends Number & Runnable, B extends A> void some(A param, B param2){}
  val sample6 = "<A:Ljava/lang/Number;:Ljava/lang/Runnable;B:TA;>(TA;TB;)V"
  test("sample6"){
    println("sample6")
    println("="*40)
    val res = sign.SignParser.methodSign(SPtr(sample6,0))
    res.foreach( (f,_) => {
      implicit val fmt = FormattingJson.pretty(true)
      println(f.json)
    })    
  }

  val sample7 = "(Ljava/lang/Number;Ljava/lang/Number;)V"
  test("mdesc") {
    println("mdesc")
    println("="*40)
    println(
      desc.DescParser.method.apply(SPtr(sample7,0))
    )
  }

  test("id list") {
    println("id list")
    println("="*40)
    assert(
      sign.SignParser.idNameSepSlash(SPtr("name1/name2/name3",0)).map(_._1)
      == Right(List(
          sign.SignParser.Id("name1")
        , sign.SignParser.Id("name2")
        , sign.SignParser.Id("name3")
      ))
    )
  }
