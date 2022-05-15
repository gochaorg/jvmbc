package xyz.cofe.jvmbc.cls;

import xyz.cofe.jvmbc.mth.MethodByteCode;

import java.util.List;

public interface ClassFactory<
    CBEGIN extends CBegin<CFIELD,CMETHOD>,
    CFIELD extends CField,
    CMETHOD extends CMethod<List<MethodByteCode>>
> {
    CBEGIN cbegin(int version, int access, String name, String signature, String superName, String[] interfaces);
    CFIELD cfield(int access, String name, String descriptor, String signature, Object value);

    public static class Default implements ClassFactory<
        CBegin<CField,CMethod<List<MethodByteCode>>>,
        CField,
        CMethod<List<MethodByteCode>>
    > {
        @Override
        public CBegin<CField,CMethod<List<MethodByteCode>>> cbegin(
            int version, int access, String name, String signature, String superName, String[] interfaces
        ){
            return new CBegin<>(version,access,name,signature,superName,interfaces);
        }

        @Override
        public CField cfield( int access, String name, String descriptor, String signature, Object value ){
            return new CField( access, name, descriptor, signature, value );
        }
    }
}
