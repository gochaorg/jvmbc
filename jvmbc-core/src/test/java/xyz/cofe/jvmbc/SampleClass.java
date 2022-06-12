package xyz.cofe.jvmbc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    public int exceptSample(int a) {
        if( a<0 )throw new IllegalArgumentException("a<0");
        return a*a;
    }

    public static class InnerErr extends Throwable {
        /**
         * Constructs a new throwable with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * <p>The {@link #fillInStackTrace()} method is called to initialize
         * the stack trace data in the newly created throwable.
         */
        public InnerErr(){
        }

        /**
         * Constructs a new throwable with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * <p>The {@link #fillInStackTrace()} method is called to initialize
         * the stack trace data in the newly created throwable.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public InnerErr( String message ){
            super(message);
        }

        /**
         * Constructs a new throwable with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this throwable's detail message.
         *
         * <p>The {@link #fillInStackTrace()} method is called to initialize
         * the stack trace data in the newly created throwable.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A {@code null} value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public InnerErr( String message, Throwable cause ){
            super(message, cause);
        }
    }
    public int exceptCatch(int a,int b) throws InnerErr {
        try {
            return exceptSample(a);
        } catch( IllegalArgumentException e ){
            throw new InnerErr("aaa",e);
        }
    }
}
