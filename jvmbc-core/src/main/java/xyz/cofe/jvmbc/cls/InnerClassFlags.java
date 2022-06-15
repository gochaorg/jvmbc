package xyz.cofe.jvmbc.cls;

import xyz.cofe.jvmbc.AccFlags;
import xyz.cofe.jvmbc.AccFlagsProperty;
import xyz.cofe.jvmbc.cls.ClassFlags;

public interface InnerClassFlags extends AccFlagsProperty {
    //region public : boolean
    default boolean isPublic(){
        return new AccFlags(getAccess()).isPublic();
    }
    default void setPublic(boolean v){
        setAccess( new AccFlags(getAccess()).withPublic(v).value() );
    }
    //endregion
    //region private : boolean
    default boolean isPrivate(){
        return new AccFlags(getAccess()).isPublic();
    }
    default void setPrivate(boolean v){
        setAccess( new AccFlags(getAccess()).withPublic(v).value() );
    }
    //endregion
    //region protected : boolean
    default boolean isProtected(){
        return new AccFlags(getAccess()).isProtected();
    }
    default void setProtected(boolean v){
        setAccess( new AccFlags(getAccess()).withProtected(v).value() );
    }
    //endregion
    //region static : boolean
    default boolean isStatic(){
        return new AccFlags(getAccess()).isStatic();
    }
    default void setStatic(boolean v){
        setAccess( new AccFlags(getAccess()).withStatic(v).value() );
    }
    //endregion
    //region final : boolean
    default boolean isFinal(){
        return new AccFlags(getAccess()).isFinal();
    }
    default void setFinal(boolean v){
        setAccess( new AccFlags(getAccess()).withFinal(v).value() );
    }
    //endregion
    //region interface : boolean
    default boolean isInterface(){
        return new AccFlags(getAccess()).isInterface();
    }
    default void setInterface(boolean v){
        setAccess( new AccFlags(getAccess()).withInterface(v).value() );
    }
    //endregion
    //region abstract : boolean
    default boolean isAbstract(){
        return new AccFlags(getAccess()).isAbstract();
    }
    default void setAbstract(boolean v){
        setAccess( new AccFlags(getAccess()).withAbstract(v).value() );
    }
    //endregion
    //region synthetic : boolean
    default boolean isSynthetic(){
        return new AccFlags(getAccess()).isSynthetic();
    }
    default void setSynthetic(boolean v){
        setAccess( new AccFlags(getAccess()).withSynthetic(v).value() );
    }
    //endregion
    //region annotation : boolean
    default boolean isAnnotation(){
        return new AccFlags(getAccess()).isAnnotation();
    }
    default void setAnnotation(boolean v){
        setAccess( new AccFlags(getAccess()).withAnnotation(v).value() );
    }
    //endregion
    //region enum : boolean
    default boolean isEnum(){
        return new AccFlags(getAccess()).isEnum();
    }
    default void setEnum(boolean v){
        setAccess( new AccFlags(getAccess()).withEnum(v).value() );
    }
    //endregion

}
