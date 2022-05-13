module xyz.cofe.jvmbc.core {
    requires org.objectweb.asm;
    requires xyz.cofe.io.fn;
    //requires xyz.cofe.ecolls;
    requires org.slf4j;

    exports xyz.cofe.jvmbc;
    exports xyz.cofe.jvmbc.ann;
    exports xyz.cofe.jvmbc.bm;
    exports xyz.cofe.jvmbc.cls;
    exports xyz.cofe.jvmbc.fld;
    exports xyz.cofe.jvmbc.mth;
}