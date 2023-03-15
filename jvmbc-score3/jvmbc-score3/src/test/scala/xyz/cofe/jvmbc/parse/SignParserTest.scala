package xyz.cofe.jvmbc.parse

import org.scalatest.funsuite.AnyFunSuite
import xyz.cofe.jvmbc.sparse.SPtr
import org.scalatest.flatspec.AnyFlatSpec

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

  // public final Map<Integer,List<Integer>> mapIntListOfInt
  val sample2 = "Ljava/util/Map<Ljava/lang/Integer;Ljava/util/List<Ljava/lang/Integer;>;>;"

  // public SampleClass<AtomicInteger> intSelf
  val sample3 = "Lxyz/cofe/jvmbc/SampleClass<Ljava/util/concurrent/atomic/AtomicInteger;>;"

  // public SampleClass<? extends AtomicInteger> extIntSelf
  val sample4 = "Lxyz/cofe/jvmbc/SampleClass<+Ljava/util/concurrent/atomic/AtomicInteger;>;"

  // public SampleClass<? super AtomicInteger> ext2IntSelf
  val sample5 = "Lxyz/cofe/jvmbc/SampleClass<-Ljava/util/concurrent/atomic/AtomicInteger;>;"

  // public <A extends Number & Runnable, B extends A> void some(A param, B param2){}
  val sample6 = "<A:Ljava/lang/Number;:Ljava/lang/Runnable;B:TA;>(TA;TB;)V"

  val sample7 = "(Ljava/lang/Number;Ljava/lang/Number;)V"

  test("mdesc") {
    println(
      desc.DescParser.method.apply(SPtr(sample7,0))
    )
  }
