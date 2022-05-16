package xyz.cofe.jvmbc.cls;

import xyz.cofe.jvmbc.fn.F1;
import xyz.cofe.jvmbc.fn.F5;
import xyz.cofe.jvmbc.fn.F6;
import xyz.cofe.jvmbc.mth.MethodByteCode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Фабрика для парсинга байт кода
 *
 * @param <CBEGIN>  тип представления класса
 * @param <CFIELD>  тип представления поля класса
 * @param <CMETHOD> тип представления метода класса
 * @param <CM_LIST> тип представления контейнера байт-кода метода класса
 */
public interface ClassFactory<
    CBEGIN extends CBegin<CFIELD, CMETHOD, CM_LIST>,
    CFIELD extends CField,
    CMETHOD extends CMethod<CM_LIST>,
    CM_LIST extends List<MethodByteCode>
    > {
    /**
     * Создание CBEGIN
     *
     * @param version    Аргументы {@link CBegin}
     * @param access     Аргументы {@link CBegin}
     * @param name       Аргументы {@link CBegin}
     * @param signature  Аргументы {@link CBegin}
     * @param superName  Аргументы {@link CBegin}
     * @param interfaces Аргументы {@link CBegin}
     * @return Экземпляр {@link CBegin}
     */
    CBEGIN cbegin( int version, int access, String name, String signature, String superName, String[] interfaces );

    CFIELD cfield( int access, String name, String descriptor, String signature, Object value );

    EmptyList<CM_LIST, MethodByteCode> methodList();

    CMETHOD cmethod( int access, String name, String descriptor, String signature, String[] exceptions );

    CMETHOD cmethod();

    /**
     * Фабрика по умолчанию
     */
    public static class Default implements ClassFactory<
        CBegin<CField, CMethod<List<MethodByteCode>>, List<MethodByteCode>>,
        CField,
        CMethod<List<MethodByteCode>>,
        List<MethodByteCode>
        > {
        @Override
        public CBegin<CField, CMethod<List<MethodByteCode>>, List<MethodByteCode>> cbegin(
            int version, int access, String name, String signature, String superName, String[] interfaces
        ){
            return new CBegin<>(version, access, name, signature, superName, interfaces);
        }

        @Override
        public CField cfield( int access, String name, String descriptor, String signature, Object value ){
            return new CField(access, name, descriptor, signature, value);
        }

        @Override
        public EmptyList<List<MethodByteCode>, MethodByteCode> methodList(){
            return ArrayList::new;
        }

        @Override
        public CMethod<List<MethodByteCode>> cmethod( int access, String name, String descriptor, String signature, String[] exceptions ){
            return new CMethod<>(methodList(), access, name, descriptor, signature, exceptions);
        }

        @Override
        public CMethod<List<MethodByteCode>> cmethod(){
            return new CMethod<>(methodList());
        }
    }

    default CBEGIN parseByteCode( byte[] byteCode ){
        if( byteCode == null ) throw new IllegalArgumentException("byteCode==null");
        return CBEGIN.parseByteCode(byteCode, this);
    }

    /**
     * Создание фабрики
     *
     * @param newEmptyContainer создание пустого контейнера
     * @param <CM_LIST>         Тип контейнера
     * @return Создание фабрики
     */
    static <CM_LIST extends List<MethodByteCode>> Builder0<CM_LIST> create( EmptyList<CM_LIST, MethodByteCode> newEmptyContainer ){
        if( newEmptyContainer == null ) throw new IllegalArgumentException("newEmptyContainer==null");
        return new Builder0<>(newEmptyContainer);
    }

    /**
     * Создание фабрики
     *
     * @param <CM_LIST> тип представления контейнера байт-кода метода класса
     */
    public static class Builder0<CM_LIST extends List<MethodByteCode>> {
        public final EmptyList<CM_LIST, MethodByteCode> emptyList;

        public Builder0( EmptyList<CM_LIST, MethodByteCode> emptyList ){
            if( emptyList == null ) throw new IllegalArgumentException("emptyList==null");
            this.emptyList = emptyList;
        }

        public Builder1<
            CBegin<CField, CMethod<CM_LIST>, CM_LIST>,
            CField,
            CMethod<CM_LIST>,
            CM_LIST
            > defaults(){
            F6<Integer,Integer,String,String,String,String[],CBegin<CField, CMethod<CM_LIST>, CM_LIST>> cbegin =
                CBegin::new;

            F5<Integer,String,String,String,Object,CField> cfield =
                CField::new;

            F6<EmptyList<CM_LIST, MethodByteCode>,Integer,String,String,String,String[], CMethod<CM_LIST>> cmethod =
                CMethod::new;

            F1<EmptyList<CM_LIST, MethodByteCode>, CMethod<CM_LIST>> cmethodDefault =
                CMethod::new;

            return new Builder1<>(this, cbegin, cfield, cmethod, cmethodDefault);
        }
    }

    /**
     * Создание фабрики
     *
     * @param <CBEGIN>  тип представления класса
     * @param <CFIELD>  тип представления поля класса
     * @param <CMETHOD> тип представления метода класса
     * @param <CM_LIST> тип представления контейнера байт-кода метода класса
     */
    public static class Builder1
        <
            CBEGIN extends CBegin<CFIELD, CMETHOD, CM_LIST>,
            CFIELD extends CField,
            CMETHOD extends CMethod<CM_LIST>,
            CM_LIST extends List<MethodByteCode>
            > {
        public final Builder0<CM_LIST> builder0;
        public final F6<Integer,Integer,String,String,String,String[],CBEGIN> cbegin;
        public final F5<Integer,String,String,String,Object,CFIELD> cfield;
        public final F6<EmptyList<CM_LIST, MethodByteCode>,Integer,String,String,String,String[], CMETHOD> cmethod;
        public final F1<EmptyList<CM_LIST, MethodByteCode>, CMETHOD> cmethodDefault;

        public Builder1(
            Builder0<CM_LIST> builder0,
            F6<Integer,Integer,String,String,String,String[],CBEGIN> cbegin,
            F5<Integer,String,String,String,Object,CFIELD> cfield,
            F6<EmptyList<CM_LIST, MethodByteCode>,Integer,String,String,String,String[], CMETHOD> cmethod,
            F1<EmptyList<CM_LIST, MethodByteCode>, CMETHOD> cmethodDefault
        ){
            if( builder0 == null ) throw new IllegalArgumentException("builder0==null");
            if( cbegin == null )throw new IllegalArgumentException( "cbegin==null" );
            if( cfield == null )throw new IllegalArgumentException( "cfield == null" );
            if( cmethod==null )throw new IllegalArgumentException( "cmethod==null" );
            if( cmethodDefault==null )throw new IllegalArgumentException( "cmethodDefault==null" );
            this.builder0 = builder0;
            this.cbegin = cbegin;
            this.cfield = cfield;
            this.cmethod = cmethod;
            this.cmethodDefault = cmethodDefault;
        }

        public Builder1( Builder1<CBEGIN, CFIELD, CMETHOD, CM_LIST> sample ){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            builder0 = sample.builder0;
            cbegin = sample.cbegin;
            cfield = sample.cfield;
            cmethod = sample.cmethod;
            cmethodDefault = sample.cmethodDefault;
        }

        /**
         * Указывает как создавать CBegin
         * @param cbegin вызов конструктора {@link CBegin}, см {@link ClassFactory#cbegin(int, int, String, String, String, String[])}
         * @param <X_CBEGIN> Тип {@link CBegin}
         * @return конструктур фабрики
         */
        public<X_CBEGIN extends CBegin<CFIELD, CMETHOD, CM_LIST>>
        Builder1<X_CBEGIN, CFIELD, CMETHOD, CM_LIST> cbegin(
            F6<Integer,Integer,String,String,String,String[],X_CBEGIN> cbegin
        )
        {
            return new Builder1<>(builder0, cbegin, cfield, cmethod, cmethodDefault);
        }

        public <X_CBEGIN extends CBegin<X_CFIELD, CMETHOD, CM_LIST>, X_CFIELD extends CField>
        Builder1<X_CBEGIN, X_CFIELD, CMETHOD, CM_LIST> cfield(
            F5<Integer,String,String,String,Object,X_CFIELD> cfield
        ) {
            return new Builder1(builder0, cbegin, cfield, cmethod, cmethodDefault);
        }

        public <
            X_CBEGIN extends CBegin<CFIELD, X_CMETHOD, X_CM_LIST>,
            X_CMETHOD extends CMethod<X_CM_LIST>,
            X_CM_LIST extends List<MethodByteCode>
            >
        Builder1<X_CBEGIN, CFIELD, X_CMETHOD, X_CM_LIST> cmethod(
            F6<EmptyList<CM_LIST, MethodByteCode>,Integer,String,String,String,String[], X_CM_LIST> cmethod
        ) {
            return new Builder1(builder0, cbegin, cfield, cmethod, cmethodDefault);
        }

        public <
            X_CBEGIN extends CBegin<CFIELD, X_CMETHOD, X_CM_LIST>,
            X_CMETHOD extends CMethod<X_CM_LIST>,
            X_CM_LIST extends List<MethodByteCode>
            >
        Builder1<X_CBEGIN, CFIELD, X_CMETHOD, X_CM_LIST> cmethod(
            F1<EmptyList<CM_LIST, MethodByteCode>,X_CM_LIST> cmethodDefault
        ) {
            return new Builder1(builder0, cbegin, cfield, cmethod, cmethodDefault);
        }

        public ClassFactory<CBEGIN,CFIELD,CMETHOD,CM_LIST> build(){
            return new ClassFactory<CBEGIN, CFIELD, CMETHOD, CM_LIST>() {
                @Override
                public CBEGIN cbegin( int version, int access, String name, String signature, String superName, String[] interfaces ){
                    return cbegin.apply(version,access,name,signature,superName,interfaces);
                }

                @Override
                public CFIELD cfield( int access, String name, String descriptor, String signature, Object value ){
                    return cfield.apply(access,name,descriptor,signature,value);
                }

                @Override
                public EmptyList<CM_LIST, MethodByteCode> methodList(){
                    return builder0.emptyList;
                }

                @Override
                public CMETHOD cmethod( int access, String name, String descriptor, String signature, String[] exceptions ){
                    return cmethod.apply(builder0.emptyList,access,name,descriptor,signature,exceptions);
                }

                @Override
                public CMETHOD cmethod(){
                    return cmethodDefault.apply(builder0.emptyList);
                }
            };
        }
    }
}
