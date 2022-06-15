package xyz.cofe.jvmbc;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;
import static xyz.cofe.jvmbc.AccFlag.Scope.*;

/**
 * Флаги
 */
public enum AccFlag {
    ABSTRACT     (ACC_ABSTRACT,      CLASS, METHOD,INNER_CLASS),
    ANNOTATION   (ACC_ANNOTATION,    CLASS,INNER_CLASS),
    DEPRECATED   (ACC_DEPRECATED,    CLASS, FIELD, METHOD),
    BRIDGE       (ACC_BRIDGE,        METHOD),
    ENUM         (ACC_ENUM,          CLASS, FIELD, INNER_CLASS),
    FINAL        (ACC_FINAL,         CLASS,FIELD,METHOD,PARAMETER,INNER_CLASS),
    INTERFACE    (ACC_INTERFACE,     CLASS,INNER_CLASS),
    MANDATED     (ACC_MANDATED,      FIELD, METHOD, PARAMETER, MoDULE),
    MODULE       (ACC_MODULE,        CLASS),
    NATIVE       (ACC_NATIVE,        METHOD),
    OPEN         (ACC_OPEN,          MoDULE),
    PUBLIC       (ACC_PUBLIC,        CLASS,FIELD,METHOD,INNER_CLASS),
    PRIVATE      (ACC_PRIVATE,       CLASS,FIELD,METHOD,INNER_CLASS),
    PROTECTED    (ACC_PROTECTED,     CLASS,FIELD,METHOD,INNER_CLASS),
    RECORD       (ACC_RECORD,        CLASS),
    STATIC       (ACC_STATIC,        FIELD,METHOD,INNER_CLASS),
    SUPER        (ACC_SUPER,         CLASS),
    SYNCHRONIZED (ACC_SYNCHRONIZED,  METHOD),
    STRICT       (ACC_STRICT,        METHOD),
    SYNTHETIC    (ACC_SYNTHETIC,     CLASS, FIELD, METHOD, PARAMETER, MoDULE,INNER_CLASS),
    STATIC_PHASE (ACC_STATIC_PHASE,  MODULE_REQUIRES),
    TRANSITIVE   (ACC_TRANSITIVE,    MODULE_REQUIRES),
    TRANSIENT    (ACC_TRANSIENT,     FIELD),
    VOLATILE     (ACC_VOLATILE,      FIELD),
    VARARGS      (ACC_VARARGS,       METHOD)
    ;
    public final int flag;
    public final Scope[] scopes;
    AccFlag( int flag, Scope ... scopes ){
        this.flag = flag;
        this.scopes = scopes;
    }

    public enum Scope {
        CLASS,
        FIELD,
        METHOD,
        PARAMETER,
        MODULE_REQUIRES,
        MoDULE,
        INNER_CLASS
    }

    private static boolean has( int flags, int flag ){
        return (flags & flag) == flag;
    }

    private static int set(int flags, int flag, boolean switchOn){
        return switchOn ? (flags | flag) : (flags & ~flag);
    }

    public static Set<AccFlag> flags( int flags, Scope scope ){
        if( scope==null )throw new IllegalArgumentException( "scope==null" );
        Set<AccFlag> set = new LinkedHashSet<>();
        for( var flag : AccFlag.values() ){
            if( has( flags, flag.flag ) ){
                set.add(flag);
            }
        }
        return set;
    }

    public static int flags( Set<AccFlag> flags ){
        if( flags==null )throw new IllegalArgumentException( "flags==null" );
        int value = 0;
        for( var flag : flags ){
            if( flag!=null ){
                value = set(value, flag.flag, true);
            }
        }
        return value;
    }
}
