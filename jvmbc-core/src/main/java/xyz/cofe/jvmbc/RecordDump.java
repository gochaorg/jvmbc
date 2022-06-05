package xyz.cofe.jvmbc;

import org.objectweb.asm.*;
import xyz.cofe.jvmbc.rec.RecAnnotation;
import xyz.cofe.jvmbc.rec.RecEnd;
import xyz.cofe.jvmbc.rec.RecTypeAnnotation;

import java.util.function.Consumer;

public class RecordDump extends RecordComponentVisitor {
    public RecordDump( int api ){
        super(api);
    }

    public RecordDump( int api, RecordComponentVisitor recordComponentVisitor ){
        super(api, recordComponentVisitor);
    }

    private void dump( String message, Object...args){
        if( message==null )return;
        if( args==null || args.length==0 ){
            System.out.println(message);
        }else{
            System.out.print(message);
            for( var a : args ){
                System.out.print(" ");
                System.out.print(a);
            }
            System.out.println();
        }
    }

    private Consumer<? super ByteCode> byteCodeConsumer;

    /**
     * Указывает функцию принимающую байт код
     * @param bc функция приема байт кода
     * @return SELF ссылка
     */
    public RecordDump byteCode(Consumer<? super ByteCode> bc){
        byteCodeConsumer = bc;
        return this;
    }

    protected void emit(ByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        var c = byteCodeConsumer;
        if( c!=null ){
            c.accept(bc);
        }
    }

    /**
     * Visits an annotation of the record component.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     *     interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitAnnotation( String descriptor, boolean visible ){
        var ra = new RecAnnotation(descriptor, visible);
        var adump = new AnnotationDump(api);
        adump.byteCode(byteCodeConsumer, ra);
        emit(ra);
        return adump;
    }

    /**
     * Visits an annotation on a type in the record component signature.
     *
     * @param typeRef a reference to the annotated type. The sort of this type reference must be
     *     {@link TypeReference#CLASS_TYPE_PARAMETER}, {@link
     *     TypeReference#CLASS_TYPE_PARAMETER_BOUND} or {@link TypeReference#CLASS_EXTENDS}. See
     *     {@link TypeReference}.
     * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
     *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
     *     'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
     *     interested in visiting this annotation.
     */
    @Override
    public AnnotationVisitor visitTypeAnnotation( int typeRef, TypePath typePath, String descriptor, boolean visible ){
        var ra = new RecTypeAnnotation(
            typeRef,
            typePath!=null ? typePath.toString() : null,
            descriptor,
            visible);
        var adump = new AnnotationDump(api);
        adump.byteCode(byteCodeConsumer, ra);
        emit(ra);
        return adump;
    }

    /**
     * Visits a non standard attribute of the record component.
     *
     * @param attribute an attribute.
     */
    @Override
    public void visitAttribute( Attribute attribute ){
    }

    /**
     * Visits the end of the record component. This method, which is the last one to be called, is
     * used to inform the visitor that everything have been visited.
     */
    @Override
    public void visitEnd(){
        var r = new RecEnd();
        emit(r);
    }
}
