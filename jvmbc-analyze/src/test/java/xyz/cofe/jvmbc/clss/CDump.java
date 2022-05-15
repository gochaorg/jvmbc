package xyz.cofe.jvmbc.clss;

import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.cls.CField;
import xyz.cofe.jvmbc.cls.CMethod;

public class CDump {
    public static void dump(ByteCode begin){
        if( begin==null )throw new IllegalArgumentException( "begin==null" );
        begin.walk().forEach( ts -> {
            if( ts.level>0 ){
                var pref = ts.toList().stream().limit(ts.level).map( b -> {
                    if( b instanceof CMethod ){
                        return CMethod.class.getSimpleName()+"#"+((CMethod) b).getName()+"()";
                    }else if( b instanceof CField ){
                        return CField.class.getSimpleName()+"#"+((CField)b).getName();
                    }
                    return b.getClass().getSimpleName();
                }).reduce("", (a,b)->a+"/"+b);
                System.out.print(pref);
            }
            System.out.println("/"+ts.node);
        });
    }
}
