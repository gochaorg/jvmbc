package xyz.cofe.jvmbc.cls;

import xyz.cofe.jvmbc.AccFlag;
import xyz.cofe.jvmbc.AccFlagsProperty;

import java.util.Set;

public interface ModuleFlags extends AccFlagsProperty {
    //region flags : Set<AccFlag>
    default Set<AccFlag> getFlags(){ return AccFlag.flags(getAccess(), AccFlag.Scope.MoDULE); }
    default void setFlags(Set<AccFlag> flags){
        if( flags==null )throw new IllegalArgumentException( "flags==null" );
        setAccess( AccFlag.flags(flags) );
    }
    //endregion
}
