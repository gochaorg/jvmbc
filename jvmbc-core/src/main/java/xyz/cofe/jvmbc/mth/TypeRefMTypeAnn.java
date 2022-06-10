package xyz.cofe.jvmbc.mth;

import xyz.cofe.jvmbc.TypeRef;
import xyz.cofe.jvmbc.TypeRefProperty;

public interface TypeRefMTypeAnn extends TypeRefProperty {
    public default boolean typeRefIsMethodTypeParameter(){
        return getTypeRef()== TypeRef.METHOD_TYPE_PARAMETER.code;
    }
    public default boolean typeRefIsMethodTypeParameterBound(){
        return getTypeRef()== TypeRef.METHOD_TYPE_PARAMETER_BOUND.code;
    }
    public default boolean typeRefIsMethodReturn(){
        return getTypeRef()== TypeRef.METHOD_RETURN.code;
    }
    public default boolean typeRefIsMethodReceiver(){
        return getTypeRef()== TypeRef.METHOD_RECEIVER.code;
    }
    public default boolean typeRefIsMethodFormalParameter(){
        return getTypeRef()== TypeRef.METHOD_FORMAL_PARAMETER.code;
    }
    public default boolean typeRefIsThrows(){
        return getTypeRef()== TypeRef.THROWS.code;
    }
}
