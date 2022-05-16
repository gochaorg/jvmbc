package xyz.cofe.jvmbc;

import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.jvmbc.cls.CField;
import xyz.cofe.jvmbc.cls.CMethod;
import xyz.cofe.jvmbc.mth.MethodByteCode;

import java.util.List;

public class MyCBegin<
    CFIELD extends CField,
    CMETHOD extends CMethod<CM_LIST>,
    CM_LIST extends List<MethodByteCode>
    > extends CBegin<CFIELD, CMETHOD, CM_LIST> {
    public MyCBegin(){
        super();
    }

    public MyCBegin( int version, int access, String name, String signature, String superName, String[] interfaces ){
        super(version, access, name, signature, superName, interfaces);
    }

    public MyCBegin( CBegin<CFIELD, CMETHOD, CM_LIST> sample ){
        super(sample);
    }

    @Override
    public CBegin<CFIELD, CMETHOD, CM_LIST> clone(){
        return new MyCBegin<>(this);
    }
}
