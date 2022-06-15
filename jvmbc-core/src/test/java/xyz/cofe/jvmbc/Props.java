package xyz.cofe.jvmbc;

import xyz.cofe.fn.Fn1;
import xyz.cofe.jvmbc.cls.CMethod;
import xyz.cofe.jvmbc.fld.FieldEnd;
import xyz.cofe.jvmbc.fn.F0;
import xyz.cofe.jvmbc.fn.F1;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Props {
    public Props(){}
    public Props(Props sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        ignores = new ArrayList<>(sample.ignores);
        convertors = new ArrayList<>(sample.convertors);
        decoders = new HashMap<>(sample.decoders);
    }
    public Props clone(){
        return new Props(this);
    }
    public static class Prop {
        public final Object owner;
        public final String name;
        public final Type propType;
        public final Class<?> propClass;
        public final Object propValue;

        public Prop( Object owner, String name, Type propType, Class<?> propClass, Object propValue ){
            this.owner = owner;
            this.name = name;
            this.propType = propType;
            this.propClass = propClass;
            this.propValue = propValue;
        }

        public Prop( Prop sample ){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            this.owner = sample.owner;
            this.name = sample.name;
            this.propType = sample.propType;
            this.propClass = sample.propClass;
            this.propValue = sample.propValue;
        }

        public Prop clone(){
            return new Prop(this);
        }

        public Prop with(Object value, Class<?> valueType) {
            return new Prop(
                owner,
                name,
                valueType,
                valueType,
                value
            );
        }
    }

    private List<Predicate<Prop>> ignores = new ArrayList<>();

    public Props ignore( Predicate<Prop> prop ){
        if( prop==null )throw new IllegalArgumentException( "prop==null" );
        ignores.add(prop);
        return this;
    }
    public Props ignore( String propName ){
        if( propName==null )throw new IllegalArgumentException( "propName==null" );
        return ignore( p -> p.name.equals(propName) );
    }
    public Props ignore( Class<?> clsName, String ... propNames ){
        if( clsName==null )throw new IllegalArgumentException( "clsName==null" );
        if( propNames==null )throw new IllegalArgumentException( "propName==null" );
        var props = new HashSet<>(Arrays.asList(propNames));
        return ignore( p -> props.contains(p.name) && p.owner!=null && clsName.isAssignableFrom(p.owner.getClass()) );
    }

    private List<F1<Prop,Prop>> convertors = new ArrayList<>();
    public <A,B> Props conv( Class<A> propType, F1<A,B> conv ) {
        convertors.add( prop -> {
            if( propType.isAssignableFrom(prop.propClass) ){
                var resValue = conv.apply((A)prop.propValue);
                if( resValue==null )throw new IllegalStateException( "resValue==null" );
                return prop.with(resValue, resValue.getClass());
            }
            return prop;
        });
        return this;
    }
    public Props conv( F1<Prop,Prop> convertor ) {
        if( convertor==null )throw new IllegalArgumentException( "convertor==null" );
        convertors.add(convertor);
        return this;
    }

    private HashMap<Class<?>, Fn1<Object,List<Prop>>> decoders = new HashMap<>();
    public List<Prop> decode(Object someValue){
        if( someValue==null )return List.of();
        var cls = someValue.getClass();
        var decodeFn = decoders.get(cls);
        if( decodeFn!=null ){
            return decodeFn.apply(someValue);
        }
        var lst = new ArrayList<Prop>();
        try{
            var bi = Introspector.getBeanInfo(cls);
            for( var prop : bi.getPropertyDescriptors() ){
                try{
                    var retVal = prop.getReadMethod().invoke(someValue);
                    var retClass = prop.getReadMethod().getReturnType();
                    var retType = prop.getReadMethod().getGenericReturnType();
                    if( retVal==null )continue;
                    if( retVal instanceof Optional && ((Optional)retVal).isEmpty() )continue;
                    if( retVal instanceof Collection && ((Collection)retVal).isEmpty() )continue;

                    Prop p = new Prop(someValue, prop.getName(), retType, retClass, retVal);
                    if( ignores.stream().map(pred -> pred.test(p)).reduce(false, (a,b) -> a || b) )continue;

                    lst.add(convertors.stream().reduce( p, (a,p0)->p0.apply(a), (a,b) -> b ));
                } catch( IllegalAccessException | InvocationTargetException e ){
                    e.printStackTrace();
                }
            }
        } catch( IntrospectionException e ){
            e.printStackTrace();
            return lst;
        }
        return lst;
    }

    public static Props defaults(){
        return new Props()
            .ignore("class")
            .ignore(CMethod.class,"methodByteCodes")
            .ignore(prop -> prop.propValue instanceof List && ((List)prop.propValue).size()==1 && ((List)prop.propValue).get(0) instanceof FieldEnd)
            .conv(Optional.class, opt -> opt.isEmpty() ? Optional.empty() : opt.get())
            .conv(TDesc.class,
                td -> td.tryGet().map(TypeDesc::toString).orElse(td.getRaw())
            )
            .conv(Sign.class, s -> s.raw);
    }
}
