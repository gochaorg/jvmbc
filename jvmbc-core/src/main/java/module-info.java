module xyz.cofe.jvmbc.core {
    requires org.objectweb.asm;
    requires org.slf4j;

    exports xyz.cofe.jvmbc;
    exports xyz.cofe.jvmbc.ann;
    exports xyz.cofe.jvmbc.bm;
    exports xyz.cofe.jvmbc.cls;
    exports xyz.cofe.jvmbc.fld;
    exports xyz.cofe.jvmbc.mth;
    //exports xyz.cofe.jvmbc.prop;
    exports xyz.cofe.jvmbc.tree;
    exports xyz.cofe.jvmbc.fn;
    exports xyz.cofe.jvmbc.rec;
}