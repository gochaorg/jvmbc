open module xyz.cofe.jvmbc.core {
    requires org.objectweb.asm;
    requires org.slf4j;
    requires org.checkerframework.checker.qual;
    requires java.desktop;

    //exports xyz.cofe.jvmbc.prop;

    exports xyz.cofe.jvmbc;
    exports xyz.cofe.jvmbc.ann;
    exports xyz.cofe.jvmbc.mth;
    exports xyz.cofe.jvmbc.mth.bm;
    exports xyz.cofe.jvmbc.cls;
    exports xyz.cofe.jvmbc.fld;
    exports xyz.cofe.jvmbc.tree;
    exports xyz.cofe.jvmbc.fn;
    exports xyz.cofe.jvmbc.rec;
    exports xyz.cofe.jvmbc.mdl;
}